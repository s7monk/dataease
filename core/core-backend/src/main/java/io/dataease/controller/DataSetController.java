package io.dataease.controller;

import cn.dev33.satoken.stp.StpUtil;
import io.dataease.data.model.DataSet;
import io.dataease.service.DataSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dataSet")
public class DataSetController {

    @Autowired
    private DataSetService dataSetService;

    @GetMapping("/manage")
    public List<String> getAuthorizedResourceIdsWithManage() {
        String id = StpUtil.isLogin() ? StpUtil.getLoginIdAsString() : "1";
        if (Objects.equals(id, "1")) {
            return dataSetService.list().stream().map(item -> item.getId().toString()).collect(Collectors.toList());
        } else {
            return dataSetService.selectAuthorizedResourceIdsWithManage();
        }
    }
}
