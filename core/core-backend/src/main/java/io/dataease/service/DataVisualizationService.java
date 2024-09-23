package io.dataease.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.dataease.data.model.DataVisualization;

import java.util.List;

public interface DataVisualizationService extends IService<DataVisualization> {

    List<DataVisualization> selectDataVisualizationByLoginId();
}
