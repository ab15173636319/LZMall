package org.lzmcommon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class BaseEntity {
    @TableId(type = IdType.AUTO)
    private long id;
    @TableField(fill = FieldFill.INSERT)
    private Timestamp createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Timestamp updateTime;
    @TableLogic
    private boolean isDelete;
}
