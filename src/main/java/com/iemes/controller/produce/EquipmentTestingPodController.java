package com.iemes.controller.produce;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iemes.annotation.SystemLog;
import com.iemes.controller.base.BaseResponse;
import com.iemes.util.Common;

@Controller
@RequestMapping("/produce/")
public class EquipmentTestingPodController extends StationPodSupport {
	@RequestMapping("equipment_testing_pod")
	public String listUI() {
		return Common.BACKGROUND_PATH + "/system/produce/equipment_testing_pod";
	}

	@ResponseBody
	@RequestMapping(value = "equipment_testing_pod_data", method = RequestMethod.GET)
	public BaseResponse data(@RequestParam(value = "sfc", required = false) String sfc,
			@RequestParam(value = "page", required = false) String page) {
		return events("equipment_testing", sfc, page);
	}

	@ResponseBody
	@RequestMapping(value = "equipment_testing_pod_record", method = RequestMethod.POST)
	@SystemLog(module = "生产过程控制", methods = "设备测试工位-登记")
	public BaseResponse record(@RequestParam(value = "sfc", required = false) String sfc,
			@RequestParam(value = "result", required = false) String result,
			@RequestParam(value = "referenceNo", required = false) String referenceNo,
			@RequestParam(value = "note", required = false) String note) {
		return record("equipment_testing", sfc, result, referenceNo, note);
	}
}
