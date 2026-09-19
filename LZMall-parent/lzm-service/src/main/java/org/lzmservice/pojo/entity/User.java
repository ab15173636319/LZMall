package org.lzmservice.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lzmcommon.entity.BaseEntity;

@Data
@TableName("user")
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {

    private  String username;
    private  String password;

    private String nickname;
    private String email;
    private String phone;

    private String avatar;

}
