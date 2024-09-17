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

package io.dataease.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.dataease.data.model.SysMenu;
import io.dataease.data.result.R;
import io.dataease.data.result.enums.Status;
import io.dataease.data.tree.TreeSelect;
import io.dataease.data.vo.MenuMeta;
import io.dataease.data.vo.MenuVO;
import io.dataease.data.vo.RoleMenuTreeselectVO;
import io.dataease.data.vo.RouterVO;
import io.dataease.i18n.I18n;
import io.dataease.license.config.XpackInteract;
import io.dataease.mapper.SysMenuMapper;
import io.dataease.menu.bo.MenuTreeNode;
import io.dataease.menu.dao.auto.entity.CoreMenu;
import io.dataease.service.SysMenuService;
import io.dataease.utils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** menu controller. */
@RestController
@RequestMapping("/menu")
public class SysMenuController {

    private static final String I18N_PREFIX = "i18n_menu.";

    private final static int ROOTID = 0;

    @Autowired private SysMenuService menuService;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @SaCheckPermission("system:menu:list")
    @GetMapping("/list")
    public R<List<SysMenu>> list(SysMenu menu) {
        List<SysMenu> menus = menuService.selectMenuList(menu);
        return R.succeed(menus);
    }

    @SaCheckPermission("system:menu:query")
    @GetMapping(value = "/{menuId}")
    public R<SysMenu> getInfo(@PathVariable Long menuId) {
        return R.succeed(menuService.selectMenuById(menuId));
    }

    @GetMapping("/treeselect")
    public R<List<TreeSelect>> treeselect(SysMenu menu) {
        List<SysMenu> menus = menuService.selectMenuList(menu);
        return R.succeed(menuService.buildMenuTreeSelect(menus));
    }

    @GetMapping(value = "/roleMenuTreeselect/{roleId}")
    public R<RoleMenuTreeselectVO> roleMenuTreeselect(@PathVariable("roleId") Integer roleId) {
        List<SysMenu> menus = menuService.selectMenuList();

        List<TreeSelect> treeMenus = menuService.buildMenuTreeSelect(menus);
        List<Long> checkedKeys = menuService.selectMenuListByRoleId(roleId);
        return R.succeed(new RoleMenuTreeselectVO(checkedKeys, treeMenus));
    }

    @SaCheckPermission("system:menu:add")
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysMenu menu) {
        if (!menuService.checkMenuNameUnique(menu)) {
            return R.failed(Status.MENU_NAME_IS_EXIST, menu.getName());
        }
        return menuService.insertMenu(menu) ? R.succeed() : R.failed();
    }

    @SaCheckPermission("system:menu:update")
    @PutMapping
    public R<Void> update(@Validated @RequestBody SysMenu menu) {
        if (!menuService.checkMenuNameUnique(menu)) {
            return R.failed(Status.MENU_NAME_IS_EXIST, menu.getName());
        }
        return menuService.updateMenu(menu) ? R.succeed() : R.failed();
    }

    @SaCheckPermission("system:menu:delete")
    @DeleteMapping("/{menuId}")
    public R<Void> delete(@PathVariable("menuId") Long menuId) {
        if (menuService.hasChildByMenuId(menuId) || menuService.checkMenuExistRole(menuId)) {
            return R.failed(Status.MENU_IN_USED);
        }
        return menuService.deleteMenuById(menuId) ? R.succeed() : R.failed();
    }

    /*@GetMapping("/getRouters")
    public R<List<RouterVO>> getRouters() {
        int userId = StpUtil.getLoginIdAsInt();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return R.succeed(menuService.buildMenus(menus));
    }*/

    @SaIgnore
    @I18n
    @GetMapping("/query")
    List<MenuVO> query() {
        List<SysMenu> coreMenus = this.coreMenus();
        return this.query(new ArrayList<>(coreMenus));
    }

    public List<MenuVO> query(List<SysMenu> coreMenus) {
        List<SysMenu> treeNodes = buildPOTree(coreMenus);
        return convertTree(treeNodes);
    }

    public List<SysMenu> coreMenus() {
        QueryWrapper<SysMenu> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("menu_sort");
        return sysMenuMapper.selectList(wrapper);
    }


    private List<SysMenu> buildPOTree(List<SysMenu> coreMenus) {
        List<SysMenu> result = new ArrayList<>();
        Map<Long, List<SysMenu>> childMap = coreMenus.stream().collect(Collectors.groupingBy(SysMenu::getPid));
        coreMenus.forEach(po -> {
            po.setChildren(childMap.get(po.getId()));
            if (po.getPid() == ROOTID) {
                result.add(po);
            }
        });
        return result;
    }

    private List<MenuVO> convertTree(List<SysMenu> roots) {
        List<MenuVO> result = new ArrayList<>();
        for (SysMenu menuTreeNode : roots) {
            MenuVO vo = convert(menuTreeNode);
            List<SysMenu> children = null;
            if (CollectionUtils.isNotEmpty(children = menuTreeNode.getChildren())) {
                vo.setChildren(convertTree(children));
            }
            if (CollectionUtils.isNotEmpty(vo.getChildren()) || menuTreeNode.getType() != 1) {
                result.add(vo);
            }
        }
        return result;
    }

    private MenuVO convert(SysMenu coreMenu) {

        if (ROOTID != coreMenu.getPid() && StringUtils.startsWith(coreMenu.getPath(), "/")) {
            coreMenu.setPath(coreMenu.getPath().substring(1));
        }
        MenuVO menuVO = new MenuVO();
        BeanUtils.copyBean(menuVO, coreMenu, "children");
        MenuMeta meta = new MenuMeta();
        meta.setTitle(I18N_PREFIX + coreMenu.getName());
        meta.setIcon(coreMenu.getIcon());
        menuVO.setMeta(meta);

        menuVO.setPlugin(isXpackMenu(coreMenu));
        return menuVO;
    }

    private boolean isXpackMenu(SysMenu coreMenu) {
        if (coreMenu.getId().equals(21L)) return false;
        return false;
    }
}
