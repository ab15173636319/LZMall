package org.app.marketserver.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.app.marketserver.common.entity.BaseEntity;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "sys_permission")
public class SysPermission extends BaseEntity {

    private String code;

    private String name;

    private String type;


}
