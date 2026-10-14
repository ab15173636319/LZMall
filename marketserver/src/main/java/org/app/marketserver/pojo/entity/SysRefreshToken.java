package org.app.marketserver.pojo.entity;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.app.marketserver.common.entity.BaseEntity;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_refresh_token")
public class SysRefreshToken extends BaseEntity {
    private Long user_id;
    private String token_hash;
    private String device_id;
    private int revoked;
    private DateTime expire_time;

}
