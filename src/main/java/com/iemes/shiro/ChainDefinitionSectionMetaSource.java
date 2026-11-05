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
		// 所有资源的访问权限，必须放在最后
		/*section.put("/**", "authc");*/
		/** 如果需要一个用户只能登录一处地方,,修改为 section.put("/**", "authc,kickout,sysUser,user"); **/
		section.put("/**", "authc");
		return section;
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
