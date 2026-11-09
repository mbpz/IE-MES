package com.iemes.controller.workshop;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.iemes.annotation.SystemLog;
import com.iemes.controller.base.ConstantController;
import com.iemes.controller.index.BaseController;
import com.iemes.entity.OperationFormMap;
import com.iemes.entity.OperationResourceFormMap;
import com.iemes.entity.UserFormMap;
import com.iemes.plugin.PageView;
import com.iemes.score.GradeCalculateService;
import com.iemes.util.Common;

/**
 * 
 * @author lanyuan 2014-11-19
 * @Email: mmm333zzz520@163.com
 * @version 3.0v
 */
@Controller
@RequestMapping("/workshop/")
public class OperationResourceController extends BaseController {
	
	@Inject
	private GradeCalculateService gradeCalculateService;
	
	@RequestMapping("operation_resource_list")
	public String listUI(Model model) throws Exception {
		model.addAttribute("res", findByRes());
		return Common.BACKGROUND_PATH + "/workshopdata/operation_resource/operation_resource_list";
	}
	
	@ResponseBody
	@RequestMapping("operation_resource_findByPage")
	public PageView findByPage(String pageNow,
			String pageSize) throws Exception {
		Session session = SecurityUtils.getSubject().getSession();
		OperationResourceFormMap operationResourceFormMap = getFormMap(OperationResourceFormMap.class);
		operationResourceFormMap.put("site", session.getAttribute("site"));
		operationResourceFormMap=toFormMap(operationResourceFormMap, pageNow, pageSize,operationResourceFormMap.getStr("orderby"));
		pageView.setRecords(baseMapper.findByPage(operationResourceFormMap));
		return pageView;
	}
	
	@RequestMapping("operation_resource_addUI")
	public String addUI(Model model) throws Exception {
		return Common.BACKGROUND_PATH + "/workshopdata/operation_resource/operation_resource_add";
	}
	
	@ResponseBody
	@RequestMapping("operation_resource_addEntity")
//	@Transactional(readOnly=false)//需要事务操作必须加入此注解
	@SystemLog(module="车间数据维护",methods="资源维护-新增资源")//凡需要处理业务逻辑的.都需要记录操作日志
	public String addEntity() throws Exception {
		OperationResourceFormMap operationResourceFormMap = getFormMap(OperationResourceFormMap.class);
		operationResourceFormMap.put("site", currentSite());
		
		// 获取request
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();  
		// 通过工具类获取当前登录的bean
		UserFormMap userFormMap = (UserFormMap)Common.findUserSession(request);
		operationResourceFormMap.put("byUser", userFormMap.getStr("accountName"));
		baseMapper.addEntity(operationResourceFormMap);
		JSONObject json = new JSONObject();
		String rs = null;
		try {
			rs = gradeCalculateService.GradeCalculate("资源维护", null);
			json.put("result", ConstantController.SUCCESS);
			json.put("score", rs);
		} catch (Exception e) {
			json.put("result", ConstantController.ERROR);
		}
		return json.toString();
	}
	
	@RequestMapping("operation_resource_editUI")
	public String editUI(Model model) throws Exception {
		String id = getPara("id");
		OperationResourceFormMap resource = currentSiteResource(id);
		if (resource == null) return "redirect:/denied.jsp";
		model.addAttribute("operationResource", resource);
		return Common.BACKGROUND_PATH + "/workshopdata/operation_resource/operation_resource_edit";
	}

	private String currentSite() {
		return String.valueOf(SecurityUtils.getSubject().getSession().getAttribute("site"));
	}

	private OperationResourceFormMap currentSiteResource(String id) {
		if (Common.isEmpty(id) || !id.matches("[0-9]+")) return null;
		OperationResourceFormMap resource = baseMapper.findbyFrist("id", id, OperationResourceFormMap.class);
		return resource != null && currentSite().equals(resource.getStr("site")) ? resource : null;
	}
	
	@ResponseBody
	@RequestMapping("operation_resource_editEntity")
	@Transactional(readOnly=false)//需要事务操作必须加入此注解
	@SystemLog(module="车间数据维护",methods="资源维护-修改资源")//凡需要处理业务逻辑的.都需要记录操作日志
	public String editEntity() throws Exception {
		OperationResourceFormMap submitted = getFormMap(OperationResourceFormMap.class);
		OperationResourceFormMap existing = currentSiteResource(submitted.getStr("id"));
		if (existing == null) return "error";
		OperationResourceFormMap update = new OperationResourceFormMap();
		update.put("id", existing.get("id"));
		for (String field : new String[] { "operationResource_no", "operationResource_desc", "operation", "state" }) {
			if (submitted.containsKey(field)) update.put(field, submitted.get(field));
		}
		if (update.size() == 1) return "error";
		baseMapper.editEntity(update);
		return "success";
	}
	
	@ResponseBody
	@RequestMapping("operation_resource_deleteEntity")
	@Transactional(readOnly=false)//需要事务操作必须加入此注解
	@SystemLog(module="车间数据维护",methods="资源维护-删除资源")//凡需要处理业务逻辑的.都需要记录操作日志
	public String deleteEntity() throws Exception {
		String[] ids = getParaValues("ids");
		if (ids == null || ids.length == 0) return "error";
		List<OperationResourceFormMap> resources = new ArrayList<OperationResourceFormMap>();
		for (String id : ids) {
			OperationResourceFormMap resource = currentSiteResource(id);
			if (resource == null) return "error";
			resources.add(resource);
		}
		for (OperationResourceFormMap resource : resources) {
			baseMapper.deleteByAttribute("id", String.valueOf(resource.get("id")), OperationResourceFormMap.class);
		}
		return "success";
	}
	
	/**
	 * 验证资源是否存在
	 * 
	 * @author lanyuan Email：mmm333zzz520@163.com date：2014-2-19
	 * @param name
	 * @return
	 */
	@RequestMapping("operation_resource_isExist")
	@ResponseBody
	public boolean operationResourceIsExist(String name) {
		Session session = SecurityUtils.getSubject().getSession();
		String site = session.getAttribute("site").toString();
		
		OperationResourceFormMap operationResourceFormMap = new OperationResourceFormMap();
		operationResourceFormMap.put("operationResource_no", name);
		operationResourceFormMap.put("site", site);
		List<OperationResourceFormMap> list = baseMapper.findByNames(operationResourceFormMap);
		if (list == null || list.size()<=0) {
			return true;
		} else {
			return false;
		}
	}

}
