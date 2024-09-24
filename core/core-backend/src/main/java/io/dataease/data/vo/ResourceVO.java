package io.dataease.data.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResourceVO {

    private String resourceId;

    private String resourceName;

    private Integer leaf;

    private Integer isSelect;

    private Integer isManage;

    private Integer isShare;

    private Integer isExport;

    private Integer isAuth;

    private List<ResourceVO> children;
}
