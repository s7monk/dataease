package io.dataease.data.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("core_datasource")
public class DataSource implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 类型
     */
    private String type;

    /**
     * 父级ID
     */
    private Long pid;

    /**
     * 更新方式：0：替换；1：追加
     */
    private String editType;

    /**
     * 详细信息
     */
    private String configuration;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 变更人
     */
    private Long updateBy;

    /**
     * 创建人ID
     */
    private String createBy;

    /**
     * 状态
     */
    private String status;

    /**
     * 状态
     */
    private String qrtzInstance;

    /**
     * 任务状态
     */
    private String taskStatus;
}
