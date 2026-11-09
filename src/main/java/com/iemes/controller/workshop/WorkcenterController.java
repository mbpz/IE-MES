package com.iemes.controller.workshop;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Controller;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.iemes.annotation.SystemLog;
import com.iemes.controller.base.ConstantController;
import com.iemes.controller.index.BaseController;
import com.iemes.entity.OperationResourceFormMap;
import com.iemes.entity.UserFormMap;
import com.iemes.entity.WorkCenterFormMap;
import com.iemes.entity.WorkCenterRelationFormMap;
import com.iemes.mapper.WorkShopMapper;
import com.iemes.plugin.PageView;
import com.iemes.score.GradeCalculateService;
import com.iemes.util.Common;

import net.sf.json.JSONObject;

/**
 * 
 * @author lanyuan 2014-11-19
 * @Email: mmm333zzz520@163.com
 * @version 3.0v
 */
@Controller
@RequestMapping("/workshop/")
public class WorkcenterController extends BaseController {

	@Inject
	private GradeCalculateService gradeCalculateService;

	@Inject
	private DataSource dataSource;

	private String currentSite() {
		return String.valueOf(SecurityUtils.getSubject().getSession().getAttribute("site"));
	}

	private WorkCenterFormMap currentSiteCenter(String id) {
		if (Common.isEmpty(id) || !id.matches("[0-9]+")) return null;
		WorkCenterFormMap center = workShopMapper.findbyFrist("id", id, WorkCenterFormMap.class);
		return center != null && currentSite().equals(center.getStr("site")) ? center : null;
	}

	private boolean centerExists(String number) {
		if (Common.isEmpty(number)) return false;
		Integer count = new JdbcTemplate(dataSource).queryForObject(
				"SELECT COUNT(*) FROM ly_workcenter WHERE site=? AND workcenter_no=?",
				new Object[] { currentSite(), number }, Integer.class);
		return count != null && count.intValue() > 0;
	}

	@Inject
	private WorkShopMapper workShopMapper;

	@RequestMapping("workcenter_list")
	public String listUI(Model model) throws Exception {
		model.addAttribute("res", findByRes());
		return Common.BACKGROUND_PATH + "/workshopdata/workcenter/workcenter_list";
	}

	@ResponseBody
	@RequestMapping("workcenter_findByPage")
	public PageView findByPage(String pageNow, String pageSize) throws Exception 
	{
		Session session = SecurityUtils.getSubject().getSession();
		WorkCenterFormMap workCenterFormMap = getFormMap(WorkCenterFormMap.class);
		workCenterFormMap.put("site", session.getAttribute("site"));
		workCenterFormMap=toFormMap(workCenterFormMap, pageNow, pageSize,workCenterFormMap.getStr("orderby"));
		List<WorkCenterFormMap> workCenterFormMaps = workShopMapper.findByPage(workCenterFormMap);
		if(CollectionUtils.isNotEmpty(workCenterFormMaps))
		{
			//加载工作中心的父工作中心
			for(WorkCenterFormMap w : workCenterFormMaps)
			{
				List<WorkCenterFormMap> fWorkcenter = workShopMapper.getParentWorkCenterMap(w.getStr("workcenter_no"));
				if(CollectionUtils.isNotEmpty(fWorkcenter))
				{
					w.put("workcenter_parent", fWorkcenter.get(0).getStr("workcenter_no"));
				}
			}
		}
		pageView.setRecords(workCenterFormMaps);
		return pageView;
	}

	/**
	 * 查询出工作中心是车间
	 * @param pageNow
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("workcenter_workShop_findByPage")
	public PageView workcenter_workShop_findByPage(String pageNow, String pageSize) throws Exception 
	{
		Session session = SecurityUtils.getSubject().getSession();
		WorkCenterFormMap workCenterFormMap = new WorkCenterFormMap();
		workCenterFormMap.put("site", session.getAttribute("site"));
		workCenterFormMap.put("workcenter_class", "workshop");
		List<WorkCenterFormMap> list = baseMapper.findByNames(workCenterFormMap);
		pageView = getPageView(pageNow, pageSize, getPara("orderby"));
		pageView.setRecords(list);
		pageView.setQueryResult(list.size(), list);
		return pageView;
	}
	
	/**
	 * 查询所有产线
	 * @param pageNow
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("workcenter_line_findByPage")
	public PageView workcenter_line_findByPage(String pageNow, String pageSize) throws Exception 
	{
		Session session = SecurityUtils.getSubject().getSession();
		WorkCenterFormMap workCenterFormMap = new WorkCenterFormMap();
		workCenterFormMap.put("site", session.getAttribute("site"));
		workCenterFormMap.put("workcenter_class", "production_line");
		List<WorkCenterFormMap> list = baseMapper.findByNames(workCenterFormMap);
		pageView = getPageView(pageNow, pageSize, getPara("orderby"));
		pageView.setRecords(list);
		pageView.setQueryResult(list.size(), list);
		return pageView;
	}



	/**
	 *  根据传过来的workcenter_no 查对应的workcenter_child 
	 * @param pageNow
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("workcenter_child_findByPage")
	public PageView findChildByPage(String pageNow, String pageSize) throws Exception 
	{
		//取得前端的值
		//		FormMap<String, String> formMap = getFormMap(FormMap.class);
		String workcenter = getPara("workcenter");
		Session session = SecurityUtils.getSubject().getSession();
		//1、现根据关系表查出产线的workcenter_no
		//2、再用下面的方法查出list
		WorkCenterRelationFormMap workCenterRelationFormMap = new WorkCenterRelationFormMap();
		workCenterRelationFormMap.put("workcenter", workcenter);
		workCenterRelationFormMap.put("site", session.getAttribute("site"));
		List<WorkCenterRelationFormMap> list = baseMapper.findByNames(workCenterRelationFormMap);

		if (list!=null) {
			StringBuffer workcenters = new StringBuffer();
			for(int i=0;i<list.size();i++){
				String workcenter_child = list.get(i).getStr("workcenter_child");
				workcenters.append("'"+workcenter_child+"'");
				if (i!=list.size()-1) {
					workcenters.append(",");
				}
			}

			WorkCenterFormMap workCenterFormMap = new WorkCenterFormMap();
//			workCenterFormMap.put("workcenter_no", workcenters.toString());
//			workCenterFormMap.put("site", session.getAttribute("site"));
//			
			workCenterFormMap.put("where", "where workcenter_no in ("+workcenters.toString()+") "
					+ " and site = '"+session.getAttribute("site")+"'");
			List<WorkCenterFormMap> rs = baseMapper.findByWhere(workCenterFormMap);
			pageView = getPageView(pageNow, pageSize, getPara("orderby"));
			pageView.setRecords(rs);
			pageView.setQueryResult(list.size(), rs);
		}
		return pageView;
	}

	@RequestMapping("workcenter_add")
	public String addUI(Model model) throws Exception {
		return Common.BACKGROUND_PATH + "/workshopdata/workcenter/workcenter_add";
	}

	@ResponseBody
	@RequestMapping("workcenter_addEntity")
	@Transactional(readOnly=false)
	@SystemLog(module="车间数据维护",methods="工作中心维护-新增工作中心")//凡需要处理业务逻辑的.都需要记录操作日志
	public String addEntity() throws Exception {
		JSONObject json = new JSONObject();
		WorkCenterFormMap workCenterFormMap = getFormMap(WorkCenterFormMap.class);
		Session session = SecurityUtils.getSubject().getSession();
		// 获取request
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();  
		String parentWorkcenterNo = request.getParameter("workcenterFormMap.workcenter_parent");
		if (StringUtils.isNotEmpty(parentWorkcenterNo) && !centerExists(parentWorkcenterNo)) return "error";
		// 通过工具类获取当前登录的bean
		UserFormMap userFormMap = (UserFormMap)Common.findUserSession(request);
		workCenterFormMap.put("byUser", userFormMap.getStr("accountName"));
		workCenterFormMap.put("site", currentSite());
		workShopMapper.addEntity(workCenterFormMap);

		//保存工作中心关联表
		if(StringUtils.isNotEmpty(parentWorkcenterNo))
		{
			WorkCenterRelationFormMap relationMap = new WorkCenterRelationFormMap();
			relationMap.put("workcenter", parentWorkcenterNo);
			relationMap.put("workcenter_child", workCenterFormMap.getStr("workcenter_no"));
			relationMap.put("site", session.getAttribute("site"));
			workShopMapper.addEntity(relationMap);
		}
		String rs = gradeCalculateService.GradeCalculate("工作中心维护", null);
		json.put("result", ConstantController.SUCCESS);
		json.put("score", rs);
		return json.toString();
	}

	@RequestMapping("workcenter_edit")
	public String editUI(Model model) throws Exception 
	{
		String id = getPara("id");
		WorkCenterFormMap center = currentSiteCenter(id);
		if (center == null) return "redirect:/denied.jsp";
		WorkCenterRelationFormMap relation = new WorkCenterRelationFormMap();
		relation.put("workcenter_child", center.getStr("workcenter_no"));
		relation.put("site", currentSite());
		List<WorkCenterRelationFormMap> parents = baseMapper.findByNames(relation);
		if (CollectionUtils.isNotEmpty(parents)) center.put("workcenter_parent", parents.get(0).getStr("workcenter"));
		model.addAttribute("workcenter", center);

		return Common.BACKGROUND_PATH + "/workshopdata/workcenter/workcenter_edit";
	}

	@ResponseBody
	@RequestMapping("workcenter_editEntity")
	@Transactional(readOnly=false)
	@SystemLog(module="车间数据维护",methods="工作中心维护-修改工作中心")//凡需要处理业务逻辑的.都需要记录操作日志
	public String editEntity() throws Exception {
		WorkCenterFormMap submitted = getFormMap(WorkCenterFormMap.class);
		WorkCenterFormMap existing = currentSiteCenter(submitted.getStr("id"));
		if (existing == null) return "error";
		String site = currentSite();
		String oldNo = existing.getStr("workcenter_no");
		String newNo = submitted.getStr("workcenter_no") == null ? oldNo : submitted.getStr("workcenter_no");
		if (Common.isEmpty(newNo) || (!newNo.equals(oldNo) && centerExists(newNo))) return "error";
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String parent = request.getParameter("workcenterFormMap.workcenter_parent");
		if (StringUtils.isNotEmpty(parent)
				&& (parent.equals(oldNo) || parent.equals(newNo) || !centerExists(parent))) return "error";
		WorkCenterFormMap update = new WorkCenterFormMap();
		update.put("id", existing.get("id"));
		for (String field : new String[] { "workcenter_no", "workcenter_name", "workcenter_description",
				"workcenter_version", "workcenter_class", "workcenter_type", "state" }) {
			if (submitted.containsKey(field)) update.put(field, submitted.get(field));
		}
		if (update.size() == 1 && parent == null) return "error";
		if (update.size() > 1) workShopMapper.editEntity(update);
		JdbcTemplate jdbc = new JdbcTemplate(dataSource);
		if (!newNo.equals(oldNo)) {
			jdbc.update("UPDATE ly_workcenter_relation SET workcenter=? WHERE site=? AND workcenter=?", newNo, site, oldNo);
			jdbc.update("UPDATE ly_workcenter_relation SET workcenter_child=? WHERE site=? AND workcenter_child=?", newNo, site, oldNo);
			jdbc.update("UPDATE ly_operation SET workcenter=? WHERE site=? AND workcenter=?", newNo, site, oldNo);
			jdbc.update("UPDATE ly_sfc_step SET workcenter=? WHERE site=? AND workcenter=?", newNo, site, oldNo);
			jdbc.update("UPDATE ly_shoporder SET workcenter=? WHERE site=? AND workcenter=?", newNo, site, oldNo);
			jdbc.update("UPDATE ly_shoporder SET workline=? WHERE site=? AND workline=?", newNo, site, oldNo);
			jdbc.update("UPDATE ly_nc_repair SET workcenter=? WHERE site=? AND workcenter=?", newNo, site, oldNo);
			jdbc.update("UPDATE ly_nc_repair SET workcenter_child=? WHERE site=? AND workcenter_child=?", newNo, site, oldNo);
		}
		if (parent != null) {
			jdbc.update("DELETE FROM ly_workcenter_relation WHERE site=? AND workcenter_child=?", site, newNo);
			if (StringUtils.isNotEmpty(parent)) {
				jdbc.update("INSERT INTO ly_workcenter_relation (workcenter,workcenter_child,site) VALUES (?,?,?)",
						parent, newNo, site);
			}
		}
		return "success";
	}

	@ResponseBody
	@RequestMapping("workcenter_deleteEntity")
	@Transactional(readOnly=false)//需要事务操作必须加入此注解
	@SystemLog(module="车间数据维护",methods="工作中心维护-删除工作中心")//凡需要处理业务逻辑的.都需要记录操作日志
	public String deleteEntity() throws Exception {
		String[] ids = getParaValues("ids");
		if (ids == null || ids.length == 0) return "error";
		List<WorkCenterFormMap> centers = new ArrayList<WorkCenterFormMap>();
		for (String id : ids) {
			WorkCenterFormMap center = currentSiteCenter(id);
			if (center == null || hasDependents(center.getStr("workcenter_no"))) return "error";
			centers.add(center);
		}
		JdbcTemplate jdbc = new JdbcTemplate(dataSource);
		for (WorkCenterFormMap center : centers) {
			jdbc.update("DELETE FROM ly_workcenter_relation WHERE site=? AND workcenter_child=?",
					currentSite(), center.getStr("workcenter_no"));
			workShopMapper.deleteByAttribute("id", String.valueOf(center.get("id")), WorkCenterFormMap.class);
		}
		return "success";
	}

	private boolean hasDependents(String number) {
		JdbcTemplate jdbc = new JdbcTemplate(dataSource);
		String[][] references = {
				{ "ly_workcenter_relation", "workcenter" }, { "ly_operation", "workcenter" },
				{ "ly_sfc_step", "workcenter" }, { "ly_shoporder", "workcenter" },
				{ "ly_shoporder", "workline" }, { "ly_nc_repair", "workcenter" },
				{ "ly_nc_repair", "workcenter_child" }
		};
		for (String[] reference : references) {
			Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM " + reference[0]
					+ " WHERE site=? AND " + reference[1] + "=?",
					new Object[] { currentSite(), number }, Integer.class);
			if (count != null && count.intValue() > 0) return true;
		}
		return false;
	}
	
	/**
	 * 验证工作中心是否存在
	 * 
	 * @author lanyuan Email：mmm333zzz520@163.com date：2014-2-19
	 * @param name
	 * @return
	 */
	@RequestMapping("workcenter_isExist")
	@ResponseBody
	public boolean workcenter_isExist(String name) {
		Session session = SecurityUtils.getSubject().getSession();
		String site = session.getAttribute("site").toString();
		
		WorkCenterFormMap workCenterFormMap = new WorkCenterFormMap();
		workCenterFormMap.put("workcenter_no", name);
		workCenterFormMap.put("site", site);
		List<WorkCenterFormMap> list = baseMapper.findByNames(workCenterFormMap);
		if (list == null || list.size()<=0) {
			return true;
		} else {
			return false;
		}
	}

}
