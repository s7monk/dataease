package io.dataease.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.dataease.data.model.UserResource;
import io.dataease.mapper.UserResourceMapper;
import io.dataease.service.UserResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserResourceServiceImpl extends ServiceImpl<UserResourceMapper, UserResource> implements UserResourceService {

    @Autowired
    private UserResourceMapper userResourceMapper;

    @Override
    public List<UserResource> selectResourceByUid(Integer userId, Integer resourceType) {
        QueryWrapper<UserResource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("resource_type", resourceType);
        return userResourceMapper.selectList(queryWrapper);
    }

    @Override
    public List<UserResource> selectAuthorizedResourceByUid(Integer userId, Integer resourceType) {
        QueryWrapper<UserResource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("is_auth", 1);
        queryWrapper.eq("resource_type", resourceType);
        return userResourceMapper.selectList(queryWrapper);
    }

    @Override
    public List<UserResource> selectAuthorizedResourceByUidWithSelect(Integer userId, Integer resourceType) {
        QueryWrapper<UserResource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("is_select", 1);
        queryWrapper.eq("resource_type", resourceType);
        return userResourceMapper.selectList(queryWrapper);
    }

    @Override
    public List<UserResource> selectAuthorizedResourceByUidWithShare(Integer userId, Integer resourceType) {
        QueryWrapper<UserResource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("is_share", 1);
        queryWrapper.eq("resource_type", resourceType);
        return userResourceMapper.selectList(queryWrapper);
    }

    @Override
    public List<UserResource> selectAuthorizedResourceByUidWithManage(Integer userId, Integer resourceType) {
        QueryWrapper<UserResource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("is_manage", 1);
        queryWrapper.eq("resource_type", resourceType);
        return userResourceMapper.selectList(queryWrapper);
    }

    @Override
    public List<UserResource> selectAuthorizedResourceByUidWithExport(Integer userId, Integer resourceType) {
        QueryWrapper<UserResource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("is_export", 1);
        queryWrapper.eq("resource_type", resourceType);
        return userResourceMapper.selectList(queryWrapper);
    }

    @Override
    public List<UserResource> selectResourceByResourceId(String resourceId) {
        QueryWrapper<UserResource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("resource_id", resourceId);
        return userResourceMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public void saveUserResource(List<UserResource> userResources) {
        for (UserResource userResource : userResources) {
            QueryWrapper<UserResource> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userResource.getUserId())
                    .eq("resource_id", userResource.getResourceId())
                    .eq("resource_type", userResource.getResourceType());

            UserResource existingResource = this.getOne(queryWrapper);

            if (existingResource != null) {
                userResource.setId(existingResource.getId());
            }
        }
        this.saveOrUpdateBatch(userResources);
    }
}
