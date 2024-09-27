package io.dataease.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.dataease.data.model.DataSet;
import io.dataease.data.model.DataSource;

import java.util.List;

public interface DataSetService extends IService<DataSet> {

    List<DataSet> getDataSets();

    List<DataSet> selectDataSetByIds();

    List<DataSet> selectDataSetByLoginId();

    List<String> selectAuthorizedResourceIds();
}
