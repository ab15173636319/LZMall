package org.app.marketserver.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleteFlag;
}
