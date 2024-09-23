package io.dataease.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.dataease.data.model.DataVisualization;
import io.dataease.mapper.DataVisualizationMapper;
import io.dataease.service.DataVisualizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataVisualizationServiceImpl extends ServiceImpl<DataVisualizationMapper, DataVisualization> implements DataVisualizationService {

    @Autowired
    private DataVisualizationMapper dataVisualizationMapper;

    @Override
    public List<DataVisualization> selectDataVisualizationByLoginId() {
        String userId = StpUtil.isLogin() ? StpUtil.getLoginIdAsString() : "1";
        QueryWrapper<DataVisualization> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("create_by", userId);
        queryWrapper.eq("delete_flag", 0);
        queryWrapper.orderByDesc("create_time");
        return dataVisualizationMapper.selectList(queryWrapper);
    }
}
