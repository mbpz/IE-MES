package com.iemes.controller.qc;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
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
import com.iemes.entity.NcCodeFormMap;
import com.iemes.entity.NcCodeGroupFormMap;
import com.iemes.entity.NcCodeGroupRelFormMap;
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
@RequestMapping("/qc/")
public class NcCodeController extends BaseController {
	@Inject
	private GradeCalculateService gradeCalculateService;
	
	@RequestMapping("nc_list")
	public String listUI(Model model) throws Exception {
		model.addAttribute("res", findByRes());
		return Common.BACKGROUND_PATH + "/qc/nc/nc_list";
	}
	
	@ResponseBody
	@RequestMapping("nc_findByPage")
	public PageView findByPage(String pageNow,
			String pageSize) throws Exception {
		NcCodeFormMap ncCodeFormMap = getFormMap(NcCodeFormMap.class);
		ncCodeFormMap=toFormMap(ncCodeFormMap, pageNow, pageSize,ncCodeFormMap.getStr("orderby"));
		Session session = SecurityUtils.getSubject().getSession();
		ncCodeFormMap.put("site", session.getAttribute("site"));
		List<NcCodeFormMap> ncCodeFormMaps = baseMapper.findByPage(ncCodeFormMap);
		//加载每个不合格代码所属的不合格组代码
		if(CollectionUtils.isNotEmpty(ncCodeFormMaps))
		{
			for(NcCodeFormMap nc : ncCodeFormMaps)
			{
				String ncCode = nc.getStr("nc_code");
				List<NcCodeGroupRelFormMap> ncCodeGroupRelFormMaps = baseMapper.findByAttribute("nc_code", ncCode, NcCodeGroupRelFormMap.class);
				if(CollectionUtils.isNotEmpty(ncCodeGroupRelFormMaps))
				{
					NcCodeGroupRelFormMap ncCodeGroupRelFormMap = ncCodeGroupRelFormMaps.get(0);
					nc.put("nc_code_group_no", ncCodeGroupRelFormMap.getStr("nc_code_group"));
				}
			}
		}
		pageView.setRecords(ncCodeFormMaps);
		return pageView;
	}
	
	@RequestMapping("nc_addUI")
	public String addUI(Model model) throws Exception {
		return Common.BACKGROUND_PATH + "/qc/nc/nc_add";
	}
	
	@ResponseBody
	@RequestMapping("nc_addEntity")
	@Transactional(readOnly=false)
	@SystemLog(module="产品质量管理",methods="不合格维护-新增不合格")//凡需要处理业务逻辑的.都需要记录操作日志
	public String addEntity() throws Exception {
		NcCodeFormMap ncCodeFormMap = getFormMap(NcCodeFormMap.class);
		
		// 获取request
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();  
		String groupNo = request.getParameter("ncCodeGroupFormMap.nc_code_group_no");
		if (Common.isEmpty(ncCodeFormMap.getStr("nc_code")) || !currentSiteGroupExists(groupNo)) return "error";
		// 通过工具类获取当前登录的bean
		UserFormMap userFormMap = (UserFormMap)Common.findUserSession(request);
		Session session = SecurityUtils.getSubject().getSession();
		ncCodeFormMap.put("site", session.getAttribute("site"));
		ncCodeFormMap.put("by_user", userFormMap.getStr("accountName"));
		baseMapper.addEntity(ncCodeFormMap);
		
		//保存不合格代码与不合代码组关联关系
		NcCodeGroupRelFormMap ncCodeGroupRelFormMap = new NcCodeGroupRelFormMap();
		ncCodeGroupRelFormMap.put("site", session.getAttribute("site"));
		ncCodeGroupRelFormMap.put("nc_code", ncCodeFormMap.getStr("nc_code"));
		ncCodeGroupRelFormMap.put("nc_code_group", groupNo);
		baseMapper.addEntity(ncCodeGroupRelFormMap);
		
		JSONObject json = new JSONObject();
		String rs = null;
		try {
			rs = gradeCalculateService.GradeCalculate("不合格代码维护", null);
			json.put("result", ConstantController.SUCCESS);
			json.put("score", rs);
		} catch (Exception e) {
			json.put("result", ConstantController.ERROR);
		}
		return json.toString();
	}
	
	@RequestMapping("nc_editUI")
	public String editUI(Model model) throws Exception {
		String id = getPara("id");
		NcCodeFormMap ncCodeFormMap = currentSiteCode(id);
		if (ncCodeFormMap == null) return "redirect:/denied.jsp";
			//加载所属不合格组
			String ncCode = ncCodeFormMap.getStr("nc_code");
			NcCodeGroupRelFormMap query = new NcCodeGroupRelFormMap();
			query.put("nc_code", ncCode);
			query.put("site", currentSite());
			List<NcCodeGroupRelFormMap> ncCodeGroupRelFormMaps = baseMapper.findByNames(query);
			if(CollectionUtils.isNotEmpty(ncCodeGroupRelFormMaps))
			{
				NcCodeGroupRelFormMap ncCodeGroupRelFormMap = ncCodeGroupRelFormMaps.get(0);
				ncCodeFormMap.put("nc_code_group_no", ncCodeGroupRelFormMap.getStr("nc_code_group"));
			}
			model.addAttribute("ncCode", ncCodeFormMap);
		return Common.BACKGROUND_PATH + "/qc/nc/nc_edit";
	}

	private String currentSite() {
		return String.valueOf(SecurityUtils.getSubject().getSession().getAttribute("site"));
	}

	private NcCodeFormMap currentSiteCode(String id) {
		if (Common.isEmpty(id) || !id.matches("[0-9]+")) return null;
		NcCodeFormMap code = baseMapper.findbyFrist("id", id, NcCodeFormMap.class);
		return code != null && currentSite().equals(code.getStr("site")) ? code : null;
	}

	private boolean currentSiteGroupExists(String groupNo) {
		if (Common.isEmpty(groupNo)) return false;
		NcCodeGroupFormMap query = new NcCodeGroupFormMap();
		query.put("nc_code_group_no", groupNo);
		query.put("site", currentSite());
		return CollectionUtils.isNotEmpty(baseMapper.findByNames(query));
	}
	
	@ResponseBody
	@RequestMapping("nc_editEntity")
	@Transactional(readOnly=false)
	@SystemLog(module="产品质量管理",methods="不合格维护-修改不合格")//凡需要处理业务逻辑的.都需要记录操作日志
	public String editEntity() throws Exception {
		NcCodeFormMap submitted = getFormMap(NcCodeFormMap.class);
		NcCodeFormMap existing = currentSiteCode(submitted.getStr("id"));
		if (existing == null || !existing.getStr("nc_code").equals(submitted.getStr("nc_code"))) return "error";
		String groupNo = submitted.getStr("nc_code_group_no");
		if (!currentSiteGroupExists(groupNo)) return "error";
		NcCodeGroupRelFormMap relation = new NcCodeGroupRelFormMap();
		relation.put("nc_code", existing.getStr("nc_code"));
		relation.put("site", currentSite());
		List<NcCodeGroupRelFormMap> links = baseMapper.findByNames(relation);
		if (CollectionUtils.isNotEmpty(links)) {
			relation = links.get(0);
			relation.put("nc_code_group", groupNo);
			baseMapper.editEntity(relation);
		} else {
			relation.put("nc_code_group", groupNo);
			baseMapper.addEntity(relation);
		}
		NcCodeFormMap update = new NcCodeFormMap();
		update.put("id", existing.get("id"));
		for (String field : new String[] { "nc_code_desc", "state" }) {
			if (submitted.containsKey(field)) update.put(field, submitted.get(field));
		}
		if (update.size() > 1) baseMapper.editEntity(update);
		return "success";
	}
	
	@ResponseBody
	@RequestMapping("nc_deleteEntity")
	@Transactional(readOnly=false)//需要事务操作必须加入此注解
	@SystemLog(module="产品质量管理",methods="不合格维护-删除不合格")//凡需要处理业务逻辑的.都需要记录操作日志
	public String deleteEntity() throws Exception {
		String[] ids = getParaValues("ids");
		if (ids == null || ids.length == 0) return "error";
		List<NcCodeFormMap> codes = new ArrayList<NcCodeFormMap>();
		for (String id : ids) {
			NcCodeFormMap code = currentSiteCode(id);
			if (code == null) return "error";
			codes.add(code);
		}
		for (NcCodeFormMap code : codes) {
			NcCodeGroupRelFormMap relation = new NcCodeGroupRelFormMap();
			relation.put("nc_code", "'" + code.getStr("nc_code") + "'");
			relation.put("site", "'" + currentSite() + "'");
			baseMapper.deleteByNames(relation);
			baseMapper.deleteByAttribute("id", String.valueOf(code.get("id")), NcCodeFormMap.class);
		}
		return "success";
	}

}
