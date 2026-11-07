package com.iemes.controller.plan;

import javax.inject.Inject;

import org.apache.shiro.SecurityUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iemes.annotation.SystemLog;
import com.iemes.controller.base.BaseResponse;
import com.iemes.controller.index.BaseController;
import com.iemes.service.PlanLedgerService;
import com.iemes.util.Common;

/** Manual external-order register reconstructed from the missing ERP page. */
@Controller
@RequestMapping("/plan/")
public class ErpOrderController extends BaseController {
	@Inject
	private PlanLedgerService ledger;

	@RequestMapping("erp_order")
	public String listUI(Model model) {
		return Common.BACKGROUND_PATH + "/system/plan/erp_order";
	}

	@ResponseBody
	@RequestMapping(value = "erp_order_data", method = RequestMethod.GET)
	public BaseResponse data(@RequestParam(value = "q", required = false) String query,
			@RequestParam(value = "page", required = false) String page) {
		try {
			return response(true, "查询成功", ledger.listErpOrders(site(), query, page(page)));
		} catch (IllegalArgumentException invalid) {
			return response(false, invalid.getMessage(), null);
		}
	}

	@ResponseBody
	@RequestMapping(value = "erp_order_shoporders", method = RequestMethod.GET)
	public BaseResponse shoporders() {
		try {
			return response(true, "查询成功", ledger.listShopOrders(site()));
		} catch (IllegalArgumentException invalid) {
			return response(false, invalid.getMessage(), null);
		}
	}

	@ResponseBody
	@RequestMapping(value = "erp_order_create", method = RequestMethod.POST)
	@SystemLog(module = "生产计划管理", methods = "ERP订单-手工登记")
	public BaseResponse create(@RequestParam(value = "erpOrderNo", required = false) String orderNo,
			@RequestParam(value = "customerName", required = false) String customer,
			@RequestParam(value = "itemNo", required = false) String itemNo,
			@RequestParam(value = "quantity", required = false) String quantity,
			@RequestParam(value = "expectedDeliveryDate", required = false) String expectedDate,
			@RequestParam(value = "shoporderId", required = false) String shoporderId,
			@RequestParam(value = "note", required = false) String note) {
		try {
			ledger.addErpOrder(site(), user(), orderNo, customer, itemNo, quantity,
					expectedDate, shoporderId, note);
			return response(true, "登记成功", null);
		} catch (DuplicateKeyException duplicate) {
			return response(false, "当前站点已存在该ERP订单号", null);
		} catch (IllegalArgumentException invalid) {
			return response(false, invalid.getMessage(), null);
		}
	}

	private String site() {
		Object selected = SecurityUtils.getSubject().getSession().getAttribute("site");
		return selected == null ? null : selected.toString();
	}

	private String user() {
		return String.valueOf(SecurityUtils.getSubject().getPrincipal());
	}

	private int page(String value) {
		try {
			return value == null ? 1 : Integer.parseInt(value);
		} catch (NumberFormatException invalid) {
			return 1;
		}
	}

	private BaseResponse response(boolean result, String message, Object data) {
		BaseResponse response = new BaseResponse();
		response.setResult(result);
		response.setMessage(message);
		response.setData(data);
		return response;
	}
}
