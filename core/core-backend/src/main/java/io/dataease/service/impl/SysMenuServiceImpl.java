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

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.dataease.commons.constants.Constants;
import io.dataease.commons.enums.MenuType;
import io.dataease.commons.utils.StringUtils;
import io.dataease.data.model.SysMenu;
import io.dataease.data.model.User;
import io.dataease.data.tree.TreeSelect;
import io.dataease.data.vo.MenuMeta;
import io.dataease.data.vo.RouterVO;
import io.dataease.mapper.RoleMenuMapper;
import io.dataease.mapper.SysMenuMapper;
import io.dataease.mapper.SysRoleMapper;
import io.dataease.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Menu service. */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu>
        implements SysMenuService {
    public static final String PREMISSION_STRING = "perms[\"{0}\"]";

    @Autowired private SysMenuMapper menuMapper;

    @Autowired private SysRoleMapper roleMapper;

    @Autowired private RoleMenuMapper roleMenuMapper;

    @Override
    public List<SysMenu> selectMenuList() {
        return selectMenuList(new SysMenu());
    }

    @Override
    public List<SysMenu> selectMenuList(SysMenu menu) {
        List<SysMenu> menuList;
        int userId = StpUtil.getLoginIdAsInt();
        if (User.isAdmin(userId)) {
            menuList = menuMapper.selectMenuList(menu);
        } else {
            menuList = menuMapper.selectMenuListByUserId(menu, userId);
        }
        return menuList;
    }

    @Override
    public Set<String> selectMenuPermsByUserId(Integer userId) {
        List<String> perms = menuMapper.selectMenuPermsByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (StringUtils.isNotEmpty(perm)) {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    @Override
    public Set<String> selectMenuPermsByRoleId(Integer roleId) {
        List<String> perms = menuMapper.selectMenuPermsByRoleId(roleId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (StringUtils.isNotEmpty(perm)) {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    @Override
    public List<SysMenu> selectMenuTreeByUserId(Integer userId) {
        List<SysMenu> menus = null;
        if (userId != null && userId == 1) {
            menus = menuMapper.selectMenuTreeAll();
        } else {
            menus = menuMapper.selectMenuTreeByUserId(userId);
        }
        return getChildPerms(menus, 0);
    }

    @Override
    public List<Long> selectMenuListByRoleId(Integer roleId) {
        return menuMapper.selectMenuListByRoleId(roleId);
    }

   /* @Override
    public List<RouterVO> buildMenus(List<SysMenu> menus) {
        List<RouterVO> routers = new LinkedList<RouterVO>();
        for (SysMenu menu : menus) {
            RouterVO router = new RouterVO();
            router.setHidden("1".equals(menu.getVisible()));
            router.setName(getRouteName(menu));
            router.setPath(getRouterPath(menu));
            router.setComponent(getComponent(menu));
            router.setQuery(menu.getQuery());
            router.setMeta(
                    new MenuMeta(
                            menu.getMenuName(),
                            menu.getIcon(),
                            menu.getIsCache() == 1,
                            menu.getPath()));
            List<SysMenu> cMenus = menu.getChildren();
            if (!CollectionUtils.isEmpty(cMenus) && MenuType.DIR.getType().equals(menu.getType())) {
                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                router.setChildren(buildMenus(cMenus));
            } else if (isMenuFrame(menu)) {
                router.setMeta(null);
                List<RouterVO> childrenList = new ArrayList<RouterVO>();
                RouterVO children = new RouterVO();
                children.setPath(menu.getPath());
                children.setComponent(menu.getComponent());
                children.setName(StringUtils.capitalize(menu.getPath()));
                children.setMeta(
                        new MenuMeta(
                                menu.getMenuName(),
                                menu.getIcon(),
                                menu.getIsCache() == 1,
                                menu.getPath()));
                children.setQuery(menu.getQuery());
                childrenList.add(children);
                router.setChildren(childrenList);
            } else if (menu.getParentId() == 0 && isInnerLink(menu)) {
                router.setMeta(new MenuMeta(menu.getMenuName(), menu.getIcon()));
                router.setPath("/");
                List<RouterVO> childrenList = new ArrayList<RouterVO>();
                RouterVO children = new RouterVO();
                String routerPath = innerLinkReplaceEach(menu.getPath());
                children.setPath(routerPath);
                children.setComponent(Constants.INNER_LINK);
                children.setName(StringUtils.capitalize(routerPath));
                children.setMeta(new MenuMeta(menu.getMenuName(), menu.getIcon(), menu.getPath()));
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            routers.add(router);
        }
        return routers;
    }*/

    @Override
    public List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        List<SysMenu> returnList = new ArrayList<SysMenu>();
        List<Long> tempList = menus.stream().map(SysMenu::getId).collect(Collectors.toList());
        for (Iterator<SysMenu> iterator = menus.iterator(); iterator.hasNext(); ) {
            SysMenu menu = (SysMenu) iterator.next();
            if (!tempList.contains(menu.getPid())) {
                recursionFn(menus, menu);
                returnList.add(menu);
            }
        }
        if (returnList.isEmpty()) {
            returnList = menus;
        }
        return returnList;
    }

    @Override
    public List<TreeSelect> buildMenuTreeSelect(List<SysMenu> menus) {
        List<SysMenu> menuTrees = buildMenuTree(menus);
        return menuTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    @Override
    public SysMenu selectMenuById(Long menuId) {
        return menuMapper.selectMenuById(menuId);
    }

    @Override
    public boolean hasChildByMenuId(Long menuId) {
        long result = menuMapper.hasChildByMenuId(menuId);
        return result > 0;
    }

    @Override
    public boolean checkMenuExistRole(Long menuId) {
        int result = roleMenuMapper.checkMenuExistRole(menuId);
        return result > 0;
    }

    @Override
    public boolean insertMenu(SysMenu menu) {
        return this.save(menu);
    }

    @Override
    public boolean updateMenu(SysMenu menu) {
        return this.updateById(menu);
    }

    @Override
    public boolean deleteMenuById(Long menuId) {
        return this.removeById(menuId);
    }

    @Override
    public boolean checkMenuNameUnique(SysMenu menu) {
        Long menuId = menu.getId() == null ? -1 : menu.getId();
        SysMenu info = menuMapper.checkMenuNameUnique(menu.getName(), menu.getPid());
        return info != null && !menuId.equals(info.getId());
    }

    public String getRouteName(SysMenu menu) {
        String routerName = StringUtils.capitalize(menu.getPath());
        if (isMenuFrame(menu)) {
            routerName = StringUtils.EMPTY;
        }
        return routerName;
    }

    public String getRouterPath(SysMenu menu) {
        String routerPath = menu.getPath();
        if (menu.getPid() != 0 && isInnerLink(menu)) {
            routerPath = innerLinkReplaceEach(routerPath);
        }
        if (isMenuFrame(menu)) {
            routerPath = "/";
        }
        return routerPath;
    }

    public String getComponent(SysMenu menu) {
        String component = Constants.LAYOUT;
        if (StringUtils.isNotEmpty(menu.getComponent()) && !isMenuFrame(menu)) {
            component = menu.getComponent();
        } else if (StringUtils.isEmpty(menu.getComponent())
                && menu.getPid() != 0
                && isInnerLink(menu)) {
            component = Constants.INNER_LINK;
        } else if (StringUtils.isEmpty(menu.getComponent()) && isParentView(menu)) {
            component = Constants.PARENT_VIEW;
        }
        return component;
    }

    public boolean isMenuFrame(SysMenu menu) {
        return false;
    }

    public boolean isInnerLink(SysMenu menu) {
        return false;
    }

    public boolean isParentView(SysMenu menu) {
        return menu.getPid() != 0 && MenuType.DIR.getType().equals(menu.getType());
    }

    public List<SysMenu> getChildPerms(List<SysMenu> list, int parentId) {
        List<SysMenu> returnList = new ArrayList<SysMenu>();
        for (SysMenu t : list) {
            if (t.getPid() == parentId) {
                recursionFn(list, t);
                returnList.add(t);
            }
        }
        return returnList;
    }

    private void recursionFn(List<SysMenu> list, SysMenu t) {
        List<SysMenu> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysMenu tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    private List<SysMenu> getChildList(List<SysMenu> list, SysMenu t) {
        List<SysMenu> tlist = new ArrayList<SysMenu>();
        for (SysMenu n : list) {
            if (n.getPid().equals(t.getId())) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    private boolean hasChild(List<SysMenu> list, SysMenu t) {
        return getChildList(list, t).size() > 0;
    }

    public String innerLinkReplaceEach(String path) {
        return StringUtils.replaceEach(
                path,
                new String[] {Constants.HTTP, Constants.HTTPS, Constants.WWW, "."},
                new String[] {"", "", "", "/"});
    }
}
