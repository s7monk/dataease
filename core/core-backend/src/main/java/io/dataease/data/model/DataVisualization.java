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
@TableName("data_visualization_info")
public class DataVisualization implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 父id
     */
    private String pid;

    /**
     * 所属组织id
     */
    private Long orgId;

    /**
     * 层级
     */
    private Integer level;

    /**
     * 节点类型  folder or panel 目录或者文件夹
     */
    private String nodeType;

    /**
     * 类型
     */
    private String type;

    /**
     * 样式数据
     */
    private String canvasStyleData;

    /**
     * 组件数据
     */
    private String componentData;

    /**
     * 移动端布局
     */
    private Boolean mobileLayout;

    /**
     * 状态 0-未发布 1-已发布
     */
    private Integer status;

    /**
     * 是否单独打开水印 0-关闭 1-开启
     */
    private Integer selfWatermarkStatus;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 备注
     */
    private String remark;

    /**
     * 数据来源
     */
    private String source;

    /**
     * 删除标志
     */
    private Boolean deleteFlag;

    /**
     * 删除时间
     */
    private Long deleteTime;

    /**
     * 删除人
     */
    private String deleteBy;

    /**
     * 可视化资源版本
     */
    private Integer version;
}
