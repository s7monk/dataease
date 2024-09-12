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
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.dataease.commons.utils.PageSupport;
import io.dataease.data.model.User;
import io.dataease.data.result.PageR;
import io.dataease.data.result.R;
import io.dataease.data.result.enums.Status;
import io.dataease.data.vo.UserVO;
import io.dataease.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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

import java.util.List;

import static io.dataease.data.result.enums.Status.USER_NOT_EXIST;

/** User api controller. */
@Slf4j
@Validated
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired private UserService userService;

    @SaCheckPermission("system:user:query")
    @GetMapping("/{id}")
    public R<UserVO> getUser(@PathVariable("id") Integer id) {
        UserVO user = userService.getUserById(id);
        if (user == null) {
            return R.failed(USER_NOT_EXIST);
        }
        return R.succeed(user);
    }

    @SaCheckPermission("system:user:list")
    @GetMapping("/list")
    public PageR<UserVO> listUsers(User user) {
        IPage<User> page = PageSupport.startPage();
        List<UserVO> list = userService.listUsers(page, user);
        return PageR.<UserVO>builder().success(true).total(page.getTotal()).data(list).build();
    }

    @SaCheckPermission("system:user:add")
    @PostMapping
    public R<Void> add(@Valid @RequestBody User user) {
        if (!userService.checkUserNameUnique(user)) {
            return R.failed(Status.USER_NAME_ALREADY_EXISTS, user.getUsername());
        }

        return userService.insertUser(user) > 0 ? R.succeed() : R.failed();
    }

    @SaCheckPermission("system:user:update")
    @PutMapping
    public R<Void> update(@Valid @RequestBody User user) {
        if (!userService.checkUserNameUnique(user)) {
            return R.failed(Status.USER_NAME_ALREADY_EXISTS, user.getUsername());
        }

        return userService.updateUser(user) > 0 ? R.succeed() : R.failed();
    }

    @SaCheckPermission("system:user:delete")
    @DeleteMapping("/{userId}")
    public R<Void> delete(@PathVariable Integer userId) {
        return userService.deleteUserByIds(userId) > 0 ? R.succeed() : R.failed();
    }

    @SaCheckPermission("system:user:change:password")
    @PostMapping("/change/password")
    public R<Void> changePassword(@Validated @RequestBody User user) {
        if (userService.getUserById(user.getId()) == null) {
            return R.failed(USER_NOT_EXIST);
        }
        return userService.changePassword(user) ? R.succeed() : R.failed();
    }

    @SaCheckPermission("system:user:update")
    @PutMapping("/changeStatus")
    public R<Void> changeStatus(@RequestBody User user) {
        return userService.updateUserStatus(user) ? R.succeed() : R.failed();
    }

    @SaCheckPermission("system:user:update")
    @PostMapping("/allocate")
    public R<Void> allocateRole(@RequestBody User user) {
        return userService.allocateRole(user) > 0 ? R.succeed() : R.failed();
    }
}
