package org.app.marketserver.common.entity;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private DateTime createTime;
    private DateTime updateTime;
    private Integer deleteFlag;
}
