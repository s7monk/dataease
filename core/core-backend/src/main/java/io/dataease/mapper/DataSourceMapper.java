package io.dataease.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.dataease.data.model.DataSource;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DataSourceMapper extends BaseMapper<DataSource> {
}
