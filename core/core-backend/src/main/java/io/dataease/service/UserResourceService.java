package io.dataease.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.dataease.data.model.UserResource;

import java.util.List;

public interface UserResourceService extends IService<UserResource> {

    List<UserResource> selectResourceByUid(Integer userId, Integer resourceType);

    List<UserResource> selectAuthorizedResourceByUid(Integer userId, Integer resourceType);

    List<UserResource> selectAuthorizedResourceByUidWithSelect(Integer userId, Integer resourceType);

    List<UserResource> selectAuthorizedResourceByUidWithShare(Integer userId, Integer resourceType);

    List<UserResource> selectAuthorizedResourceByUidWithManage(Integer userId, Integer resourceType);

    List<UserResource> selectAuthorizedResourceByUidWithExport(Integer userId, Integer resourceType);

    List<UserResource> selectResourceByResourceId(String resourceId);

    void saveUserResource(List<UserResource> userResources);
}
