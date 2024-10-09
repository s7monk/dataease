package io.dataease.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.dataease.data.model.DataVisualization;

import java.util.List;

public interface DataVisualizationService extends IService<DataVisualization> {

    List<DataVisualization> getDataVisualizations();

    List<DataVisualization> selectDataVisualizationByIds();

    List<DataVisualization> selectDataVisualizationByLoginId();

    List<String> selectAuthorizedResourceIds();

    List<String> selectAuthorizedResourceIdsWithSelect();

    List<String> selectAuthorizedResourceIdsWithShare();

    List<String> selectAuthorizedResourceIdsWithManage();

    List<String> selectAuthorizedResourceIdsWithExport();
}
