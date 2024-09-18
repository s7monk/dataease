/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.dataease.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Preconditions;
import io.dataease.data.model.RoleMenu;
import io.dataease.data.model.SysRole;
import io.dataease.data.model.UserRole;
import io.dataease.data.result.exception.role.RoleInUsedException;
import io.dataease.mapper.RoleMenuMapper;
import io.dataease.mapper.SysRoleMapper;
import io.dataease.mapper.UserRoleMapper;
import io.dataease.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Role Service. */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole>
        implements SysRoleService {

    @Autowired private SysRoleMapper roleMapper;

    @Autowired private RoleMenuMapper roleMenuMapper;

    @Autowired private UserRoleMapper userRoleMapper;

    @Override
    public List<SysRole> listRoles(IPage<SysRole> page, SysRole role) {
        return roleMapper.selectRoleList(page, role);
    }

    @Override
    public List<SysRole> selectRolesByUserId(Integer userId) {
        List<SysRole> userRoles = roleMapper.selectRolePermissionByUserId(userId);
        List<SysRole> roles = this.list();
        for (SysRole role : roles) {
            for (SysRole userRole : userRoles) {
                if (role.getId().intValue() == userRole.getId().intValue()) {
                    role.setFlag(true);
                    break;
                }
            }
        }
        return roles;
    }

    @Override
    public Set<String> selectRolePermissionByUserId(Integer userId) {
        List<SysRole> perms = roleMapper.selectRolePermissionByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (SysRole perm : perms) {
            if (perm != null) {
                permsSet.addAll(Arrays.asList(perm.getRoleKey().trim().split(",")));
            }
        }
        return permsSet;
    }

    @Override
    public List<Integer> selectRoleListByUserId(Integer userId) {
        return roleMapper.selectRoleListByUserId(userId);
    }

    @Override
    public SysRole getRoleById(Integer roleId) {
        return this.getById(roleId);
    }

    @Override
    public boolean checkRoleNameUnique(SysRole role) {
        int roleId = role.getId() == null ? -1 : role.getId();
        SysRole info = this.lambdaQuery().eq(SysRole::getRoleName, role.getRoleName()).one();
        return info == null || info.getId() == roleId;
    }

    @Override
    public boolean checkRoleKeyUnique(SysRole role) {
        int roleId = role.getId() == null ? -1 : role.getId();
        SysRole info = this.lambdaQuery().eq(SysRole::getRoleKey, role.getRoleKey()).one();
        return info == null || info.getId() == roleId;
    }

    @Override
    public boolean checkRoleAllowed(SysRole role) {
        return role.getId() != null && role.getId() == 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRole(SysRole role) {
        List<SysRole> list = this.list();
        role.setSort(list.size() + 1);
        this.save(role);
        return insertRoleMenu(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRole(SysRole role) {
        this.updateById(role);
        roleMenuMapper.deleteRoleMenuByRoleId(role.getId());
        return insertRoleMenu(role);
    }

    public int insertRoleMenu(SysRole role) {
        Long[] mergedMenuIds = mergeMenuIds(role.getMenuIds(), role.getIndeterminateKeys());
        int rows = 1;
        if (mergedMenuIds.length > 0) {
            List<RoleMenu> list = new ArrayList<RoleMenu>();
            for (Long menuId : mergedMenuIds) {
                RoleMenu rm = new RoleMenu();
                rm.setRoleId(role.getId());
                rm.setMenuId(menuId);
                list.add(rm);
            }
            rows = roleMenuMapper.batchRoleMenu(list);
        }
        return rows;
    }

    private Long[] mergeMenuIds(Long[] menuIds, Long[] indeterminateKeys) {
        Set<Long> mergedSet = new HashSet<>();
        if (menuIds != null) {
            mergedSet.addAll(Arrays.asList(menuIds));
        }
        if (indeterminateKeys != null) {
            mergedSet.addAll(Arrays.asList(indeterminateKeys));
        }
        return mergedSet.toArray(new Long[0]);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRoleById(Integer roleId) {
        roleMenuMapper.deleteRoleMenuByRoleId(roleId);
        return roleMapper.deleteById(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRoleByIds(Integer roleIds) {
        SysRole sysRole = new SysRole();
        sysRole.setId(roleIds);
        checkRoleAllowed(sysRole);
        SysRole role = getRoleById(roleIds);
        if (countUserRoleByRoleId(roleIds) > 0) {
            throw new RoleInUsedException(role.getRoleName());
        }
        roleMenuMapper.deleteRoleMenu(roleIds);
        return roleMapper.deleteById(roleIds);
    }

    @Override
    public int deleteAuthUser(UserRole userRole) {
        return userRoleMapper.deleteById(userRole);
    }

    @Override
    public int deleteAuthUsers(Integer roleId, Integer[] userIds) {
        return userRoleMapper.deleteUserRoleInfos(roleId, userIds);
    }

    @Override
    public int insertAuthUsers(Integer roleId, Integer[] userIds) {
        List<UserRole> list = new ArrayList<UserRole>();
        for (Integer userId : userIds) {
            UserRole ur = new UserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            list.add(ur);
        }
        return userRoleMapper.batchUserRole(list);
    }

    @Override
    public int countUserRoleByRoleId(Integer roleId) {
        return userRoleMapper
                .selectCount(new QueryWrapper<UserRole>().eq("role_id", roleId))
                .intValue();
    }

    @Override
    public boolean updateRoleStatus(SysRole role) {
        Preconditions.checkArgument(role != null && role.getId() != null);
        return this.lambdaUpdate()
                .set(SysRole::getEnabled, role.getEnabled())
                .eq(SysRole::getId, role.getId())
                .update();
    }
}
