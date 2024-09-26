package io.dataease.controller;

import io.dataease.data.model.DataVisualization;
import io.dataease.data.model.UserResource;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
    public List<ResourceVO> getDashboardsByUserId(String userId) {
        List<DataVisualization> dataVisualizations = dataVisualizationService.getDataVisualizations();
        List<DataVisualization> dashboards = dataVisualizations.stream().filter(item -> item.getType().equals("dashboard")).collect(Collectors.toList());

        List<ResourceVO> resourceList = new ArrayList<>();
        Map<String, ResourceVO> resourceMap = new HashMap<>();
        if (Objects.equals(userId, "1")) {
            for (DataVisualization dashboard : dashboards) {
                ResourceVO resourceVO = buildResourceVO(dashboard, 1, 1, 1, 1, 1);
                resourceMap.put(dashboard.getId(), resourceVO);
            }
        } else {
            List<UserResource> userResources = userResourceService.selectResourceByUid(Integer.valueOf(userId));

            List<DataVisualization> userCreatedDashboards = dashboards.stream()
                    .filter(dashboard -> dashboard.getCreateBy().equals(userId))
                    .collect(Collectors.toList());

            Set<String> authorizedResourceIds = userResources.stream()
                    .map(UserResource::getResourceId)
                    .collect(Collectors.toSet());

            for (DataVisualization dashboard : dashboards) {
                if (userCreatedDashboards.contains(dashboard)) {
                    ResourceVO resourceVO = buildResourceVO(dashboard, 1, 1, 1, 1, 1);
                    resourceMap.put(dashboard.getId(), resourceVO);
                } else if (authorizedResourceIds.contains(dashboard.getId())) {
                    UserResource userResource = userResources.stream()
                            .filter(ur -> ur.getResourceId().equals(dashboard.getId()))
                            .findFirst()
                            .orElse(null);

                    if (userResource != null) {
                        ResourceVO resourceVO = buildResourceVO(dashboard, userResource.getIsSelect(),
                                userResource.getIsManage(), userResource.getIsShare(), userResource.getIsExport(), userResource.getIsAuth());
                        resourceMap.put(dashboard.getId(), resourceVO);
                    }
                }
            }

        }

        for (DataVisualization dashboard : dashboards) {
            ResourceVO resourceVO = resourceMap.get(dashboard.getId());
            if (resourceVO != null) {
                if (Objects.equals(dashboard.getPid(), "0")) {
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

    @GetMapping("/getDataViewByUserId")
    public List<ResourceVO> getDataViewByUserId(String userId) {
        List<DataVisualization> dataVisualizations = dataVisualizationService.getDataVisualizations();
        List<DataVisualization> dashboards = dataVisualizations.stream().filter(item -> item.getType().equals("dataV")).collect(Collectors.toList());

        List<ResourceVO> resourceList = new ArrayList<>();
        Map<String, ResourceVO> resourceMap = new HashMap<>();
        if (Objects.equals(userId, "1")) {
            for (DataVisualization dashboard : dashboards) {
                ResourceVO resourceVO = buildResourceVO(dashboard, 1, 1, 1, 1, 1);
                resourceMap.put(dashboard.getId(), resourceVO);
            }
        } else {
            List<UserResource> userResources = userResourceService.selectResourceByUid(Integer.valueOf(userId));

            List<DataVisualization> userCreatedDashboards = dashboards.stream()
                    .filter(dashboard -> dashboard.getCreateBy().equals(userId))
                    .collect(Collectors.toList());

            Set<String> authorizedResourceIds = userResources.stream()
                    .map(UserResource::getResourceId)
                    .collect(Collectors.toSet());

            for (DataVisualization dashboard : dashboards) {
                if (userCreatedDashboards.contains(dashboard)) {
                    ResourceVO resourceVO = buildResourceVO(dashboard, 1, 1, 1, 1, 1);
                    resourceMap.put(dashboard.getId(), resourceVO);
                } else if (authorizedResourceIds.contains(dashboard.getId())) {
                    UserResource userResource = userResources.stream()
                            .filter(ur -> ur.getResourceId().equals(dashboard.getId()))
                            .findFirst()
                            .orElse(null);

                    if (userResource != null) {
                        ResourceVO resourceVO = buildResourceVO(dashboard, userResource.getIsSelect(),
                                userResource.getIsManage(), userResource.getIsShare(), userResource.getIsExport(), userResource.getIsAuth());
                        resourceMap.put(dashboard.getId(), resourceVO);
                    }
                }
            }

        }

        for (DataVisualization dashboard : dashboards) {
            ResourceVO resourceVO = resourceMap.get(dashboard.getId());
            if (resourceVO != null) {
                if (Objects.equals(dashboard.getPid(), "0")) {
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

    private ResourceVO buildResourceVO(DataVisualization dashboard, Integer isSelect, Integer isManage, Integer isShare,
                                       Integer isExport, Integer isAuth) {
        return ResourceVO.builder()
                .resourceId(dashboard.getId())
                .resourceName(dashboard.getName())
                .isSelect(isSelect)
                .isManage(isManage)
                .isShare(isShare)
                .isExport(isExport)
                .isAuth(isAuth)
                .leaf("folder".equals(dashboard.getNodeType()) ? 0 : 1)
                .children(new ArrayList<>())
                .build();
    }
}
