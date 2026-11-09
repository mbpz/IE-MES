package com.iemes.controller.workshop;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Controller;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.iemes.annotation.SystemLog;
import com.iemes.controller.base.ConstantController;
import com.iemes.controller.index.BaseController;
import com.iemes.entity.RoutingFormMap;
import com.iemes.entity.RoutingStepFormMap;
import com.iemes.entity.UserFormMap;
import com.iemes.mapper.operation.OperationMapper;
import com.iemes.mapper.routing.RoutingMapper;
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
public class RoutingController extends BaseController {
	@Inject
	private DataSource dataSource;

	private String currentSite() {
		return String.valueOf(SecurityUtils.getSubject().getSession().getAttribute("site"));
	}

	private RoutingFormMap currentSiteRouting(String id) {
		if (Common.isEmpty(id) || !id.matches("[0-9]+")) return null;
		RoutingFormMap route = baseMapper.findbyFrist("id", id, RoutingFormMap.class);
		return route != null && currentSite().equals(route.getStr("siteBo")) ? route : null;
	}

	private RoutingFormMap currentSiteRoutingByNo(String number) {
		if (Common.isEmpty(number)) return null;
		RoutingFormMap query = new RoutingFormMap();
		query.put("process_route", number);
		query.put("siteBo", currentSite());
		List<RoutingFormMap> routes = baseMapper.findByNames(query);
		return routes == null || routes.isEmpty() ? null : routes.get(0);
	}
	
	@Inject
	private RoutingMapper routingMapper;
	@Inject
	private OperationMapper operationMapper;
	
	@Inject
	private GradeCalculateService gradeCalculateService;
	
	@RequestMapping("routing_list")
	public String listUI(Model model) throws Exception {
		model.addAttribute("res", findByRes());
		return Common.BACKGROUND_PATH + "/workshopdata/process_route/routing_list";
	}
	
	@ResponseBody
	@RequestMapping("routing_findByPage")
	public PageView findByPage(String pageNow,
			String pageSize) throws Exception {
		Session session = SecurityUtils.getSubject().getSession();
		RoutingFormMap routingFormMap = getFormMap(RoutingFormMap.class);
		routingFormMap.put("siteBo", session.getAttribute("site"));
		routingFormMap=toFormMap(routingFormMap, pageNow, pageSize,routingFormMap.getStr("orderby"));
		pageView.setRecords(routingMapper.findByPage(routingFormMap));
		return pageView;
	}

	@RequestMapping("routing_addUI")
	public String addUI(Model model) throws Exception {
		return Common.BACKGROUND_PATH + "/workshopdata/process_route/routing_add";
	}
	
	@RequestMapping("routing_designerUI")
	public String designer(Model model) throws Exception {
		return "redirect:/process_designer/gooFlow/routing_add.html";
	}
	
	@ResponseBody
	@RequestMapping("routing_addEntity")
	@Transactional(readOnly=false)
	@SystemLog(module="车间数据维护",methods="工艺路线维护-新增工艺路线")//凡需要处理业务逻辑的.都需要记录操作日志
	public String addEntity() {
		RoutingFormMap routingFormMap = getFormMap(RoutingFormMap.class);
		Session session = SecurityUtils.getSubject().getSession();
		String site = session.getAttribute("site").toString();
		
		// 获取request
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();  
		// 通过工具类获取当前登录的bean
		UserFormMap userFormMap = (UserFormMap)Common.findUserSession(request);
		routingFormMap.put("createUser", userFormMap.getStr("accountName"));
		routingFormMap.put("siteBo", session.getAttribute("site"));
		if (Common.isEmpty(routingFormMap.getStr("process_route"))
				|| currentSiteRoutingByNo(routingFormMap.getStr("process_route")) != null) return ConstantController.ERROR;
		try{
			routingMapper.addEntity(routingFormMap);
			String data = routingFormMap.getStr("data");
			JSONObject obj = JSONObject.fromObject(data);
			JSONObject lines = (JSONObject) obj.get("lines");
			JSONObject nodes = (JSONObject) obj.get("nodes");
			if (lines.size()==0 && nodes.size()==1) {
				Set<String> set = nodes.keySet();
				int i=0;
				for (String key : set) {
					RoutingStepFormMap map2 = new RoutingStepFormMap();
					map2.put("process_route", routingFormMap.get("process_route"));
					map2.put("operation", key);
					map2.put("next_operation", "");
					map2.put("state", 0);
					map2.put("site", site);
					if (i==0) {
						map2.put("operation_order", 0);
					}
					i++;
					baseMapper.addEntity(map2);
				}
			}else {
				if (lines.size()==0) throw new IllegalArgumentException("工艺路线节点未连接");
				Set<String> set = lines.keySet();
				int i=0;
				for (String key : set) {
					//循环取对象
					JSONObject line = (JSONObject) lines.get(key);
					//取from值
					String operation = line.getString("from");
					//取to值
					String nextOperation = line.getString("to");
					
					//根据to值找from值进行存储
					RoutingStepFormMap map2 = new RoutingStepFormMap();
					map2.put("process_route", routingFormMap.get("process_route"));
					map2.put("operation", operation);
					map2.put("next_operation", nextOperation);
					map2.put("state", 0);
					map2.put("site", site);
					if (i==0) {
						map2.put("operation_order", 0);
					}
					i++;
					baseMapper.addEntity(map2);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return ConstantController.ERROR;
		}
		JSONObject json = new JSONObject();
		String rs = null;
		try {
			rs = gradeCalculateService.GradeCalculate("工艺路线", null);
			json.put("result", ConstantController.SUCCESS);
			json.put("score", rs);
		} catch (Exception e) {
			json.put("result", ConstantController.ERROR);
		}
		return json.toString();
	}
	
	/**
	 * 验证账号是否存在
	 * 
	 * @author lanyuan Email：mmm333zzz520@163.com date：2014-2-19
	 * @param name
	 * @return
	 */
	@RequestMapping("routing_isExist")
	@ResponseBody
	public boolean isExist() {
		Session session = SecurityUtils.getSubject().getSession();
		String site = session.getAttribute("site").toString();
		RoutingFormMap routingFormMap = new RoutingFormMap();
		routingFormMap.put("process_route", getPara("process_route"));
		routingFormMap.put("site", site);
		List<RoutingFormMap> list = routingMapper.findByNames(routingFormMap);
		if (list == null || list.size()<=0) {
			return true;
		} else {
			return false;
		}
	}
	
	@RequestMapping("routing_editUI")
	public String editUI(Model model) throws Exception {
		String id = getPara("id");
		RoutingFormMap route = currentSiteRouting(id);
		if (route == null) return "redirect:/denied.jsp";
		model.addAttribute("routing", route);
		return "redirect:/process_designer/gooFlow/routing_edit.html";
	}
	
	@ResponseBody
	@RequestMapping("routing_editEntity")
	@Transactional(readOnly=false)//需要事务操作必须加入此注解
	@SystemLog(module="车间数据维护",methods="工艺路线维护-修改工艺路线")//凡需要处理业务逻辑的.都需要记录操作日志
	public String editEntity() throws Exception {
		RoutingFormMap submitted = getFormMap(RoutingFormMap.class);
		RoutingFormMap existing = currentSiteRouting(submitted.getStr("id"));
		if (existing == null) return ConstantController.ERROR;
		String site = currentSite();
		String oldNo = existing.getStr("process_route");
		String newNo = submitted.getStr("process_route") == null ? oldNo : submitted.getStr("process_route");
		if (Common.isEmpty(newNo) || (currentSiteRoutingByNo(newNo) != null && !newNo.equals(oldNo))) {
			return ConstantController.ERROR;
		}
		String data = submitted.getStr("data");
		if (Common.isEmpty(data)) return ConstantController.ERROR;
		RoutingFormMap update = new RoutingFormMap();
		update.put("id", existing.get("id"));
		update.put("process_route", newNo);
		for (String field : new String[] { "process_route_desc", "state", "allowpass", "data" }) {
			if (submitted.containsKey(field)) update.put(field, submitted.get(field));
		}
		try{
			routingMapper.editEntity(update);
			if (!newNo.equals(oldNo)) {
				JdbcTemplate jdbc = new JdbcTemplate(dataSource);
				jdbc.update("UPDATE ly_shoporder SET process_route=? WHERE site=? AND process_route=?", newNo, site, oldNo);
				jdbc.update("UPDATE ly_sfc_step SET process_route=? WHERE site=? AND process_route=?", newNo, site, oldNo);
			}
			
			//根据工艺路线、站点删除原来的工艺步骤信息
			RoutingStepFormMap map = new RoutingStepFormMap();
			map.put("process_route", "'" + oldNo + "'");
			map.put("site", "'"+site+"'");
			baseMapper.deleteByNames(map);
			
			JSONObject obj = JSONObject.fromObject(data);
			JSONObject lines = (JSONObject) obj.get("lines");
			JSONObject nodes = (JSONObject) obj.get("nodes");
			if (lines.size()==0 && nodes.size()==1) {
				int i=0;
				Set<String> set = nodes.keySet();
				for (String key : set) {
					RoutingStepFormMap map2 = new RoutingStepFormMap();
					map2.put("process_route", newNo);
					map2.put("operation", key);
					map2.put("next_operation", "");
					map2.put("state", 0);
					map2.put("site", site);
					if (i==0) {
						map2.put("operation_order", 0);
					}
					i++;
					baseMapper.addEntity(map2);
				}
			}else {
				if (lines.size()==0) throw new IllegalArgumentException("工艺路线节点未连接");
				Set<String> set = lines.keySet();
				JSONObject checkLine = null;
				int i=0;
				
				for (String key : set) {
					//循环取对象
					JSONObject line = (JSONObject) lines.get(key);
					//取from值
					String operation = line.getString("from");
					//取to值
					String nextOperation = line.getString("to");
					
					//根据to值查找node对象
					JSONObject node = (JSONObject) nodes.get(nextOperation);
					
					//根据to值找from值进行存储
					RoutingStepFormMap map2 = new RoutingStepFormMap();
					map2.put("process_route", newNo);
					map2.put("operation", operation);
					map2.put("next_operation", nextOperation);
					map2.put("state", 0);
					map2.put("site", site);
					if (i==0) {
						map2.put("operation_order", 0);
					}
					i++;
					baseMapper.addEntity(map2);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return "error";
		}
		return "success";
	}
	
	
	@ResponseBody
	@RequestMapping("routing_queryData")
	public String queryData(HttpServletResponse response) throws Exception {
		RoutingFormMap route = currentSiteRoutingByNo(getPara("process_route"));
		if (route == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		JSONObject object = JSONObject.fromObject(route);
		return object.toString();
	}
	
	@ResponseBody
	@RequestMapping("routing_deleteEntity")
	@Transactional(readOnly=false)//需要事务操作必须加入此注解
	@SystemLog(module="车间数据维护",methods="工艺路线维护-删除工艺路线")//凡需要处理业务逻辑的.都需要记录操作日志
	public String deleteEntity() throws Exception {
		try{
			String[] ids = getParaValues("ids");
			if (ids == null || ids.length == 0) return ConstantController.ERROR;
			List<RoutingFormMap> routes = new ArrayList<RoutingFormMap>();
			JdbcTemplate jdbc = new JdbcTemplate(dataSource);
			for (String id : ids) {
				RoutingFormMap route = currentSiteRouting(id);
				if (route == null) return ConstantController.ERROR;
				String number = route.getStr("process_route");
				Integer orders = jdbc.queryForObject("SELECT COUNT(*) FROM ly_shoporder WHERE site=? AND process_route=?",
						new Object[] { currentSite(), number }, Integer.class);
				Integer steps = jdbc.queryForObject("SELECT COUNT(*) FROM ly_sfc_step WHERE site=? AND process_route=?",
						new Object[] { currentSite(), number }, Integer.class);
				if ((orders != null && orders.intValue() > 0) || (steps != null && steps.intValue() > 0)) {
					return ConstantController.ERROR;
				}
				routes.add(route);
			}
			for (RoutingFormMap route : routes) {
				RoutingStepFormMap condition = new RoutingStepFormMap();
				condition.put("process_route", "'" + route.getStr("process_route") + "'");
				condition.put("site", "'" + currentSite() + "'");
				baseMapper.deleteByNames(condition);
				routingMapper.deleteByAttribute("id", String.valueOf(route.get("id")), RoutingFormMap.class);
			}
		}catch(Exception e){
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return "error";
		}
		return "success";
	}
	
	/**
	 * 获取所有未被使用的工序
	 * 
	 * @author lanyuan Email：mmm333zzz520@163.com date：2014-2-19
	 * @param name
	 * @return
	 */
	@RequestMapping("routing_getOperation")
	@ResponseBody
	public Object getOperation() {
		Session session = SecurityUtils.getSubject().getSession();
		String site = session.getAttribute("site").toString();
		List<Map<String, String>> list = operationMapper.getOperationWhereNoUse(site);
		JSONObject json = new JSONObject();
		List<String> rs = new ArrayList<String>();
		for (int i=0;i<list.size();i++) {
			Map<String, String> map = list.get(i);
			rs.add("'"+map.get("operation_no")+"'");
		}
		json.put("success", true);
		json.put("data", rs.toString());
		return json;
	}
}
