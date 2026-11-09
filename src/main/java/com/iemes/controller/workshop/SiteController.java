package com.iemes.controller.workshop;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.iemes.annotation.SystemLog;
import com.iemes.controller.index.BaseController;
import com.iemes.entity.SiteFormMap;
import com.iemes.mapper.site.SiteMapper;
import com.iemes.plugin.PageView;
import com.iemes.service.SiteLifecycleService;
import com.iemes.util.Common;

/**
 * 
 * @author lanyuan 2014-11-19
 * @Email: mmm333zzz520@163.com
 * @version 3.0v
 */
@Controller
@RequestMapping("/workshop/")
public class SiteController extends BaseController {
	@Inject
	private SiteMapper siteMapper; 
	@Inject
	private SiteLifecycleService siteLifecycleService;
	
	@RequestMapping("site_list")
	public String listUI(Model model) throws Exception {
		model.addAttribute("res", findByRes());
		return Common.BACKGROUND_PATH + "/workshopdata/site/site_list";
	}
	
	@ResponseBody
	@RequestMapping("site_findByPage")
	public PageView findByPage(String pageNow,
			String pageSize) throws Exception {
		SiteFormMap siteFormMap = getFormMap(SiteFormMap.class);
		siteFormMap=toFormMap(siteFormMap, pageNow, pageSize,siteFormMap.getStr("orderby"));
		pageView.setRecords(siteMapper.findByPage(siteFormMap));
		return pageView;
	}

	@RequestMapping("site_addUI")
	public String addUI(Model model) throws Exception {
		return Common.BACKGROUND_PATH + "/workshopdata/site/site_add";
	}
	
	@ResponseBody
	@RequestMapping("site_addEntity")
	@SystemLog(module="车间数据维护",methods="站点维护-新增站点")//凡需要处理业务逻辑的.都需要记录操作日志
	public String addEntity() throws Exception {
		SiteFormMap siteFormMap = getFormMap(SiteFormMap.class);
		try {
			siteLifecycleService.create(siteFormMap.getStr("site"), siteFormMap.getStr("siteName"),
					siteFormMap.getStr("siteDescription"), siteFormMap.getStr("state"),
					String.valueOf(SecurityUtils.getSubject().getPrincipal()));
			return "success";
		} catch (IllegalArgumentException | DuplicateKeyException invalid) {
			return "error";
		}
	}
	
	/**
	 * 验证账号是否存在
	 * 
	 * @author lanyuan Email：mmm333zzz520@163.com date：2014-2-19
	 * @param name
	 * @return
	 */
	@RequestMapping("isExist")
	@ResponseBody
	public boolean isExist(String name) {
		return !siteLifecycleService.exists(name);
	}
	
	@RequestMapping("site_editUI")
	public String editUI(Model model) throws Exception {
		String id = getPara("id");
		if(Common.isNotEmpty(id)){
			model.addAttribute("site", siteMapper.findbyFrist("id", id, SiteFormMap.class));
		}
		return Common.BACKGROUND_PATH + "/workshopdata/site/site_edit";
	}
	
	@ResponseBody
	@RequestMapping("site_editEntity")
	@SystemLog(module="车间数据维护",methods="站点维护-修改站点")//凡需要处理业务逻辑的.都需要记录操作日志
	public String editEntity() throws Exception {
		SiteFormMap siteFormMap = getFormMap(SiteFormMap.class);
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		try {
			siteLifecycleService.edit(siteFormMap.getStr("id"),
					request.getParameter("siteFormMap.site"), request.getParameter("siteFormMap.siteName"),
					request.getParameter("siteFormMap.siteDescription"), request.getParameter("siteFormMap.state"));
			return "success";
		} catch (IllegalArgumentException | DuplicateKeyException invalid) {
			return "error";
		}
	}
	
	@ResponseBody
	@RequestMapping("site_deleteEntity")
	@SystemLog(module="车间数据维护",methods="站点维护-删除站点")//凡需要处理业务逻辑的.都需要记录操作日志
	public String deleteEntity() throws Exception {
		try {
			siteLifecycleService.delete(getParaValues("ids"));
			return "success";
		} catch (IllegalArgumentException invalid) {
			return "error";
		}
	}
}
