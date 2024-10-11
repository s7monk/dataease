package io.dataease.controller;

import cn.dev33.satoken.stp.StpUtil;
import io.dataease.service.DataSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dataSource")
public class DataSourceController {

    @Autowired
    private DataSourceService dataSourceService;

    @GetMapping("/manage")
    public List<String> getAuthorizedResourceIdsWithManage() {
        String id = StpUtil.isLogin() ? StpUtil.getLoginIdAsString() : "1";
        if (id.equals("1")) {
            return dataSourceService.list().stream().map(item -> item.getId().toString()).collect(Collectors.toList());
        }
        return dataSourceService.selectAuthorizedResourceIdsWithManage();
    }
}
