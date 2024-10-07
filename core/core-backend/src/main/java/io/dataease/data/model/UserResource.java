package io.dataease.data.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResource {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private String resourceId;

    private Integer isSelect;

    private Integer isManage;

    private Integer isShare;

    private Integer isExport;

    private Integer isAuth;

    private Integer resourceType;
}
