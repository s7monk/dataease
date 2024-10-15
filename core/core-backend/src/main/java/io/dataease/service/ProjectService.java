package io.dataease.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.dataease.data.model.Project;

import java.util.List;

public interface ProjectService extends IService<Project> {

    List<Project> getActiveProjects();
}
