package com.iemes.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Reconstructed manual ERP-order and delivery ledgers; no external ERP or stock mutation. */
@Service
public class PlanLedgerService {
	private static final int PAGE_SIZE = 25;
	private static final BigDecimal MAX_QUANTITY = new BigDecimal("999999999999999.999");
	private final JdbcTemplate jdbc;

	@Inject
	public PlanLedgerService(DataSource dataSource) {
		this.jdbc = new JdbcTemplate(dataSource);
	}

	public Map<String, Object> listErpOrders(String site, String search, int requestedPage) {
		String currentSite = required(site, 255, "站点无效");
		String query = optional(search, 80);
		String pattern = "%" + (query == null ? "" : query) + "%";
		int page = page(requestedPage);
		Integer total = jdbc.queryForObject(
				"SELECT COUNT(*) FROM ly_erp_order WHERE site=? AND erp_order_no LIKE ?",
				new Object[] { currentSite, pattern }, Integer.class);
		List<Map<String, Object>> rows = jdbc.queryForList(
				"SELECT e.id, e.erp_order_no AS erpOrderNo, e.customer_name AS customerName, "
				+ "e.item_no AS itemNo, e.ordered_quantity AS orderedQuantity, "
				+ "DATE_FORMAT(e.expected_delivery_date, '%Y-%m-%d') AS expectedDeliveryDate, "
				+ "e.shoporder_id AS shoporderId, s.shoporder_no AS shoporderNo, e.note, "
				+ "e.created_by AS createdBy, DATE_FORMAT(e.created_at, '%Y-%m-%d %H:%i:%s') AS createdAt "
				+ "FROM ly_erp_order e LEFT JOIN ly_shoporder s ON s.id=e.shoporder_id AND s.site=e.site "
				+ "WHERE e.site=? AND e.erp_order_no LIKE ? ORDER BY e.id DESC LIMIT ? OFFSET ?",
				currentSite, pattern, PAGE_SIZE, (page - 1) * PAGE_SIZE);
		return pageResult(rows, total, page);
	}

	@Transactional
	public void addErpOrder(String site, String user, String orderNo, String customer,
			String itemNo, String quantity, String expectedDate, String shoporderId, String note) {
		String currentSite = required(site, 255, "站点无效");
		String externalNo = required(orderNo, 80, "ERP订单号不能为空或过长");
		String item = required(itemNo, 255, "物料编号不能为空或过长");
		BigDecimal amount = quantity(quantity);
		Long linkedOrder = optionalId(shoporderId);
		if (linkedOrder != null) {
			Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM ly_shoporder WHERE id=? AND site=?",
					new Object[] { linkedOrder, currentSite }, Integer.class);
			if (count == null || count.intValue() == 0) throw new IllegalArgumentException("工单不存在或不属于当前站点");
		}
		Date deliveryDate = null;
		if (expectedDate != null && !expectedDate.trim().isEmpty()) {
			try {
				deliveryDate = Date.valueOf(expectedDate.trim());
			} catch (IllegalArgumentException invalidDate) {
				throw new IllegalArgumentException("预计交付日期格式错误");
			}
		}
		jdbc.update("INSERT INTO ly_erp_order "
				+ "(site,erp_order_no,customer_name,item_no,ordered_quantity,expected_delivery_date,shoporder_id,note,created_by) "
				+ "VALUES (?,?,?,?,?,?,?,?,?)",
				currentSite, externalNo, optional(customer, 255), item, amount, deliveryDate,
				linkedOrder, optional(note, 500), required(user, 255, "用户无效"));
	}

	public List<Map<String, Object>> listShopOrders(String site) {
		return jdbc.queryForList("SELECT s.id, s.shoporder_no AS shoporderNo, "
				+ "s.shoporder_item AS itemNo, s.shoporder_numbers AS plannedQuantity, s.status, "
				+ "COALESCE(SUM(d.quantity),0) AS deliveredQuantity "
				+ "FROM ly_shoporder s LEFT JOIN ly_delivery_record d "
				+ "ON d.shoporder_id=s.id AND d.site=s.site "
				+ "WHERE s.site=? AND s.status<>6 "
				+ "GROUP BY s.id, s.shoporder_no, s.shoporder_item, s.shoporder_numbers, s.status "
				+ "ORDER BY s.id DESC", required(site, 255, "站点无效"));
	}

	public Map<String, Object> listDeliveries(String site, String shoporderId, int requestedPage) {
		String currentSite = required(site, 255, "站点无效");
		Long orderId = optionalId(shoporderId);
		String where = " WHERE d.site=?" + (orderId == null ? "" : " AND d.shoporder_id=?");
		Object[] countArgs = orderId == null ? new Object[] { currentSite } : new Object[] { currentSite, orderId };
		Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM ly_delivery_record d" + where, countArgs, Integer.class);
		int page = page(requestedPage);
		Object[] rowArgs = orderId == null
				? new Object[] { currentSite, PAGE_SIZE, (page - 1) * PAGE_SIZE }
				: new Object[] { currentSite, orderId, PAGE_SIZE, (page - 1) * PAGE_SIZE };
		List<Map<String, Object>> rows = jdbc.queryForList(
				"SELECT d.id, d.shoporder_id AS shoporderId, s.shoporder_no AS shoporderNo, "
				+ "d.quantity, d.reference_no AS referenceNo, d.note, d.created_by AS createdBy, "
				+ "DATE_FORMAT(d.delivered_at, '%Y-%m-%d %H:%i:%s') AS deliveredAt "
				+ "FROM ly_delivery_record d JOIN ly_shoporder s ON s.id=d.shoporder_id AND s.site=d.site"
				+ where + " ORDER BY d.id DESC LIMIT ? OFFSET ?", rowArgs);
		return pageResult(rows, total, page);
	}

	@Transactional
	public void addDelivery(String site, String user, String shoporderId,
			String quantity, String referenceNo, String note) {
		String currentSite = required(site, 255, "站点无效");
		Long orderId = optionalId(shoporderId);
		if (orderId == null) throw new IllegalArgumentException("请选择工单");
		BigDecimal amount = quantity(quantity);
		Map<String, Object> order;
		try {
			order = jdbc.queryForMap("SELECT shoporder_numbers, status FROM ly_shoporder "
					+ "WHERE id=? AND site=? FOR UPDATE", orderId, currentSite);
		} catch (EmptyResultDataAccessException missing) {
			throw new IllegalArgumentException("工单不存在或不属于当前站点");
		}
		if (((Number) order.get("status")).intValue() == 6) throw new IllegalArgumentException("已删除工单不能交付");
		BigDecimal planned;
		try {
			planned = quantity(String.valueOf(order.get("shoporder_numbers")));
		} catch (IllegalArgumentException invalidPlan) {
			throw new IllegalArgumentException("工单数量无效");
		}
		BigDecimal delivered = jdbc.queryForObject(
				"SELECT COALESCE(SUM(quantity),0) FROM ly_delivery_record WHERE site=? AND shoporder_id=?",
				new Object[] { currentSite, orderId }, BigDecimal.class);
		if (delivered.add(amount).compareTo(planned) > 0) throw new IllegalArgumentException("累计交付数量不能超过工单数量");
		jdbc.update("INSERT INTO ly_delivery_record "
				+ "(site,shoporder_id,quantity,reference_no,note,created_by) VALUES (?,?,?,?,?,?)",
				currentSite, orderId, amount, optional(referenceNo, 80), optional(note, 500),
				required(user, 255, "用户无效"));
	}

	private Map<String, Object> pageResult(List<Map<String, Object>> rows, Integer total, int page) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("rows", rows);
		data.put("total", total == null ? 0 : total.intValue());
		data.put("page", page);
		data.put("pageSize", PAGE_SIZE);
		return data;
	}

	private int page(int requestedPage) {
		return Math.max(1, Math.min(100000, requestedPage));
	}

	private String required(String value, int maxLength, String message) {
		if (value == null || value.trim().isEmpty() || value.trim().length() > maxLength) {
			throw new IllegalArgumentException(message);
		}
		return value.trim();
	}

	private String optional(String value, int maxLength) {
		if (value == null || value.trim().isEmpty()) return null;
		if (value.trim().length() > maxLength) throw new IllegalArgumentException("输入内容过长");
		return value.trim();
	}

	private Long optionalId(String value) {
		if (value == null || value.trim().isEmpty()) return null;
		if (!value.matches("[0-9]+")) throw new IllegalArgumentException("工单编号无效");
		try {
			long id = Long.parseLong(value);
			if (id <= 0) throw new NumberFormatException();
			return id;
		} catch (NumberFormatException invalidId) {
			throw new IllegalArgumentException("工单编号无效");
		}
	}

	private BigDecimal quantity(String value) {
		BigDecimal result;
		try {
			result = new BigDecimal(value == null ? "" : value.trim()).setScale(3, RoundingMode.UNNECESSARY);
		} catch (NumberFormatException invalidNumber) {
			throw new IllegalArgumentException("数量格式错误");
		} catch (ArithmeticException tooPrecise) {
			throw new IllegalArgumentException("数量最多保留三位小数");
		}
		if (result.signum() <= 0 || result.compareTo(MAX_QUANTITY) > 0) {
			throw new IllegalArgumentException("数量必须大于零且不能过大");
		}
		return result;
	}
}
