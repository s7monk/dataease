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

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Preconditions;
import io.dataease.commons.utils.StringUtils;
import io.dataease.data.dto.LoginDTO;
import io.dataease.data.dto.RoleWithUserDTO;
import io.dataease.data.dto.UserWithRolesDTO;
import io.dataease.data.model.RoleMenu;
import io.dataease.data.model.SysMenu;
import io.dataease.data.model.SysRole;
import io.dataease.data.model.User;
import io.dataease.data.model.UserRole;
import io.dataease.data.result.exception.BaseException;
import io.dataease.data.result.exception.user.UserDisabledException;
import io.dataease.data.result.exception.user.UserNotExistsException;
import io.dataease.data.result.exception.user.UserPasswordNotMatchException;
import io.dataease.data.vo.UserInfoVO;
import io.dataease.data.vo.UserVO;
import io.dataease.exception.DEException;
import io.dataease.i18n.Translator;
import io.dataease.mapper.UserMapper;
import io.dataease.mapper.UserRoleMapper;
import io.dataease.result.ResultCode;
import io.dataease.service.RoleMenuService;
import io.dataease.service.SysMenuService;
import io.dataease.service.SysRoleService;
import io.dataease.service.TenantService;
import io.dataease.service.UserRoleService;
import io.dataease.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/** UserServiceImpl. */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired private UserMapper userMapper;
    @Autowired private UserRoleService userRoleService;
    @Autowired private SysRoleService sysRoleService;
    @Autowired private RoleMenuService roleMenuService;
    @Autowired private SysMenuService sysMenuService;
    @Autowired private UserRoleMapper userRoleMapper;
    @Autowired private TenantService tenantService;

    @Override
    public UserVO getUserById(Integer id) {
        UserWithRolesDTO userWithRolesDTO = userMapper.selectUserWithRolesById(id);
        if (Objects.nonNull(userWithRolesDTO)) {
            return toVo(userWithRolesDTO);
        }
        return null;
    }

    @Override
    public List<UserVO> listUsers(IPage<User> page, User user) {
        return userMapper.listUsers(page, user).stream()
                .map(this::toVo)
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkUserNameUnique(User user) {
        int userId = user.getId() == null ? -1 : user.getId();
        User info = this.lambdaQuery().eq(User::getUsername, user.getUsername()).one();
        return info == null || info.getId() == userId;
    }

    /**
     * login by username and password.
     *
     * @param loginDTO login info
     * @return {@link String}
     */
    @Override
    public UserInfoVO login(LoginDTO loginDTO) throws Exception {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();

        User user = localLogin(username, password);
        if (!user.getEnabled()) {
            DEException.throwException(ResultCode.USER_ACCOUNT_FORBIDDEN.code(), Translator.get("i18n_user.is.disabled"));
        }
        // query user info
        UserInfoVO userInfoVo = getUserInfoVo(user);
        // todo: Currently do not bind tenants
        /*if (CollectionUtils.isEmpty(userInfoVo.getTenantList())) {
            throw new UserNotBindTenantException();
        }*/
        // Setting login user info to SaSession.
        StpUtil.login(user.getId(), loginDTO.isRememberMe());
        userInfoVo.setPermissions(StpUtil.getPermissionList());
        SaSession saSession = StpUtil.getSession();
        saSession.set(user.getId().toString(), userInfoVo);
        return userInfoVo;
    }

    /**
     * get user info. include user, role, menu. tenant.
     *
     * @param user user
     * @return {@link UserInfoVO}
     */
    private UserInfoVO getUserInfoVo(User user) {
        UserInfoVO userInfoVo = new UserInfoVO();
        userInfoVo.setUser(user);
        userInfoVo.setSaTokenInfo(StpUtil.getTokenInfo());

        // get user role list
        List<SysRole> sysRoles = new ArrayList<>();
        List<UserRole> userRoleList = userRoleService.selectUserRoleListByUserId(user);

        // get role list
        userRoleList.forEach(
                userRole -> {
                    sysRoles.add(sysRoleService.getById(userRole.getRoleId()));
                });
        userInfoVo.setRoleList(sysRoles);
        // get menu list
        List<SysMenu> sysMenus = new ArrayList<>();
        userRoleList.forEach(
                userRole -> {
                    roleMenuService
                            .list(
                                    new LambdaQueryWrapper<RoleMenu>()
                                            .eq(RoleMenu::getRoleId, userRole.getRoleId()))
                            .forEach(
                                    roleMenu -> {
                                        sysMenus.add(sysMenuService.getById(roleMenu.getMenuId()));
                                    });
                });
        userInfoVo.setSysMenuList(sysMenus);

        userInfoVo.setCurrentTenant(tenantService.getById(1));
        return userInfoVo;
    }

    private User localLogin(String username, String password) throws Exception {
        User user =
                this.lambdaQuery()
                        .eq(User::getUsername, username)
                        .one();
        if (user == null) {
            DEException.throwException(ResultCode.USER_NOT_EXIST.code(), Translator.get("i18n_user.not.exist"));
        }

        if (!SaSecureUtil.md5(password).equals(user.getPassword())) {
            DEException.throwException(ResultCode.USER_LOGIN_ERROR.code(), Translator.get("i18n_user.password.error"));
        }

        return user;
    }

    /**
     * Query the list of assigned user roles.
     *
     * @param page the pagination information
     * @param roleWithUserDTO query params
     * @return user list
     */
    @Override
    public List<User> selectAllocatedList(
            IPage<RoleWithUserDTO> page, RoleWithUserDTO roleWithUserDTO) {
        return userMapper.selectAllocatedList(page, roleWithUserDTO);
    }

    /**
     * Query the list of unassigned user roles.
     *
     * @param page the pagination information
     * @param roleWithUserDTO query params
     * @return user list
     */
    @Override
    public List<User> selectUnallocatedList(
            IPage<RoleWithUserDTO> page, RoleWithUserDTO roleWithUserDTO) {
        return userMapper.selectUnallocatedList(page, roleWithUserDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertUser(User user) {
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        this.save(user);
        return insertUserRole(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateUser(User user) {
        this.updateById(user);
        userRoleMapper.deleteUserRoleByUserId(user.getId());
        return insertUserRole(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteUserByIds(Integer userId) {
        userRoleMapper.deleteUserRole(userId);
        return userMapper.deleteById(userId);
    }

    @Override
    public boolean changePassword(User user) {
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        return this.updateById(user);
    }

    @Override
    public boolean updateUserStatus(User user) {
        Preconditions.checkArgument(user != null && user.getId() != null);
        return this.lambdaUpdate()
                .set(User::getEnabled, user.getEnabled())
                .eq(User::getId, user.getId())
                .update();
    }

    @Override
    public int allocateRole(User user) {
        return this.insertUserRole(user);
    }

    private int insertUserRole(User user) {
        int rows = 1;
        if (user.getRoleIds() != null && user.getRoleIds().length > 0) {
            List<UserRole> list = new ArrayList<>();
            for (Integer roleId : user.getRoleIds()) {
                UserRole userRole = new UserRole();
                userRole.setUserId(user.getId());
                userRole.setRoleId(roleId);
                list.add(userRole);
            }
            if (!list.isEmpty()) {
                rows = userRoleMapper.batchUserRole(list);
            }
        }
        return rows;
    }

    private UserVO toVo(UserWithRolesDTO userWithRolesDTO) {
        return UserVO.builder()
                .id(userWithRolesDTO.getId())
                .username(userWithRolesDTO.getUsername())
                .nickname(
                        StringUtils.isNotEmpty(userWithRolesDTO.getNickname())
                                ? userWithRolesDTO.getNickname()
                                : "")
                .mobile(
                        StringUtils.isNotEmpty(userWithRolesDTO.getMobile())
                                ? userWithRolesDTO.getMobile()
                                : "")
                .email(
                        StringUtils.isNotEmpty(userWithRolesDTO.getEmail())
                                ? userWithRolesDTO.getEmail()
                                : "")
                .enabled(userWithRolesDTO.getEnabled())
                .createTime(userWithRolesDTO.getCreateTime())
                .updateTime(userWithRolesDTO.getUpdateTime())
                .roles(userWithRolesDTO.getRoles())
                .build();
    }
}
