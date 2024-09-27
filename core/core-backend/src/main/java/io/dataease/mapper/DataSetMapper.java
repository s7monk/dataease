package io.dataease.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.dataease.data.model.DataSet;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DataSetMapper extends BaseMapper<DataSet> {
}
