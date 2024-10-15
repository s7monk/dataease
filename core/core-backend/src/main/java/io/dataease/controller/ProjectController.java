package io.dataease.controller;

import io.dataease.data.model.Project;
import io.dataease.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping("/activeProjects")
    public List<Project> getActiveProjects() {
        return projectService.getActiveProjects();
    }
}
