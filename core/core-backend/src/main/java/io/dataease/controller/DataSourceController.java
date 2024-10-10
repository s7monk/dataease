package io.dataease.controller;

import io.dataease.service.DataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dataSource")
public class DataSourceController {

    @Autowired
    private DataSourceService dataSourceService;

    @GetMapping("/manage")
    public List<String> getAuthorizedResourceIdsWithManage() {
        return dataSourceService.selectAuthorizedResourceIdsWithManage();
    }
}
