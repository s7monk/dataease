package io.dataease.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.dataease.data.model.RoleResource;

import java.util.List;

public interface RoleResourceService extends IService<RoleResource> {

    List<RoleResource> selectResourceByRoleId(Integer roleId, Integer resourceType);

    List<RoleResource> selectAuthorizedResourceByRoleIds(List<Integer> roleIds, Integer resourceType);

    List<RoleResource> selectResourceByResourceId(String resourceId);

    void saveRoleResource(List<RoleResource> roleResources);
}
