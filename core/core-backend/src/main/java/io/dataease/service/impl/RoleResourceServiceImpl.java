package io.dataease.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.dataease.data.model.RoleResource;
import io.dataease.mapper.RoleResourceMapper;
import io.dataease.service.RoleResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleResourceServiceImpl extends ServiceImpl<RoleResourceMapper, RoleResource> implements RoleResourceService {

    @Autowired
    private RoleResourceMapper roleResourceMapper;

    @Override
    public List<RoleResource> selectResourceByRoleId(Integer roleId) {
        QueryWrapper<RoleResource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId);
        return roleResourceMapper.selectList(queryWrapper);
    }

    @Override
    public List<RoleResource> selectAuthorizedResourceByRoleIds(List<Integer> roleIds) {
        QueryWrapper<RoleResource> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("role_id", roleIds);
        queryWrapper.eq("is_auth", 1);
        return roleResourceMapper.selectList(queryWrapper);
    }

    @Override
    public List<RoleResource> selectResourceByResourceId(String resourceId) {
        QueryWrapper<RoleResource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("resource_id", resourceId);
        return roleResourceMapper.selectList(queryWrapper);
    }
}
