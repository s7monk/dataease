package io.dataease.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleResource {

    private Integer RoleId;

    private Long resourceId;

    private Integer isSelect;

    private Integer isManage;

    private Integer isShare;

    private Integer isExport;

    private Integer isAuth;
}
