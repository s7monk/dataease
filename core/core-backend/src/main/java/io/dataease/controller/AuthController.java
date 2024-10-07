package io.dataease.controller;

import io.dataease.data.dto.ResourceDTO;
import io.dataease.data.model.DataSet;
import io.dataease.data.model.DataSource;
import io.dataease.data.model.DataVisualization;
import io.dataease.data.model.RoleResource;
import io.dataease.data.model.UserResource;
import io.dataease.data.vo.ResourceVO;
import io.dataease.service.DataSetService;
import io.dataease.service.DataSourceService;
import io.dataease.service.DataVisualizationService;
import io.dataease.service.RoleResourceService;
import io.dataease.service.UserResourceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private DataSetService dataSetService;

    @PostMapping("/saveResourceWithUserId")
    public void saveResourceWithUserId(@RequestBody List<ResourceDTO> resourceDTOS) {
        List<UserResource> userResourceList = toUserResourceList(resourceDTOS);
        userResourceService.saveUserResource(userResourceList);
    }

    @PostMapping("/saveResourceWithRoleId")
    public void saveResourceWithRoleId(@RequestBody List<ResourceDTO> resourceDTOS) {
        List<RoleResource> roleResourceList = toRoleResourceList(resourceDTOS);
        roleResourceService.saveRoleResource(roleResourceList);
    }

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
            List<UserResource> userResources = userResourceService.selectResourceByUid(Integer.valueOf(userId), 1);

            List<DataVisualization> userCreatedDashboards = dashboards.stream()
                    .filter(dashboard -> dashboard.getCreateBy().equals(userId))
                    .collect(Collectors.toList());

            Set<String> authorizedResourceIds = userResources.stream()
                    .map(UserResource::getResourceId)
                    .collect(Collectors.toSet());

            for (DataVisualization dashboard : dashboards) {
                ResourceVO resourceVO;
                if (userCreatedDashboards.contains(dashboard)) {
                    resourceVO = buildResourceVO(dashboard, 1, 1, 1, 1, 1);
                } else if (authorizedResourceIds.contains(dashboard.getId())) {
                    UserResource userResource = userResources.stream()
                            .filter(ur -> ur.getResourceId().equals(dashboard.getId()))
                            .findFirst()
                            .orElse(null);

                    if (userResource != null) {
                        resourceVO = buildResourceVO(dashboard, userResource.getIsSelect(),
                                userResource.getIsManage(), userResource.getIsShare(), userResource.getIsExport(), userResource.getIsAuth());
                    } else {
                        resourceVO = buildResourceVO(dashboard, 0, 0, 0, 0, 0);
                    }
                } else {
                    resourceVO = buildResourceVO(dashboard, 0, 0, 0, 0, 0);
                }
                resourceMap.put(dashboard.getId(), resourceVO);
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

    @GetMapping("/getDashboardsByRoleId")
    public List<ResourceVO> getDashboardsByRoleId(String roleId) {
        List<DataVisualization> dataVisualizations = dataVisualizationService.getDataVisualizations();
        List<DataVisualization> dashboards = dataVisualizations.stream().filter(item -> item.getType().equals("dashboard")).collect(Collectors.toList());

        List<ResourceVO> resourceList = new ArrayList<>();
        Map<String, ResourceVO> resourceMap = new HashMap<>();
        if (Objects.equals(roleId, "1")) {
            for (DataVisualization dashboard : dashboards) {
                ResourceVO resourceVO = buildResourceVO(dashboard, 1, 1, 1, 1, 1);
                resourceMap.put(dashboard.getId(), resourceVO);
            }
        } else {
            List<RoleResource> roleResources = roleResourceService.selectResourceByRoleId(Integer.valueOf(roleId), 1);

            Set<String> authorizedResourceIds = roleResources.stream()
                    .map(RoleResource::getResourceId)
                    .collect(Collectors.toSet());

            for (DataVisualization dashboard : dashboards) {
                ResourceVO resourceVO;
                if (authorizedResourceIds.contains(dashboard.getId())) {
                    RoleResource roleResource = roleResources.stream()
                            .filter(ur -> ur.getResourceId().equals(dashboard.getId()))
                            .findFirst()
                            .orElse(null);

                    if (roleResource != null) {
                        resourceVO = buildResourceVO(dashboard, roleResource.getIsSelect(),
                                roleResource.getIsManage(), roleResource.getIsShare(), roleResource.getIsExport(), roleResource.getIsAuth());
                    } else {
                        resourceVO = buildResourceVO(dashboard, 0, 0, 0, 0, 0);
                    }
                } else {
                    resourceVO = buildResourceVO(dashboard, 0, 0, 0, 0, 0);
                }
                resourceMap.put(dashboard.getId(), resourceVO);
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
            List<UserResource> userResources = userResourceService.selectResourceByUid(Integer.valueOf(userId), 1);

            List<DataVisualization> userCreatedDashboards = dashboards.stream()
                    .filter(dashboard -> dashboard.getCreateBy().equals(userId))
                    .collect(Collectors.toList());

            Set<String> authorizedResourceIds = userResources.stream()
                    .map(UserResource::getResourceId)
                    .collect(Collectors.toSet());

            for (DataVisualization dashboard : dashboards) {
                ResourceVO resourceVO;
                if (userCreatedDashboards.contains(dashboard)) {
                    resourceVO = buildResourceVO(dashboard, 1, 1, 1, 1, 1);
                } else if (authorizedResourceIds.contains(dashboard.getId())) {
                    UserResource userResource = userResources.stream()
                            .filter(ur -> ur.getResourceId().equals(dashboard.getId()))
                            .findFirst()
                            .orElse(null);

                    if (userResource != null) {
                        resourceVO = buildResourceVO(dashboard, userResource.getIsSelect(),
                                userResource.getIsManage(), userResource.getIsShare(), userResource.getIsExport(), userResource.getIsAuth());
                    } else {
                        resourceVO = buildResourceVO(dashboard, 0, 0, 0, 0, 0);
                    }
                } else {
                    resourceVO = buildResourceVO(dashboard, 0, 0, 0, 0, 0);
                }
                resourceMap.put(dashboard.getId(), resourceVO);
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

    @GetMapping("/getDataViewByRoleId")
    public List<ResourceVO> getDataViewByRoleId(String roleId) {
        List<DataVisualization> dataVisualizations = dataVisualizationService.getDataVisualizations();
        List<DataVisualization> dashboards = dataVisualizations.stream().filter(item -> item.getType().equals("dataV")).collect(Collectors.toList());

        List<ResourceVO> resourceList = new ArrayList<>();
        Map<String, ResourceVO> resourceMap = new HashMap<>();
        if (Objects.equals(roleId, "1")) {
            for (DataVisualization dashboard : dashboards) {
                ResourceVO resourceVO = buildResourceVO(dashboard, 1, 1, 1, 1, 1);
                resourceMap.put(dashboard.getId(), resourceVO);
            }
        } else {
            List<RoleResource> roleResources = roleResourceService.selectResourceByRoleId(Integer.valueOf(roleId), 1);

            Set<String> authorizedResourceIds = roleResources.stream()
                    .map(RoleResource::getResourceId)
                    .collect(Collectors.toSet());

            for (DataVisualization dashboard : dashboards) {
                ResourceVO resourceVO;
                if (authorizedResourceIds.contains(dashboard.getId())) {
                    RoleResource roleResource = roleResources.stream()
                            .filter(ur -> ur.getResourceId().equals(dashboard.getId()))
                            .findFirst()
                            .orElse(null);

                    if (roleResource != null) {
                        resourceVO = buildResourceVO(dashboard, roleResource.getIsSelect(),
                                roleResource.getIsManage(), roleResource.getIsShare(), roleResource.getIsExport(), roleResource.getIsAuth());
                    } else {
                        resourceVO = buildResourceVO(dashboard, 0, 0, 0, 0, 0);
                    }
                } else {
                    resourceVO = buildResourceVO(dashboard, 0, 0, 0, 0, 0);
                }
                resourceMap.put(dashboard.getId(), resourceVO);
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

    @GetMapping("/getDataSourceByUserId")
    public List<ResourceVO> getDataSourceByUserId(String userId) {
        List<DataSource> dataSources = dataSourceService.getDataSources();

        List<ResourceVO> resourceList = new ArrayList<>();
        Map<String, ResourceVO> resourceMap = new HashMap<>();
        if (Objects.equals(userId, "1")) {
            for (DataSource dataSource : dataSources) {
                ResourceVO resourceVO = buildResourceVO(dataSource, 1, 1, 1, 1, 1);
                resourceMap.put(String.valueOf(dataSource.getId()), resourceVO);
            }
        } else {
            List<UserResource> userResources = userResourceService.selectResourceByUid(Integer.valueOf(userId), 2);

            List<DataSource> userCreatedDashboards = dataSources.stream()
                    .filter(dashboard -> dashboard.getCreateBy().equals(userId))
                    .collect(Collectors.toList());

            Set<String> authorizedResourceIds = userResources.stream()
                    .map(UserResource::getResourceId)
                    .collect(Collectors.toSet());

            for (DataSource datasource : dataSources) {
                ResourceVO resourceVO;
                if (userCreatedDashboards.contains(datasource)) {
                    resourceVO = buildResourceVO(datasource, 1, 1, 1, 1, 1);
                } else if (authorizedResourceIds.contains(String.valueOf(datasource.getId()))) {
                    UserResource userResource = userResources.stream()
                            .filter(ur -> ur.getResourceId().equals(String.valueOf(datasource.getId())))
                            .findFirst()
                            .orElse(null);

                    if (userResource != null) {
                        resourceVO = buildResourceVO(datasource, userResource.getIsSelect(),
                                userResource.getIsManage(), userResource.getIsShare(), userResource.getIsExport(), userResource.getIsAuth());
                    } else {
                        resourceVO = buildResourceVO(datasource, 0, 0, 0, 0, 0);
                    }
                } else {
                    resourceVO = buildResourceVO(datasource, 0, 0, 0, 0, 0);
                }
                resourceMap.put(String.valueOf(datasource.getId()), resourceVO);
            }

        }

        for (DataSource datasource : dataSources) {
            ResourceVO resourceVO = resourceMap.get(String.valueOf(datasource.getId()));
            if (resourceVO != null) {
                if (Objects.equals(String.valueOf(datasource.getPid()), "0")) {
                    resourceList.add(resourceVO);
                } else {
                    ResourceVO parent = resourceMap.get(String.valueOf(datasource.getPid()));
                    if (parent != null) {
                        parent.getChildren().add(resourceVO);
                    }
                }
            }
        }
        return resourceList;
    }

    @GetMapping("/getDataSourceByRoleId")
    public List<ResourceVO> getDataSourceByRoleId(String roleId) {
        List<DataSource> dataSources = dataSourceService.getDataSources();

        List<ResourceVO> resourceList = new ArrayList<>();
        Map<String, ResourceVO> resourceMap = new HashMap<>();
        if (Objects.equals(roleId, "1")) {
            for (DataSource dataSource : dataSources) {
                ResourceVO resourceVO = buildResourceVO(dataSource, 1, 1, 1, 1, 1);
                resourceMap.put(String.valueOf(dataSource.getId()), resourceVO);
            }
        } else {
            List<RoleResource> roleResources = roleResourceService.selectResourceByRoleId(Integer.valueOf(roleId), 2);

            Set<String> authorizedResourceIds = roleResources.stream()
                    .map(RoleResource::getResourceId)
                    .collect(Collectors.toSet());

            for (DataSource datasource : dataSources) {
                ResourceVO resourceVO;
                if (authorizedResourceIds.contains(String.valueOf(datasource.getId()))) {
                    RoleResource roleResource = roleResources.stream()
                            .filter(ur -> ur.getResourceId().equals(String.valueOf(datasource.getId())))
                            .findFirst()
                            .orElse(null);

                    if (roleResource != null) {
                        resourceVO = buildResourceVO(datasource, roleResource.getIsSelect(),
                                roleResource.getIsManage(), roleResource.getIsShare(), roleResource.getIsExport(), roleResource.getIsAuth());
                    } else {
                        resourceVO = buildResourceVO(datasource, 0, 0, 0, 0, 0);
                    }
                } else {
                    resourceVO = buildResourceVO(datasource, 0, 0, 0, 0, 0);
                }
                resourceMap.put(String.valueOf(datasource.getId()), resourceVO);
            }

        }

        for (DataSource datasource : dataSources) {
            ResourceVO resourceVO = resourceMap.get(String.valueOf(datasource.getId()));
            if (resourceVO != null) {
                if (Objects.equals(String.valueOf(datasource.getPid()), "0")) {
                    resourceList.add(resourceVO);
                } else {
                    ResourceVO parent = resourceMap.get(String.valueOf(datasource.getPid()));
                    if (parent != null) {
                        parent.getChildren().add(resourceVO);
                    }
                }
            }
        }
        return resourceList;
    }

    @GetMapping("/getDataSetByUserId")
    public List<ResourceVO> getDataSetByUserId(String userId) {
        List<DataSet> dataSets = dataSetService.getDataSets();

        List<ResourceVO> resourceList = new ArrayList<>();
        Map<String, ResourceVO> resourceMap = new HashMap<>();
        if (Objects.equals(userId, "1")) {
            for (DataSet dataset : dataSets) {
                ResourceVO resourceVO = buildResourceVO(dataset, 1, 1, 1, 1, 1);
                resourceMap.put(String.valueOf(dataset.getId()), resourceVO);
            }
        } else {
            List<UserResource> userResources = userResourceService.selectResourceByUid(Integer.valueOf(userId), 3);

            List<DataSet> userCreatedDashboards = dataSets.stream()
                    .filter(dashboard -> dashboard.getCreateBy().equals(userId))
                    .collect(Collectors.toList());

            Set<String> authorizedResourceIds = userResources.stream()
                    .map(UserResource::getResourceId)
                    .collect(Collectors.toSet());

            for (DataSet dataset : dataSets) {
                ResourceVO resourceVO;
                if (userCreatedDashboards.contains(dataset)) {
                    resourceVO = buildResourceVO(dataset, 1, 1, 1, 1, 1);
                } else if (authorizedResourceIds.contains(String.valueOf(dataset.getId()))) {
                    UserResource userResource = userResources.stream()
                            .filter(ur -> ur.getResourceId().equals(String.valueOf(dataset.getId())))
                            .findFirst()
                            .orElse(null);

                    if (userResource != null) {
                        resourceVO = buildResourceVO(dataset, userResource.getIsSelect(),
                                userResource.getIsManage(), userResource.getIsShare(), userResource.getIsExport(), userResource.getIsAuth());
                    } else {
                        resourceVO = buildResourceVO(dataset, 0, 0, 0, 0, 0);
                    }
                } else {
                   resourceVO = buildResourceVO(dataset, 0, 0, 0, 0, 0);
                }
                resourceMap.put(String.valueOf(dataset.getId()), resourceVO);
            }

        }

        for (DataSet dataset : dataSets) {
            ResourceVO resourceVO = resourceMap.get(String.valueOf(dataset.getId()));
            if (resourceVO != null) {
                if (Objects.equals(String.valueOf(dataset.getPid()), "0")) {
                    resourceList.add(resourceVO);
                } else {
                    ResourceVO parent = resourceMap.get(String.valueOf(dataset.getPid()));
                    if (parent != null) {
                        parent.getChildren().add(resourceVO);
                    }
                }
            }
        }
        return resourceList;
    }

    @GetMapping("/getDataSetByRoleId")
    public List<ResourceVO> getDataSetByRoleId(String roleId) {
        List<DataSet> dataSets = dataSetService.getDataSets();

        List<ResourceVO> resourceList = new ArrayList<>();
        Map<String, ResourceVO> resourceMap = new HashMap<>();
        if (Objects.equals(roleId, "1")) {
            for (DataSet dataset : dataSets) {
                ResourceVO resourceVO = buildResourceVO(dataset, 1, 1, 1, 1, 1);
                resourceMap.put(String.valueOf(dataset.getId()), resourceVO);
            }
        } else {
            List<RoleResource> roleResources = roleResourceService.selectResourceByRoleId(Integer.valueOf(roleId), 3);

            Set<String> authorizedResourceIds = roleResources.stream()
                    .map(RoleResource::getResourceId)
                    .collect(Collectors.toSet());

            for (DataSet dataset : dataSets) {
                ResourceVO resourceVO;
                if (authorizedResourceIds.contains(String.valueOf(dataset.getId()))) {
                    RoleResource roleResource = roleResources.stream()
                            .filter(ur -> ur.getResourceId().equals(String.valueOf(dataset.getId())))
                            .findFirst()
                            .orElse(null);

                    if (roleResource != null) {
                       resourceVO = buildResourceVO(dataset, roleResource.getIsSelect(),
                                roleResource.getIsManage(), roleResource.getIsShare(), roleResource.getIsExport(), roleResource.getIsAuth());
                    } else {
                        resourceVO = buildResourceVO(dataset, 0, 0, 0, 0, 0);
                    }
                } else {
                    resourceVO = buildResourceVO(dataset, 0, 0, 0, 0, 0);
                }
                resourceMap.put(String.valueOf(dataset.getId()), resourceVO);
            }

        }

        for (DataSet dataset : dataSets) {
            ResourceVO resourceVO = resourceMap.get(String.valueOf(dataset.getId()));
            if (resourceVO != null) {
                if (Objects.equals(String.valueOf(dataset.getPid()), "0")) {
                    resourceList.add(resourceVO);
                } else {
                    ResourceVO parent = resourceMap.get(String.valueOf(dataset.getPid()));
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

    private ResourceVO buildResourceVO(DataSource datasource, Integer isSelect, Integer isManage, Integer isShare,
                                       Integer isExport, Integer isAuth) {
        return ResourceVO.builder()
                .resourceId(String.valueOf(datasource.getId()))
                .resourceName(datasource.getName())
                .isSelect(isSelect)
                .isManage(isManage)
                .isShare(isShare)
                .isExport(isExport)
                .isAuth(isAuth)
                .leaf("folder".equals(datasource.getType()) ? 0 : 1)
                .children(new ArrayList<>())
                .build();
    }

    private ResourceVO buildResourceVO(DataSet dataSet, Integer isSelect, Integer isManage, Integer isShare,
                                       Integer isExport, Integer isAuth) {
        return ResourceVO.builder()
                .resourceId(String.valueOf(dataSet.getId()))
                .resourceName(dataSet.getName())
                .isSelect(isSelect)
                .isManage(isManage)
                .isShare(isShare)
                .isExport(isExport)
                .isAuth(isAuth)
                .leaf("folder".equals(dataSet.getNodeType()) ? 0 : 1)
                .children(new ArrayList<>())
                .build();
    }

    private List<UserResource> toUserResourceList(List<ResourceDTO> resourceDTOS) {
        List<UserResource> userResourceList = new ArrayList<>();
        for (ResourceDTO resourceDTO : resourceDTOS) {
            UserResource userResource = new UserResource();
            userResource.setUserId(resourceDTO.getUserId());
            userResource.setResourceId(resourceDTO.getResourceId());
            userResource.setIsSelect(resourceDTO.getIsSelect());
            userResource.setIsManage(resourceDTO.getIsManage());
            userResource.setIsExport(resourceDTO.getIsExport());
            userResource.setIsShare(resourceDTO.getIsShare());
            userResource.setIsAuth(resourceDTO.getIsAuth());
            userResource.setResourceType(resourceDTO.getResourceType());
            userResourceList.add(userResource);
        }
        return userResourceList;
    }

    private List<RoleResource> toRoleResourceList(List<ResourceDTO> resourceDTOS) {
        List<RoleResource> roleResourceList = new ArrayList<>();
        for (ResourceDTO resourceDTO : resourceDTOS) {
            RoleResource roleResource = new RoleResource();
            roleResource.setRoleId(resourceDTO.getRoleId());
            roleResource.setResourceId(resourceDTO.getResourceId());
            roleResource.setIsSelect(resourceDTO.getIsSelect());
            roleResource.setIsManage(resourceDTO.getIsManage());
            roleResource.setIsExport(resourceDTO.getIsExport());
            roleResource.setIsShare(resourceDTO.getIsShare());
            roleResource.setIsAuth(resourceDTO.getIsAuth());
            roleResource.setResourceType(resourceDTO.getResourceType());
            roleResourceList.add(roleResource);
        }
        return roleResourceList;
    }
}
