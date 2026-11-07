package com.iemes.controller.plan;

import javax.inject.Inject;

import org.apache.shiro.SecurityUtils;
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

/** Manual delivery register; it does not mutate production or inventory state. */
@Controller
@RequestMapping("/plan/")
public class DeliverNumberController extends BaseController {
	@Inject
	private PlanLedgerService ledger;

	@RequestMapping("deliver_number")
	public String listUI(Model model) {
		return Common.BACKGROUND_PATH + "/system/plan/deliver_number";
	}

	@ResponseBody
	@RequestMapping(value = "deliver_number_orders", method = RequestMethod.GET)
	public BaseResponse orders() {
		try {
			return response(true, "查询成功", ledger.listShopOrders(site()));
		} catch (IllegalArgumentException invalid) {
			return response(false, invalid.getMessage(), null);
		}
	}

	@ResponseBody
	@RequestMapping(value = "deliver_number_data", method = RequestMethod.GET)
	public BaseResponse data(@RequestParam(value = "shoporderId", required = false) String shoporderId,
			@RequestParam(value = "page", required = false) String page) {
		try {
			return response(true, "查询成功", ledger.listDeliveries(site(), shoporderId, page(page)));
		} catch (IllegalArgumentException invalid) {
			return response(false, invalid.getMessage(), null);
		}
	}

	@ResponseBody
	@RequestMapping(value = "deliver_number_create", method = RequestMethod.POST)
	@SystemLog(module = "生产计划管理", methods = "工单交付-手工登记")
	public BaseResponse create(@RequestParam(value = "shoporderId", required = false) String shoporderId,
			@RequestParam(value = "quantity", required = false) String quantity,
			@RequestParam(value = "referenceNo", required = false) String referenceNo,
			@RequestParam(value = "note", required = false) String note) {
		try {
			ledger.addDelivery(site(), user(), shoporderId, quantity, referenceNo, note);
			return response(true, "登记成功", null);
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
