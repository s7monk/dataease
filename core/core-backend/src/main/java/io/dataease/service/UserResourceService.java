package io.dataease.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.dataease.data.model.UserResource;

import java.util.List;

public interface UserResourceService extends IService<UserResource> {

    List<UserResource> selectResourceByUid(Integer userId, Integer resourceType);

    List<UserResource> selectAuthorizedResourceByUid(Integer userId, Integer resourceType);

    List<UserResource> selectResourceByResourceId(String resourceId);
}
