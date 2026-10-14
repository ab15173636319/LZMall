package org.app.marketserver.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.app.marketserver.common.entity.BaseEntity;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "sys_user")
public class SysUser extends BaseEntity {

    private String username;

    private String password;

    private Integer status;

}
