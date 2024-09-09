package io.dataease.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.dataease.data.model.Tenant;
import io.dataease.mapper.TenantMapper;
import io.dataease.service.TenantService;
import org.springframework.stereotype.Service;

@Service
public class TenantServiceImpl extends ServiceImpl<TenantMapper, Tenant> implements TenantService {}
