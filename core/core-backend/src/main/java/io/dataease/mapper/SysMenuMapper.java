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

package io.dataease.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.dataease.data.model.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** Menu mapper. */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<SysMenu> selectMenuList(@Param("menu") SysMenu menu);

    List<String> selectMenuPerms();

    List<SysMenu> selectMenuListByUserId(
            @Param("menu") SysMenu menu, @Param("userId") Integer userId);

    List<String> selectMenuPermsByRoleId(Integer roleId);

    List<String> selectMenuPermsByUserId(Integer userId);

    List<SysMenu> selectMenuTreeAll();

    List<SysMenu> selectMenuTreeByUserId(Integer userId);

    List<Long> selectMenuListByRoleId(Integer roleId);

    SysMenu selectMenuById(Long menuId);

    Long hasChildByMenuId(Long menuId);

    SysMenu checkMenuNameUnique(
            @Param("name") String name, @Param("pid") Long pid);
}
