package io.dataease.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.dataease.data.model.DataSource;

import java.util.List;

public interface DataSourceService extends IService<DataSource> {

    List<DataSource> getDataSources();

    List<DataSource> selectDataSourceByIds();

    List<DataSource> selectDataSourceByLoginId();

    List<String> selectAuthorizedResourceIds();
}
