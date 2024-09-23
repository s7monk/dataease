package io.dataease.controller;

import io.dataease.data.model.DataVisualization;
import io.dataease.data.vo.ResourceVO;
import io.dataease.service.DataVisualizationService;
import io.dataease.service.RoleResourceService;
import io.dataease.service.UserResourceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserResourceService userResourceService;

    @Autowired
    private RoleResourceService roleResourceService;

    @Autowired
    private DataVisualizationService dataVisualizationService;

    @GetMapping("/getDashboardsByUserId")
    public List<ResourceVO> getDashboardsByUserId(String userId, String resourceName) {
        List<DataVisualization> dataVisualizations = dataVisualizationService.getDataVisualizations();
        List<DataVisualization> dashboards = new ArrayList<>();
        if (StringUtils.isNotBlank(resourceName)) {
            dashboards = dataVisualizations.stream().filter(item -> item.getType().equals("dashboard") && item.getName().equals(resourceName)).toList();
        } else {
            dashboards = dataVisualizations.stream().filter(item -> item.getType().equals("dashboard")).toList();
        }
        List<ResourceVO> resourceList = new ArrayList<>();
        if (Objects.equals(userId, "1")) {
            Map<Long, ResourceVO> resourceMap = new HashMap<>();
            for (DataVisualization dashboard : dashboards) {
                ResourceVO resourceVO = ResourceVO.builder()
                        .resourceId(dashboard.getId())
                        .resourceName(dashboard.getName())
                        .isSelect(1)
                        .isManage(1)
                        .isShare(1)
                        .isExport(1)
                        .isAuth(1)
                        .leaf("folder".equals(dashboard.getNodeType()) ? 0 : 1)
                        .children(new ArrayList<>())
                        .build();
                resourceMap.put(dashboard.getId(), resourceVO);
            }

            for (DataVisualization dashboard : dashboards) {
                ResourceVO resourceVO = resourceMap.get(dashboard.getId());
                if (dashboard.getPid() == 0) {
                    resourceList.add(resourceVO);
                } else {
                    ResourceVO parent = resourceMap.get(dashboard.getPid());
                    if (parent != null) {
                        parent.getChildren().add(resourceVO);
                    }
                }
            }
        }
        return resourceList;
    }
}
