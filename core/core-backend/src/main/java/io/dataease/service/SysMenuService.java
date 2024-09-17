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

package io.dataease.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.dataease.data.model.SysMenu;
import io.dataease.data.tree.TreeSelect;
import io.dataease.data.vo.RouterVO;

import java.util.List;
import java.util.Set;

/** Menu service. */
public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> selectMenuList();

    List<SysMenu> selectMenuList(SysMenu menu);

    Set<String> selectMenuPermsByUserId(Integer userId);

    Set<String> selectMenuPermsByRoleId(Integer roleId);

    List<SysMenu> selectMenuTreeByUserId(Integer userId);

    List<Long> selectMenuListByRoleId(Integer roleId);

    // List<RouterVO> buildMenus(List<SysMenu> menus);

    List<SysMenu> buildMenuTree(List<SysMenu> menus);

    List<TreeSelect> buildMenuTreeSelect(List<SysMenu> menus);

    SysMenu selectMenuById(Long menuId);

    boolean hasChildByMenuId(Long menuId);

    boolean checkMenuExistRole(Long menuId);

    boolean insertMenu(SysMenu menu);

    boolean updateMenu(SysMenu menu);

    boolean deleteMenuById(Long menuId);

    boolean checkMenuNameUnique(SysMenu menu);
}
