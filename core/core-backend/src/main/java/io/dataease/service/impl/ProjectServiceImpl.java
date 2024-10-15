package io.dataease.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.dataease.data.model.Project;
import io.dataease.mapper.ProjectMapper;
import io.dataease.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {

    @Autowired
    private ProjectMapper projectMapper;

    @Override
    public List<Project> getActiveProjects() {
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("enabled", 1);
        return projectMapper.selectList(queryWrapper);
    }
}
