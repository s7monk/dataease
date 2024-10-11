package io.dataease.controller;

import cn.dev33.satoken.stp.StpUtil;
import io.dataease.data.model.DataVisualization;
import io.dataease.service.DataVisualizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dataVisualization")
public class DataVisualizationController {

    @Autowired
    private DataVisualizationService dataVisualizationService;

    @GetMapping("/share")
    public List<String> getAuthorizedResourceIdsWithShare() {
        String id = StpUtil.isLogin() ? StpUtil.getLoginIdAsString() : "1";
        if (id.equals("1")) {
            return dataVisualizationService.list().stream().map(DataVisualization::getId).collect(Collectors.toList());
        }
        return dataVisualizationService.selectAuthorizedResourceIdsWithShare();
    }

    @GetMapping("/manage")
    public List<String> getAuthorizedResourceIdsWithManage() {
        String id = StpUtil.isLogin() ? StpUtil.getLoginIdAsString() : "1";
        if (id.equals("1")) {
            return dataVisualizationService.list().stream().map(DataVisualization::getId).collect(Collectors.toList());
        }
        return dataVisualizationService.selectAuthorizedResourceIdsWithManage();
    }

    @GetMapping("/export")
    public List<String> getAuthorizedResourceIdsWithExport() {
        String id = StpUtil.isLogin() ? StpUtil.getLoginIdAsString() : "1";
        if (id.equals("1")) {
            return dataVisualizationService.list().stream().map(DataVisualization::getId).collect(Collectors.toList());
        }
        return dataVisualizationService.selectAuthorizedResourceIdsWithExport();
    }
}
