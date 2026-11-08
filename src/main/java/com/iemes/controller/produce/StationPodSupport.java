package com.iemes.controller.produce;

import javax.inject.Inject;

import org.apache.shiro.SecurityUtils;

import com.iemes.controller.base.BaseResponse;
import com.iemes.controller.index.BaseController;
import com.iemes.service.StationEventService;

/** Shared validation response for the reconstructed station pages. */
public abstract class StationPodSupport extends BaseController {
	@Inject
	protected StationEventService stationEvents;

	protected BaseResponse events(String type, String sfc, String page) {
		try {
			return response(true, "查询成功", stationEvents.list(site(), type, sfc, page(page)));
		} catch (IllegalArgumentException invalid) {
			return response(false, invalid.getMessage(), null);
		}
	}

	protected BaseResponse record(String type, String sfc, String result, String referenceNo, String note) {
		try {
			stationEvents.record(site(), user(), type, sfc, result, referenceNo, note);
			return response(true, "登记成功", null);
		} catch (IllegalArgumentException invalid) {
			return response(false, invalid.getMessage(), null);
		}
	}

	protected String site() {
		Object selected = SecurityUtils.getSubject().getSession().getAttribute("site");
		return selected == null ? null : selected.toString();
	}

	protected String user() {
		return String.valueOf(SecurityUtils.getSubject().getPrincipal());
	}

	protected int page(String value) {
		try {
			return value == null ? 1 : Integer.parseInt(value);
		} catch (NumberFormatException invalid) {
			return 1;
		}
	}

	protected BaseResponse response(boolean result, String message, Object data) {
		BaseResponse response = new BaseResponse();
		response.setResult(result);
		response.setMessage(message);
		response.setData(data);
		return response;
	}
}
