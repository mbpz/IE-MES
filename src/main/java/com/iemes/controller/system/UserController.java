package com.iemes.controller.system;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.iemes.controller.index.BaseController;
import com.iemes.entity.SiteFormMap;
import com.iemes.entity.UserFormMap;
import com.iemes.entity.UserGroupsFormMap;
import com.iemes.exception.SystemException;
import com.iemes.mapper.UserMapper;
import com.iemes.mapper.site.SiteMapper;
import com.iemes.plugin.PageView;
import com.iemes.service.UserProvisioningPolicy;
import com.iemes.util.Common;
import com.iemes.util.JsonUtils;
import com.iemes.util.POIUtils;
import com.iemes.util.PasswordHelper;

/**
 * 
 * @author lanyuan 2014-11-19
 * @Email: mmm333zzz520@163.com
 * @version 3.0v
 */
@Controller
@RequestMapping("/user/")
public class UserController extends BaseController {
	@Inject
	private UserMapper userMapper;
	
	@Inject
	private SiteMapper siteMapper;
	@Inject
	private UserProvisioningPolicy userProvisioningPolicy;
	
	@RequestMapping("list")
	public String listUI(Model model) throws Exception {
		model.addAttribute("res", findByRes());
		return Common.BACKGROUND_PATH + "/system/user/list";
	}

	@ResponseBody
	@RequestMapping("findByPage")
	public PageView findByPage( String pageNow,
			String pageSize,String column,String sort) throws Exception {
		Session session = SecurityUtils.getSubject().getSession();
		String site = session.getAttribute("site").toString();
		UserFormMap userFormMap = getFormMap(UserFormMap.class);
		userFormMap=toFormMap(userFormMap, pageNow, pageSize,userFormMap.getStr("orderby"));
		userFormMap.put("column", sortableUserColumn(column));
		userFormMap.put("sort", safeSortDirection(sort));
		userFormMap.put("site", site);
        pageView.setRecords(userMapper.findUserPage(userFormMap));//不调用默认分页,调用自已的mapper中findUserPage
        return pageView;
	}
	
	@RequestMapping("/export")
	public void download(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String fileName = "用户列表";
		UserFormMap userFormMap = findHasHMap(UserFormMap.class);
		userFormMap.put("site", SecurityUtils.getSubject().getSession().getAttribute("site"));
		userFormMap.put("column", sortableUserColumn(userFormMap.getStr("column")));
		userFormMap.put("sort", safeSortDirection(userFormMap.getStr("sort")));
		//exportData = 
		// [{"colkey":"sql_info","name":"SQL语句","hide":false},
		// {"colkey":"total_time","name":"总响应时长","hide":false},
		// {"colkey":"avg_time","name":"平均响应时长","hide":false},
		// {"colkey":"record_time","name":"记录时间","hide":false},
		// {"colkey":"call_count","name":"请求次数","hide":false}
		// ]
		String exportData = userFormMap.getStr("exportData");// 列表头的json字符串

		List<Map<String, Object>> listMap = JsonUtils.parseJSONList(exportData);

		List<UserFormMap> lis = userMapper.findUserPage(userFormMap);
		POIUtils.exportToExcel(response, listMap, lis, fileName);
	}

	private String sortableUserColumn(String column) {
		if ("id".equals(column) || "userName".equals(column) || "accountName".equals(column)
				|| "locked".equals(column) || "description".equals(column)
				|| "site".equals(column) || "createTime".equals(column)) return column;
		return null;
	}

	private String safeSortDirection(String sort) {
		return "desc".equalsIgnoreCase(sort) ? "desc" : "asc";
	}

	@RequestMapping("addUI")
	public String addUI(Model model,HttpServletRequest request) throws Exception {
		request.setAttribute("siteInfo", assignableSites());
		return Common.BACKGROUND_PATH + "/system/user/add";
	}

	private List<SiteFormMap> assignableSites() {
		List<SiteFormMap> sites = new ArrayList<SiteFormMap>();
		for (SiteFormMap site : siteMapper.findByWhere(new SiteFormMap())) {
			if (userProvisioningPolicy.canAssignSite(site.getStr("site"))) sites.add(site);
		}
		return sites;
	}

	@ResponseBody
	@RequestMapping("addEntity")
	@SystemLog(module="系统管理",methods="用户管理-新增用户")//凡需要处理业务逻辑的.都需要记录操作日志
	@Transactional(readOnly=false)//需要事务操作必须加入此注解
	public String addEntity(String txtGroupsSelect){
		UserFormMap submitted = getFormMap(UserFormMap.class);
		if (Common.isEmpty(submitted.getStr("accountName")) || Common.isEmpty(submitted.getStr("password"))) return "error";
		List<String> roleIds;
		try {
			roleIds = userProvisioningPolicy.parseRoleIds(txtGroupsSelect);
			userProvisioningPolicy.requireAssignment(submitted.getStr("site"), roleIds);
		} catch (IllegalArgumentException e) {
			return "error";
		}
		try {
			UserFormMap userFormMap = new UserFormMap();
			for (String key : new String[]{"userName", "accountName", "password", "description", "locked", "site"}) {
				if (submitted.get(key) != null) userFormMap.put(key, submitted.get(key));
			}
			PasswordHelper passwordHelper = new PasswordHelper();
			passwordHelper.encryptPassword(userFormMap);
			userMapper.addEntity(userFormMap);//新增后返回新增信息
			if (!roleIds.isEmpty()) {
				UserGroupsFormMap userGroupsFormMap = new UserGroupsFormMap();
				for (String roleId : roleIds) {
					userGroupsFormMap.put("userId", userFormMap.get("id"));
					userGroupsFormMap.put("roleId", roleId);
					userMapper.addEntity(userGroupsFormMap);
				}
			}
		} catch (Exception e) {
			 throw new SystemException("添加账号异常");
		}
		return "success";
	}

	@ResponseBody
	@RequestMapping("deleteEntity")
	@Transactional(readOnly=false)//需要事务操作必须加入此注解
	@SystemLog(module="系统管理",methods="用户管理-删除用户")//凡需要处理业务逻辑的.都需要记录操作日志
	public String deleteEntity() throws Exception {
		String[] ids = getParaValues("ids");
		if (ids == null || ids.length == 0) return "error";
		for (String id : ids) {
			if (!userProvisioningPolicy.canManageUser(id)) return "error";
		}
		for (String id : ids) {
			userMapper.deleteByAttribute("userId", id, UserGroupsFormMap.class);
//			userMapper.deleteByAttribute("userId", id, ResUserFormMap.class);
			userMapper.deleteByAttribute("id", id, UserFormMap.class);
		}
		return "success";
	}

	@RequestMapping("editUI")
	public String editUI(Model model,HttpServletRequest request) throws Exception {
		String id = getPara("id");
		if (!userProvisioningPolicy.canManageUser(id)) return "redirect:/denied.jsp";
		if(Common.isNotEmpty(id)){
			model.addAttribute("user", userMapper.findbyFrist("id", id, UserFormMap.class));
		}
		request.setAttribute("siteInfo", assignableSites());
		return Common.BACKGROUND_PATH + "/system/user/edit";
	}

	@ResponseBody
	@RequestMapping("editEntity")
	@Transactional(readOnly=false)//需要事务操作必须加入此注解
	@SystemLog(module="系统管理",methods="用户管理-修改用户")//凡需要处理业务逻辑的.都需要记录操作日志
	public String editEntity(String txtGroupsSelect) throws Exception {
		UserFormMap submitted = getFormMap(UserFormMap.class);
		String userId = submitted.getStr("id");
		if (!userProvisioningPolicy.canManageUser(userId)) return "error";
		List<String> roleIds;
		try {
			roleIds = userProvisioningPolicy.parseRoleIds(txtGroupsSelect);
			userProvisioningPolicy.requireAssignment(submitted.getStr("site"), roleIds);
		} catch (IllegalArgumentException e) {
			return "error";
		}
		UserFormMap userFormMap = new UserFormMap();
		for (String key : new String[]{"id", "userName", "accountName", "locked", "site", "description"}) {
			if (submitted.get(key) != null) userFormMap.put(key, submitted.get(key));
		}
		userMapper.editEntity(userFormMap);
		userMapper.deleteByAttribute("userId", userId, UserGroupsFormMap.class);
		if(!roleIds.isEmpty()){
			for (String roleId : roleIds) {
				UserGroupsFormMap userGroupsFormMap = new UserGroupsFormMap();
				userGroupsFormMap.put("userId", userFormMap.get("id"));
				userGroupsFormMap.put("roleId", roleId);
				userMapper.addEntity(userGroupsFormMap);
			}
		}
		return "success";
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
		UserFormMap account = userMapper.findbyFrist("accountName", name, UserFormMap.class);
		if (account == null) {
			return true;
		} else {
			return false;
		}
	}
	
	//密码修改
	@RequestMapping("updatePassword")
	public String updatePassword(Model model) throws Exception {
		return Common.BACKGROUND_PATH + "/system/user/updatePassword";
	}
	
	//保存新密码
	@RequestMapping("editPassword")
	@ResponseBody
	@Transactional(readOnly=false)//需要事务操作必须加入此注解
	@SystemLog(module="系统管理",methods="用户管理-修改密码")//凡需要处理业务逻辑的.都需要记录操作日志
	public String editPassword() throws Exception{
		UserFormMap submitted = getFormMap(UserFormMap.class);
		String newPassword = submitted.getStr("newpassword");
		String confirmation = submitted.getStr("confirmpassword");
		if (newPassword == null || newPassword.length() < 6 || !newPassword.equals(confirmation)) {
			return "error";
		}
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		UserFormMap currentUser = (UserFormMap) Common.findUserSession(request);
		if (currentUser == null) {
			return "error";
		}
		if ((submitted.get("id") != null && !currentUser.get("id").toString().equals(submitted.get("id").toString()))
				|| (submitted.get("accountName") != null && !currentUser.get("accountName").equals(submitted.get("accountName")))) {
			return "error";
		}
		UserFormMap passwordUpdate = new UserFormMap();
		passwordUpdate.put("id", currentUser.get("id"));
		passwordUpdate.put("accountName", currentUser.get("accountName"));
		passwordUpdate.put("password", newPassword);
		PasswordHelper passwordHelper = new PasswordHelper();
		passwordHelper.encryptPassword(passwordUpdate);
		passwordUpdate.remove("accountName");
		userMapper.editEntity(passwordUpdate);
		return "success";
	}
}
