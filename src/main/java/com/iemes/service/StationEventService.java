package com.iemes.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Reconstructed station records, kept separate from the legacy SFC state machine. */
@Service
public class StationEventService {
	private static final int PAGE_SIZE = 25;
	private final JdbcTemplate jdbc;

	@Inject
	public StationEventService(DataSource dataSource) {
		this.jdbc = new JdbcTemplate(dataSource);
	}

	public Map<String, Object> list(String site, String type, String sfc, int requestedPage) {
		String currentSite = required(site, 255, "站点无效");
		String stationType = type(type);
		String sfcFilter = optional(sfc, 255);
		String where = " WHERE site=? AND station_type=?" + (sfcFilter == null ? "" : " AND sfc=?");
		Object[] baseArgs = sfcFilter == null
				? new Object[] { currentSite, stationType }
				: new Object[] { currentSite, stationType, sfcFilter };
		Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM ly_station_event" + where, baseArgs, Integer.class);
		int page = Math.max(1, Math.min(100000, requestedPage));
		Object[] rowArgs = sfcFilter == null
				? new Object[] { currentSite, stationType, PAGE_SIZE, (page - 1) * PAGE_SIZE }
				: new Object[] { currentSite, stationType, sfcFilter, PAGE_SIZE, (page - 1) * PAGE_SIZE };
		List<Map<String, Object>> rows = jdbc.queryForList(
				"SELECT id,sfc,result,reference_no AS referenceNo,note,created_by AS createdBy, "
				+ "DATE_FORMAT(created_at,'%Y-%m-%d %H:%i:%s') AS createdAt FROM ly_station_event"
				+ where + " ORDER BY id DESC LIMIT ? OFFSET ?", rowArgs);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("rows", rows);
		data.put("total", total == null ? 0 : total.intValue());
		data.put("page", page);
		data.put("pageSize", PAGE_SIZE);
		return data;
	}

	@Transactional
	public void record(String site, String user, String type, String sfc,
			String result, String referenceNo, String note) {
		String currentSite = required(site, 255, "站点无效");
		String stationType = type(type);
		String serial = required(sfc, 255, "请输入SFC");
		String outcome = "package".equals(stationType) ? "complete" : required(result, 16, "请选择结果");
		if (!"package".equals(stationType) && !"pass".equals(outcome) && !"fail".equals(outcome)) {
			throw new IllegalArgumentException("结果必须为通过或不通过");
		}
		Map<String, Object> product;
		try {
			product = jdbc.queryForMap("SELECT status FROM ly_shoporder_sfc "
					+ "WHERE site=? AND sfc=? ORDER BY id DESC LIMIT 1 FOR UPDATE", currentSite, serial);
		} catch (EmptyResultDataAccessException missing) {
			throw new IllegalArgumentException("SFC不存在或不属于当前站点");
		}
		int status = ((Number) product.get("status")).intValue();
		if (status == 3) throw new IllegalArgumentException("已报废SFC不能登记工位记录");
		if ("package".equals(stationType)) {
			if (status != 2) throw new IllegalArgumentException("只有已完成SFC可以包装");
			Integer existing = jdbc.queryForObject("SELECT COUNT(*) FROM ly_station_event "
					+ "WHERE site=? AND station_type='package' AND sfc=?",
					new Object[] { currentSite, serial }, Integer.class);
			if (existing != null && existing.intValue() > 0) throw new IllegalArgumentException("该SFC已登记包装");
		}
		jdbc.update("INSERT INTO ly_station_event "
				+ "(site,station_type,sfc,result,reference_no,note,created_by) VALUES (?,?,?,?,?,?,?)",
				currentSite, stationType, serial, outcome, optional(referenceNo, 80), optional(note, 500),
				required(user, 255, "用户无效"));
	}

	public Map<String, Object> steps(String site, String sfc) {
		String currentSite = required(site, 255, "站点无效");
		String serial = required(sfc, 255, "请输入SFC");
		Map<String, Object> product;
		try {
			product = jdbc.queryForMap("SELECT sfc,shoporderId AS shoporderNo,status "
					+ "FROM ly_shoporder_sfc WHERE site=? AND sfc=? ORDER BY id DESC LIMIT 1", currentSite, serial);
		} catch (EmptyResultDataAccessException missing) {
			throw new IllegalArgumentException("SFC不存在或不属于当前站点");
		}
		List<Map<String, Object>> rows = jdbc.queryForList("SELECT operation,process_route AS processRoute,"
				+ "status,workcenter,byUser,DATE_FORMAT(createTime,'%Y-%m-%d %H:%i:%s') AS createdAt,"
				+ "DATE_FORMAT(finishTime,'%Y-%m-%d %H:%i:%s') AS finishedAt "
				+ "FROM ly_sfc_step WHERE site=? AND sfc=? ORDER BY id", currentSite, serial);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("product", product);
		data.put("rows", rows);
		return data;
	}

	private String type(String value) {
		if ("package".equals(value) || "sampling".equals(value)
				|| "equipment_testing".equals(value) || "visual_inspection".equals(value)) return value;
		throw new IllegalArgumentException("未知工位");
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
}
