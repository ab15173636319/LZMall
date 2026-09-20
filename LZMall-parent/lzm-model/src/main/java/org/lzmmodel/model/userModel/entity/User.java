package org.lzmmodel.model.userModel.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.lzmcommon.entity.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
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
