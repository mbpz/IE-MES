package com.iemes.controller.produce;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iemes.controller.base.BaseResponse;
import com.iemes.util.Common;

@Controller
@RequestMapping("/produce/")
public class SfcStepController extends StationPodSupport {
	@RequestMapping("sfc_step")
	public String listUI() {
		return Common.BACKGROUND_PATH + "/system/produce/sfc_step";
	}

	@ResponseBody
	@RequestMapping(value = "sfc_step_data", method = RequestMethod.GET)
	public BaseResponse data(@RequestParam(value = "sfc", required = false) String sfc) {
		try {
			return response(true, "查询成功", stationEvents.steps(site(), sfc));
		} catch (IllegalArgumentException invalid) {
			return response(false, invalid.getMessage(), null);
		}
	}
}
