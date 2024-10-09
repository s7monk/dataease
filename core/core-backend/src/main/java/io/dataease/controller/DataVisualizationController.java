package io.dataease.controller;

import io.dataease.service.DataVisualizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dataVisualization")
public class DataVisualizationController {

    @Autowired
    private DataVisualizationService dataVisualizationService;

    @GetMapping("/share")
    public List<String> getAuthorizedResourceIdsWithShare() {
        return dataVisualizationService.selectAuthorizedResourceIdsWithShare();
    }

    @GetMapping("/manage")
    public List<String> getAuthorizedResourceIdsWithManage() {
        return dataVisualizationService.selectAuthorizedResourceIdsWithManage();
    }

    @GetMapping("/export")
    public List<String> getAuthorizedResourceIdsWithExport() {
        return dataVisualizationService.selectAuthorizedResourceIdsWithExport();
    }
}
