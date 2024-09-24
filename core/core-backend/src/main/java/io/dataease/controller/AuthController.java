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
    public List<ResourceVO> getDashboardsByUserId(String userId, String resourceName) {
        List<DataVisualization> dataVisualizations = dataVisualizationService.getDataVisualizations();
        List<DataVisualization> dashboards = dataVisualizations.stream().filter(item -> item.getType().equals("dashboard")).collect(Collectors.toList());
        if (StringUtils.isNotBlank(resourceName)) {
            dashboards = searchDashboardsByName(dataVisualizations, dashboards, resourceName);
          /*  dashboards = dashboards.stream().filter(item -> {
                if (item.getNodeType().equals("leaf")) {
                    return item.getName().contains(resourceName);
                } else return item.getNodeType().equals("folder") && item.getName().contains(resourceName);
            }).collect(Collectors.toList());

            if (!dashboards.isEmpty() && dashboards.get(0).getNodeType().equals("folder")) {
                DataVisualization folderNode = dashboards.get(0);
                dashboards.addAll(findChildNodes(dataVisualizations, folderNode.getId()));
            }*/
        }

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

    private List<DataVisualization> searchDashboardsByName(List<DataVisualization> dataVisualizations, List<DataVisualization> dashboards, String resourceName) {
        Set<String> addedIds = new HashSet<>();
        List<DataVisualization> result = new ArrayList<>();

        for (DataVisualization item : dashboards) {
            if (item.getName().equals(resourceName)) {
                result.add(item);
                addedIds.add(item.getId());

                if (item.getNodeType().equals("folder")) {
                    List<DataVisualization> childNodes = findChildNodes(dataVisualizations, item.getId());
                    for (DataVisualization child : childNodes) {
                        if (!addedIds.contains(child.getId())) {
                            result.add(child);
                            addedIds.add(child.getId());
                        }
                    }
                } else {
                    List<DataVisualization> parentNodes = findParentNodes(dataVisualizations, item.getPid());
                    for (DataVisualization parent : parentNodes) {
                        if (!addedIds.contains(parent.getId())) {
                            result.add(parent);
                            addedIds.add(parent.getId());
                        }
                    }
                }
            }
        }

        return result;
    }

    private List<DataVisualization> findParentNodes(List<DataVisualization> dataVisualizations, String childId) {
        List<DataVisualization> parentNodes = new ArrayList<>();
        for (DataVisualization dataVisualization : dataVisualizations) {
            if (dataVisualization.getId().equals(childId)) {
                parentNodes.add(dataVisualization);
                if (!Objects.equals(dataVisualization.getPid(), "0")) {
                    parentNodes.addAll(findParentNodes(dataVisualizations, dataVisualization.getPid()));
                }
            }
        }
        return parentNodes;
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

    private List<DataVisualization> findChildNodes(List<DataVisualization> dataVisualizations, String parentId) {
        List<DataVisualization> childNodes = new ArrayList<>();
        for (DataVisualization dataVisualization : dataVisualizations) {
            if (dataVisualization.getPid().equals(parentId)) {
                childNodes.add(dataVisualization);
                if (dataVisualization.getNodeType().equals("folder")) {
                    childNodes.addAll(findChildNodes(dataVisualizations, dataVisualization.getId()));
                }
            }
        }
        return childNodes;
    }
}
