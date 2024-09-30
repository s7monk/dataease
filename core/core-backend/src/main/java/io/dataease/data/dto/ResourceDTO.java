package io.dataease.data.dto;

import io.dataease.data.vo.ResourceVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResourceDTO {

    private Integer userId;

    private Integer roleId;

    private String resourceId;

    private Integer isSelect;

    private Integer isManage;

    private Integer isShare;

    private Integer isExport;

    private Integer isAuth;

    private Integer resourceType;
}
