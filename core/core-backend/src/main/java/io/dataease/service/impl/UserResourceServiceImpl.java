package io.dataease.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.dataease.data.model.UserResource;
import io.dataease.mapper.UserResourceMapper;
import io.dataease.service.UserResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserResourceServiceImpl extends ServiceImpl<UserResourceMapper, UserResource> implements UserResourceService {

    @Autowired
    private UserResourceMapper userResourceMapper;

    @Override
    public List<UserResource> selectResourceByUid(Integer userId) {
        QueryWrapper<UserResource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return userResourceMapper.selectList(queryWrapper);
    }

    @Override
    public List<UserResource> selectAuthorizedResourceByUid(Integer userId) {
        QueryWrapper<UserResource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("is_auth", 1);
        return userResourceMapper.selectList(queryWrapper);
    }

    @Override
    public List<UserResource> selectResourceByResourceId(Long resourceId) {
        QueryWrapper<UserResource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("resource_id", resourceId);
        return userResourceMapper.selectList(queryWrapper);
    }
}
