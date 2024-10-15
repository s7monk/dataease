package io.dataease.data.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("projects")
public class Project {

    private Integer id;

    private String gn;

    private String name;

    private Integer enabled;

    private Integer default_selected;
}
