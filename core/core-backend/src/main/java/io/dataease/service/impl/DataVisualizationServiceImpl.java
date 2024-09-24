package io.dataease.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.dataease.data.model.DataVisualization;
import io.dataease.data.model.RoleResource;
import io.dataease.data.model.UserResource;
import io.dataease.mapper.DataVisualizationMapper;
import io.dataease.service.DataVisualizationService;
import io.dataease.service.RoleResourceService;
import io.dataease.service.SysRoleService;
import io.dataease.service.UserResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
public class DataVisualizationServiceImpl extends ServiceImpl<DataVisualizationMapper, DataVisualization> implements DataVisualizationService {

    @Autowired
    private DataVisualizationMapper dataVisualizationMapper;

    @Autowired
    private UserResourceService userResourceService;

    @Autowired
    private RoleResourceService roleResourceService;

    @Autowired
    private SysRoleService roleService;


    @Override
    public List<DataVisualization> getDataVisualizations() {
        String userId = StpUtil.isLogin() ? StpUtil.getLoginIdAsString() : "1";
        if (Objects.equals(userId, "1")) {
            return this.list();
        } else {
            return selectDataVisualizationByIds();
        }
    }

    @Override
    public List<DataVisualization> selectDataVisualizationByIds() {
        QueryWrapper<DataVisualization> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", selectAuthorizedResourceIds());
        return dataVisualizationMapper.selectList(queryWrapper);
    }

    @Override
    public List<DataVisualization> selectDataVisualizationByLoginId() {
        String userId = StpUtil.isLogin() ? StpUtil.getLoginIdAsString() : "1";
        QueryWrapper<DataVisualization> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("create_by", userId);
        queryWrapper.eq("delete_flag", 0);
        queryWrapper.orderByDesc("create_time");
        return dataVisualizationMapper.selectList(queryWrapper);
    }

    @Override
    public List<String> selectAuthorizedResourceIds() {
        int userId = StpUtil.isLogin() ? StpUtil.getLoginIdAsInt() : 1;
        List<Integer> roles = roleService.selectRoleListByUserId(userId);
        List<RoleResource> roleResources = roleResourceService.selectAuthorizedResourceByRoleIds(roles);
        List<UserResource> userResources = userResourceService.selectAuthorizedResourceByUid(userId);
        List<DataVisualization> dataVisualizations = selectDataVisualizationByLoginId();

        Set<String> resourceIds = roleResources.stream()
                .map(RoleResource::getResourceId)
                .collect(Collectors.toSet());

        resourceIds.addAll(userResources.stream()
                .map(UserResource::getResourceId)
                .collect(Collectors.toSet()));

        resourceIds.addAll(dataVisualizations.stream()
                .map(DataVisualization::getId)
                .collect(Collectors.toSet()));

        return new ArrayList<>(resourceIds);
    }

}
