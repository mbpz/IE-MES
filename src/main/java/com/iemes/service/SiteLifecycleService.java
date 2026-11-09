package com.iemes.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Keeps a site code and all existing site-owned rows consistent. */
@Service
public class SiteLifecycleService {
	private final JdbcTemplate jdbc;

	@Inject
	public SiteLifecycleService(DataSource dataSource) {
		this.jdbc = new JdbcTemplate(dataSource);
	}

	public boolean exists(String site) {
		if (site == null || site.trim().isEmpty()) return false;
		Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM ly_site WHERE site=?",
				new Object[] { site.trim() }, Integer.class);
		return count != null && count.intValue() > 0;
	}

	@Transactional
	public void create(String site, String name, String description, String state, String user) {
		String code = required(site, 255, "站点编号不能为空或过长");
		if (exists(code)) throw new IllegalArgumentException("站点编号已存在");
		jdbc.update("INSERT INTO ly_site (site,siteName,siteDescription,state,byUser) VALUES (?,?,?,?,?)",
				code, required(name, 255, "站点名称不能为空或过长"), optional(description, 255),
				state(state), required(user, 255, "用户无效"));
	}

	@Transactional
	public void edit(String id, String newSite, String name, String description, String state) {
		long numericId = id(id);
		Map<String, Object> existing = site(numericId);
		String oldCode = String.valueOf(existing.get("site"));
		String newCode = newSite == null ? oldCode : required(newSite, 255, "站点编号不能为空或过长");
		String newName = name == null ? String.valueOf(existing.get("siteName"))
				: required(name, 255, "站点名称不能为空或过长");
		String newDescription = description == null ? (String) existing.get("siteDescription")
				: optional(description, 255);
		int newState = state == null ? ((Number) existing.get("state")).intValue() : state(state);
		if (!oldCode.equals(newCode)) {
			if (exists(newCode)) throw new IllegalArgumentException("站点编号已存在");
			for (String[] reference : siteReferences()) {
				jdbc.update("UPDATE `" + reference[0] + "` SET `" + reference[1]
						+ "`=? WHERE `" + reference[1] + "`=?", newCode, oldCode);
			}
		}
		jdbc.update("UPDATE ly_site SET site=?,siteName=?,siteDescription=?,state=? WHERE id=?",
				newCode, newName, newDescription, newState, numericId);
	}

	@Transactional
	public void delete(String[] ids) {
		if (ids == null || ids.length == 0) throw new IllegalArgumentException("请选择站点");
		Set<Long> uniqueIds = new LinkedHashSet<Long>();
		for (String value : ids) uniqueIds.add(id(value));
		List<String[]> references = siteReferences();
		for (Long numericId : uniqueIds) {
			String code = String.valueOf(site(numericId).get("site"));
			for (String[] reference : references) {
				Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM `" + reference[0]
						+ "` WHERE `" + reference[1] + "`=?", new Object[] { code }, Integer.class);
				if (count != null && count.intValue() > 0) {
					throw new IllegalArgumentException("站点仍有业务数据，不能删除");
				}
			}
		}
		for (Long numericId : uniqueIds) jdbc.update("DELETE FROM ly_site WHERE id=?", numericId);
	}

	private Map<String, Object> site(long id) {
		try {
			return jdbc.queryForMap("SELECT site,siteName,siteDescription,state FROM ly_site WHERE id=? FOR UPDATE", id);
		} catch (EmptyResultDataAccessException missing) {
			throw new IllegalArgumentException("站点不存在");
		}
	}

	private List<String[]> siteReferences() {
		List<Map<String, Object>> columns = jdbc.queryForList(
				"SELECT table_name,column_name FROM information_schema.columns "
				+ "WHERE table_schema=DATABASE() AND column_name IN ('site','siteBo') "
				+ "AND table_name<>'ly_site' ORDER BY table_name,column_name");
		List<String[]> references = new ArrayList<String[]>();
		for (Map<String, Object> column : columns) {
			String table = String.valueOf(column.get("table_name"));
			String field = String.valueOf(column.get("column_name"));
			if (!table.matches("[A-Za-z0-9_]+") || !field.matches("[A-Za-z0-9_]+")) {
				throw new IllegalStateException("站点字段名无效");
			}
			references.add(new String[] { table, field });
		}
		return references;
	}

	private long id(String value) {
		if (value == null || !value.matches("[0-9]+")) throw new IllegalArgumentException("站点ID无效");
		try {
			long numeric = Long.parseLong(value);
			if (numeric <= 0) throw new NumberFormatException();
			return numeric;
		} catch (NumberFormatException invalid) {
			throw new IllegalArgumentException("站点ID无效");
		}
	}

	private String required(String value, int maxLength, String message) {
		if (value == null || value.trim().isEmpty() || value.trim().length() > maxLength) {
			throw new IllegalArgumentException(message);
		}
		return value.trim();
	}

	private String optional(String value, int maxLength) {
		if (value == null || value.trim().isEmpty()) return null;
		if (value.trim().length() > maxLength) throw new IllegalArgumentException("输入内容过长");
		return value.trim();
	}

	private int state(String value) {
		if (!"0".equals(value) && !"1".equals(value)) throw new IllegalArgumentException("站点状态无效");
		return Integer.parseInt(value);
	}
}
