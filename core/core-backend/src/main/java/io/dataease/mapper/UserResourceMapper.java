package io.dataease.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.dataease.data.model.UserResource;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserResourceMapper extends BaseMapper<UserResource> {
}
