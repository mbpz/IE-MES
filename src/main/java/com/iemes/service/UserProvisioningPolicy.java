package com.iemes.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;

import com.iemes.entity.ResFormMap;
import com.iemes.entity.ResRoleFormMap;
import com.iemes.entity.RoleFormMap;
import com.iemes.entity.UserFormMap;
import com.iemes.entity.UserGroupsFormMap;
import com.iemes.mapper.base.BaseMapper;

/** Limits user and role assignments to the current operator's authority. */
@Service
public class UserProvisioningPolicy {
    @Inject
    private BaseMapper baseMapper;

    public List<String> parseRoleIds(String csv) {
        Set<String> ids = new LinkedHashSet<String>();
        if (csv == null || csv.trim().isEmpty()) return new ArrayList<String>();
        for (String part : csv.split(",", -1)) {
            String id = part.trim();
            if (!validId(id)) throw new IllegalArgumentException("无效的角色编号");
            ids.add(id);
        }
        return new ArrayList<String>(ids);
    }

    public void requireAssignment(String site, List<String> roleIds) {
        if (!canAssignSite(site)) {
            throw new IllegalArgumentException("不能为其他站点分配用户");
        }
        for (String roleId : roleIds) {
            if (!canAssignRole(roleId)) throw new IllegalArgumentException("不能分配该角色");
        }
    }

    public boolean canAssignSite(String site) {
        return site != null && !site.trim().isEmpty()
                && (isGlobalAdmin() || site.equals(currentSite()));
    }

    public boolean canAssignRole(String roleId) {
        if (!validId(roleId)) return false;
        RoleFormMap role = baseMapper.findbyFrist("id", roleId, RoleFormMap.class);
        if (role == null) return false;
        if (isGlobalAdmin()) return true;
        if ("1".equals(roleId)) return false;
        ResRoleFormMap condition = new ResRoleFormMap();
        condition.put("roleId", roleId);
        List<ResRoleFormMap> resources = baseMapper.findByNames(condition);
        Subject subject = SecurityUtils.getSubject();
        for (ResRoleFormMap link : resources) {
            Object resourceId = link.get("resId");
            ResFormMap resource = resourceId == null ? null :
                    baseMapper.findbyFrist("id", resourceId.toString(), ResFormMap.class);
            Object key = resource == null ? null : resource.get("resKey");
            if (key == null || !subject.isPermitted(key.toString())) return false;
        }
        return true;
    }

    public boolean canManageUser(String userId) {
        if (!validId(userId)) return false;
        UserFormMap target = baseMapper.findbyFrist("id", userId, UserFormMap.class);
        if (target == null) return false;
        if (isGlobalAdmin()) return true;
        if (!currentSite().equals(target.get("site"))) return false;
        UserGroupsFormMap condition = new UserGroupsFormMap();
        condition.put("userId", userId);
        List<UserGroupsFormMap> roles = baseMapper.findByNames(condition);
        for (UserGroupsFormMap link : roles) {
            Object roleId = link.get("roleId");
            if (roleId == null || !canAssignRole(roleId.toString())) return false;
        }
        return true;
    }

    private boolean isGlobalAdmin() {
        Object userId = SecurityUtils.getSubject().getSession().getAttribute("userSessionId");
        if (userId == null) return false;
        UserGroupsFormMap condition = new UserGroupsFormMap();
        condition.put("userId", userId);
        condition.put("roleId", 1);
        return !baseMapper.findByNames(condition).isEmpty();
    }

    private String currentSite() {
        Object site = SecurityUtils.getSubject().getSession().getAttribute("site");
        return site == null ? "" : site.toString();
    }

    private boolean validId(String id) {
        if (id == null || !id.matches("[1-9][0-9]*")) return false;
        try {
            Integer.parseInt(id);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
