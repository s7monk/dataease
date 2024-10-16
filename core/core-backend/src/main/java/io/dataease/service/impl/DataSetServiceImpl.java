package io.dataease.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.dataease.data.model.DataSet;
import io.dataease.data.model.RoleResource;
import io.dataease.data.model.UserResource;
import io.dataease.mapper.DataSetMapper;
import io.dataease.service.DataSetService;
import io.dataease.service.RoleResourceService;
import io.dataease.service.SysRoleService;
import io.dataease.service.UserResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DataSetServiceImpl extends ServiceImpl<DataSetMapper, DataSet> implements DataSetService {

    @Autowired
    private DataSetMapper dataSetMapper;

    @Autowired
    private UserResourceService userResourceService;

    @Autowired
    private RoleResourceService roleResourceService;

    @Autowired
    private SysRoleService roleService;

    @Override
    public List<DataSet> getDataSets() {
        String userId = StpUtil.isLogin() ? StpUtil.getLoginIdAsString() : "1";
        if (Objects.equals(userId, "1")) {
            QueryWrapper<DataSet> queryWrapper = new QueryWrapper<>();
            return dataSetMapper.selectList(queryWrapper);
        } else {
            return selectDataSetByIds();
        }
    }

    @Override
    public List<DataSet> selectDataSetByIds() {
        QueryWrapper<DataSet> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", selectAuthorizedResourceIds());
        return dataSetMapper.selectList(queryWrapper);
    }

    @Override
    public List<DataSet> selectDataSetByLoginId() {
        String userId = StpUtil.isLogin() ? StpUtil.getLoginIdAsString() : "1";
        QueryWrapper<DataSet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("create_by", userId);
        queryWrapper.orderByDesc("create_time");
        return dataSetMapper.selectList(queryWrapper);
    }

    @Override
    public List<String> selectAuthorizedResourceIds() {
        int userId = StpUtil.isLogin() ? StpUtil.getLoginIdAsInt() : 1;
        List<Integer> roles = roleService.selectRoleListByUserId(userId);
        List<RoleResource> roleResources = roleResourceService.selectAuthorizedResourceByRoleIds(roles, 2);
        List<UserResource> userResources = userResourceService.selectAuthorizedResourceByUid(userId, 2);
        List<DataSet> dataVisualizations = selectDataSetByLoginId();

        Set<String> resourceIds = roleResources.stream()
                .map(RoleResource::getResourceId)
                .collect(Collectors.toSet());

        resourceIds.addAll(userResources.stream()
                .map(UserResource::getResourceId)
                .collect(Collectors.toSet()));

        resourceIds.addAll(dataVisualizations.stream()
                .map(item -> String.valueOf(item.getId()))
                .collect(Collectors.toSet()));

        if (resourceIds.isEmpty()) {
            return Collections.singletonList("0");
        }

        return new ArrayList<>(resourceIds);
    }

    @Override
    public List<String> selectAuthorizedResourceIdsWithSelect() {
        int userId = StpUtil.isLogin() ? StpUtil.getLoginIdAsInt() : 1;
        List<Integer> roles = roleService.selectRoleListByUserId(userId);
        List<RoleResource> roleResources = roleResourceService.selectAuthorizedResourceByRoleIdsWithSelect(roles, 2);
        List<UserResource> userResources = userResourceService.selectAuthorizedResourceByUidWithSelect(userId, 2);
        List<DataSet> dataVisualizations = selectDataSetByLoginId();

        Set<String> resourceIds = roleResources.stream()
                .map(RoleResource::getResourceId)
                .collect(Collectors.toSet());

        resourceIds.addAll(userResources.stream()
                .map(UserResource::getResourceId)
                .collect(Collectors.toSet()));

        resourceIds.addAll(dataVisualizations.stream()
                .map(item -> String.valueOf(item.getId()))
                .collect(Collectors.toSet()));

        if (resourceIds.isEmpty()) {
            return Collections.singletonList("0");
        }

        return new ArrayList<>(resourceIds);
    }

    @Override
    public List<String> selectAuthorizedResourceIdsWithManage() {
        int userId = StpUtil.isLogin() ? StpUtil.getLoginIdAsInt() : 1;
        List<Integer> roles = roleService.selectRoleListByUserId(userId);
        List<RoleResource> roleResources = roleResourceService.selectAuthorizedResourceByRoleIdsWithManage(roles, 2);
        List<UserResource> userResources = userResourceService.selectAuthorizedResourceByUidWithManage(userId, 2);
        List<DataSet> dataVisualizations = selectDataSetByLoginId();

        Set<String> resourceIds = roleResources.stream()
                .map(RoleResource::getResourceId)
                .collect(Collectors.toSet());

        resourceIds.addAll(userResources.stream()
                .map(UserResource::getResourceId)
                .collect(Collectors.toSet()));

        resourceIds.addAll(dataVisualizations.stream()
                .map(item -> String.valueOf(item.getId()))
                .collect(Collectors.toSet()));

        return new ArrayList<>(resourceIds);
    }
}
