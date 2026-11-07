package com.iemes.shiro;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.config.Ini;
import org.springframework.beans.factory.FactoryBean;

import com.iemes.entity.ResFormMap;
import com.iemes.mapper.ResourcesMapper;
import com.iemes.util.ConfigUtils;

/**
 * 产生责任链，确定每个url的访问权限
 * 
 */
public class ChainDefinitionSectionMetaSource implements FactoryBean<Ini.Section> {

	@Inject
	private ResourcesMapper resourcesMapper;

	// 静态资源访问权限
	private String filterChainDefinitions = null;

	public Ini.Section getObject() throws Exception {
		new ConfigUtils().initTableField(resourcesMapper); 
		Ini ini = new Ini();
		// 加载默认的url
		ini.load(filterChainDefinitions);
		Ini.Section section = ini.getSection(Ini.DEFAULT_SECTION_NAME);
		// 循环Resource的url,逐个添加到section中。section就是filterChainDefinitionMap,
		// 里面的键就是链接URL,值就是存在什么条件才能访问该链接
		List<ResFormMap> lists = resourcesMapper.findByWhere(new ResFormMap());
		for (ResFormMap resources : lists) {
			// 构成permission字符串
			if (StringUtils.isNotEmpty(resources.get("resUrl") + "") && StringUtils.isNotEmpty(resources.get("resKey") + "")) {
				String permission = "perms[" + resources.get("resKey") + "]";
				System.out.println(permission);
				// 不对角色进行权限验证
				// 如需要则 permission = "roles[" + resources.getResKey() + "]";
				section.put(resources.get("resUrl") + "", permission);
			}

		}
		// Resource rows cover menu pages, but not their data and write endpoints.
		// Keep self-service password routes available to any authenticated user.
		// Spring MVC accepts extra suffixes before .shtml; match them before module fallbacks.
		section.put("/user/updatePassword*.shtml", "authc");
		section.put("/user/editPassword*.shtml", "authc");
		section.put("/user/findByPage*.shtml", "perms[account]");
		section.put("/user/export*.shtml", "perms[account]");
		section.put("/user/isExist*.shtml", "perms[account_add]");
		section.put("/user/addUI*.shtml", "perms[account_add]");
		section.put("/user/addEntity*.shtml", "perms[account_add]");
		section.put("/user/editUI*.shtml", "perms[account_edit]");
		section.put("/user/editEntity*.shtml", "perms[account_edit]");
		section.put("/user/deleteEntity*.shtml", "perms[account_delete]");
		section.put("/user/**", "perms[account]");
		section.put("/role/findByPage*.shtml", "perms[role]");
		section.put("/role/selRole*.shtml", "authc");
		section.put("/role/addUI*.shtml", "perms[role_add]");
		section.put("/role/addEntity*.shtml", "perms[role_add]");
		section.put("/role/editUI*.shtml", "perms[role_edit]");
		section.put("/role/editEntity*.shtml", "perms[role_edit]");
		section.put("/role/deleteEntity*.shtml", "perms[role_delete]");
		section.put("/role/**", "perms[role]");
		section.put("/message/upload*.shtml", "perms[account_add]");
		section.put("/message/studentInfoUpload*.shtml", "perms[account_add]");
		section.put("/message/userInfoDownLoad*.shtml", "perms[account_add]");
		section.put("/message/**", "perms[base_data]");
		section.put("/point_grade/**", "roles[admin]");
		section.put("/base/getExamInfoList*.shtml", "roles[admin]");
		section.put("/base/getSfcByAssemble*.shtml", "perms[pod_panel]");
		section.put("/PopupTable/**", "perms[pod_panel]");
		section.put("/install.shtml", "roles[admin]");
		addBusinessRules(section);
		// 所有资源的访问权限，必须放在最后
		/*section.put("/**", "authc");*/
		/** 如果需要一个用户只能登录一处地方,,修改为 section.put("/**", "authc,kickout,sysUser,user"); **/
		section.put("/**", "authc");
		return section;
	}

	private void addBusinessRules(Ini.Section section) {
		section.put("/resources/permissions.shtml", "perms[role_perss]");
		section.put("/resources/permissions*.shtml", "perms[role_perss]");
		section.put("/resources/findRes*.shtml", "perms[role_perss]");
		section.put("/resources/addUserRes*.shtml", "perms[role_perss]");
		section.put("/resources/add*.shtml", "perms[ly_resources_add]");
		section.put("/resources/edit*.shtml", "perms[ly_resources_edit]");
		section.put("/resources/delete*.shtml", "perms[ly_resources_delete]");
		section.put("/resources/sortUpdate*.shtml", "perms[ly_resources_edit]");
		section.put("/resources/**", "perms[ly_resources]");
		section.put("/userlogin/**", "perms[ly_log_list]");
		section.put("/log/**", "perms[ly_log]");

		section.put("/workshop_inventory/workshop_inventory_add*.shtml", "perms[add_inventory]");
		section.put("/workshop_inventory/**", "perms[get_item]");
		section.put("/kanban/**", "perms[kanban]");
		section.put("/nc_group/nc_group_add*.shtml", "perms[add_nc_group]");
		section.put("/nc_group/nc_group_edit*.shtml", "perms[edit_nc_group]");
		section.put("/nc_group/nc_group_delete*.shtml", "perms[delete_nc_group]");
		section.put("/nc_group/**", "perms[nc_group]");
		section.put("/qc/nc_handle*.shtml", "perms[nc_handle]");
		section.put("/qc/getNcHandleSFCData*.shtml", "perms[nc_handle]");
		section.put("/qc/addNcHandleMsg*.shtml", "perms[nc_handle]");
		section.put("/qc/nc_add*.shtml", "perms[add_nc_code]");
		section.put("/qc/nc_edit*.shtml", "perms[edit_nc_code]");
		section.put("/qc/nc_delete*.shtml", "perms[delete_nc_code]");
		section.put("/qc/**", "perms[nc_code]");

		section.put("/item_resource/item_add*.shtml", "perms[add_item]");
		section.put("/item_resource/item_edit*.shtml", "perms[edit_item]");
		section.put("/item_resource/item_delete*.shtml", "perms[delete_item]");
		section.put("/item_resource/item_*.shtml", "perms[item_manager]");
		section.put("/item_resource/bom_add*.shtml", "perms[add_bom]");
		section.put("/item_resource/bom_edit*.shtml", "perms[edit_bom]");
		section.put("/item_resource/bom_delete*.shtml", "perms[delete_bom]");
		section.put("/item_resource/bom_*.shtml", "perms[itembom_manager]");
		section.put("/item_resource/**", "perms[item]");

		section.put("/workshop/operation_resource_add*.shtml", "perms[add_operation_resource]");
		section.put("/workshop/operation_resource_edit*.shtml", "perms[edit_operation_resource]");
		section.put("/workshop/operation_resource_delete*.shtml", "perms[delete_operation_resource]");
		section.put("/workshop/operation_resource_*.shtml", "perms[operation_resource]");
		section.put("/workshop/operation_add*.shtml", "perms[app_operation]");
		section.put("/workshop/operation_edit*.shtml", "perms[edit_operation]");
		section.put("/workshop/operation_delete*.shtml", "perms[delete_operation]");
		section.put("/workshop/operation_*.shtml", "perms[operation]");
		section.put("/workshop/workcenter_add*.shtml", "perms[workcenter_add]");
		section.put("/workshop/workcenter_edit*.shtml", "perms[workcenter_edit]");
		section.put("/workshop/workcenter_delete*.shtml", "perms[workcenter_delete]");
		section.put("/workshop/workcenter_*.shtml", "perms[workcenter]");
		section.put("/workshop/routing_add*.shtml", "perms[process_route_add]");
		section.put("/workshop/routing_edit*.shtml", "perms[process_route_edit]");
		section.put("/workshop/routing_delete*.shtml", "perms[process_route_delete]");
		section.put("/workshop/routing_*.shtml", "perms[routing]");
		section.put("/workshop/site_add*.shtml", "perms[site_add]");
		section.put("/workshop/site_edit*.shtml", "perms[site_edit]");
		section.put("/workshop/site_delete*.shtml", "perms[site_delete]");
		section.put("/workshop/site_*.shtml", "perms[site]");
		section.put("/workshop/isExist*.shtml", "perms[site]");
		section.put("/workshop/**", "perms[base_data]");

		section.put("/produce/pod_function_add*.shtml", "perms[pod_function_add]");
		section.put("/produce/pod_function_edit*.shtml", "perms[pod_function_edit]");
		section.put("/produce/pod_function_delete*.shtml", "perms[pod_function_del]");
		section.put("/produce/pod_function_*.shtml", "perms[pod_function]");
		section.put("/produce/podfunction_*.shtml", "perms[pod_function]");
		section.put("/produce/isExistButton*.shtml", "perms[pod_button]");
		section.put("/produce/isExist*.shtml", "perms[pod_function]");
		section.put("/produce/pod_button_add*.shtml", "perms[pod_button_add]");
		section.put("/produce/pod_button_edit*.shtml", "perms[pod_button_edit]");
		section.put("/produce/pod_button_delete*.shtml", "perms[pod_button_del]");
		section.put("/produce/pod_button_*.shtml", "perms[pod_button]");
		section.put("/produce/selButton*.shtml", "perms[pod_button]");
		section.put("/produce/**", "perms[pod_panel]");

		section.put("/product_plan/shoporder_maintenance_add*.shtml", "perms[add_shoporder]");
		section.put("/product_plan/**", "perms[shoporder_make]");
		section.put("/plan/deliver_number*.shtml", "perms[product_plan]");
		section.put("/plan/erp_order*.shtml", "perms[product_plan]");
		section.put("/plan/**", "perms[shoporder_Issued]");
		section.put("/report/scheduler_reportUi*.shtml", "perms[scheduler_report]");
		section.put("/report/inventory_reportUi*.shtml", "perms[in_inventory_report]");
		section.put("/report/productionRecord_reportUI*.shtml", "perms[prouction_record_report]");
		section.put("/report/sfc_assemblyInfo_listUI*.shtml", "perms[prouction_record_report]");
		section.put("/report/get_productionRecord_*.shtml", "perms[prouction_record_report]");
		section.put("/report/getProductionRecord*.shtml", "perms[prouction_record_report]");
		section.put("/report/get_sfc_assemblyInfo_list*.shtml", "perms[prouction_record_report]");
		section.put("/report/sfc_assembly_info_listData*.shtml", "perms[prouction_record_report]");
		// getReportData selects several reports by parameter; check that selection in the controller.
		section.put("/report/getReportData*.shtml", "authc");
		section.put("/report/**", "perms[report]");
		section.put("/capacity_report/**", "perms[report]");
	}

	/**
	 * 通过filterChainDefinitions对默认的url过滤定义
	 * 
	 * @param filterChainDefinitions
	 *            默认的url过滤定义
	 */
	public void setFilterChainDefinitions(String filterChainDefinitions) {
		this.filterChainDefinitions = filterChainDefinitions;
	}

	public Class<?> getObjectType() {
		return this.getClass();
	}

	public boolean isSingleton() {
		return false;
	}
}
