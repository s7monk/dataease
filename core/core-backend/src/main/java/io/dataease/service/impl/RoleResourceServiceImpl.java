package io.dataease.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.dataease.data.model.RoleResource;
import io.dataease.data.model.UserResource;
import io.dataease.mapper.RoleResourceMapper;
import io.dataease.service.RoleResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleResourceServiceImpl extends ServiceImpl<RoleResourceMapper, RoleResource> implements RoleResourceService {

    @Autowired
    private RoleResourceMapper roleResourceMapper;

    @Override
    public List<RoleResource> selectResourceByRoleId(Integer roleId, Integer resourceType) {
        QueryWrapper<RoleResource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId);
        queryWrapper.eq("resource_type", resourceType);
        return roleResourceMapper.selectList(queryWrapper);
    }

    @Override
    public List<RoleResource> selectAuthorizedResourceByRoleIds(List<Integer> roleIds, Integer resourceType) {
        QueryWrapper<RoleResource> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("role_id", roleIds);
        queryWrapper.eq("is_auth", 1);
        queryWrapper.eq("resource_type", resourceType);
        return roleResourceMapper.selectList(queryWrapper);
    }

    @Override
    public List<RoleResource> selectAuthorizedResourceByRoleIdsWithSelect(List<Integer> roleIds, Integer resourceType) {
        QueryWrapper<RoleResource> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("role_id", roleIds);
        queryWrapper.eq("is_select", 1);
        queryWrapper.eq("resource_type", resourceType);
        return roleResourceMapper.selectList(queryWrapper);
    }

    @Override
    public List<RoleResource> selectAuthorizedResourceByRoleIdsWithShare(List<Integer> roleIds, Integer resourceType) {
        QueryWrapper<RoleResource> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("role_id", roleIds);
        queryWrapper.eq("is_share", 1);
        queryWrapper.eq("resource_type", resourceType);
        return roleResourceMapper.selectList(queryWrapper);
    }

    @Override
    public List<RoleResource> selectAuthorizedResourceByRoleIdsWithManage(List<Integer> roleIds, Integer resourceType) {
        QueryWrapper<RoleResource> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("role_id", roleIds);
        queryWrapper.eq("is_manage", 1);
        queryWrapper.eq("resource_type", resourceType);
        return roleResourceMapper.selectList(queryWrapper);
    }

    @Override
    public List<RoleResource> selectAuthorizedResourceByRoleIdsWithExport(List<Integer> roleIds, Integer resourceType) {
        QueryWrapper<RoleResource> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("role_id", roleIds);
        queryWrapper.eq("is_export", 1);
        queryWrapper.eq("resource_type", resourceType);
        return roleResourceMapper.selectList(queryWrapper);
    }

    @Override
    public List<RoleResource> selectResourceByResourceId(String resourceId) {
        QueryWrapper<RoleResource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("resource_id", resourceId);
        return roleResourceMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public void saveRoleResource(List<RoleResource> roleResources) {
        for (RoleResource roleResource : roleResources) {
            QueryWrapper<RoleResource> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("role_id", roleResource.getRoleId())
                    .eq("resource_id", roleResource.getResourceId())
                    .eq("resource_type", roleResource.getResourceType());

            RoleResource existingResource = this.getOne(queryWrapper);

            if (existingResource != null) {
                roleResource.setId(existingResource.getId());
            }
        }
        this.saveOrUpdateBatch(roleResources);
    }
}
