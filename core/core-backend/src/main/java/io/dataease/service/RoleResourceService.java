package io.dataease.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.dataease.data.model.RoleResource;

import java.util.List;

public interface RoleResourceService extends IService<RoleResource> {

    List<RoleResource> selectResourceByRoleId(Integer roleId);

    List<RoleResource> selectAuthorizedResourceByRoleIds(List<Integer> roleIds);

    List<RoleResource> selectResourceByResourceId(Long resourceId);
}
