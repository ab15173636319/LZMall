package org.lzmmodel.model.userModel.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.lzmcommon.entity.BaseEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserVo extends BaseEntity {
    private String username;

    private String nickname;
    private String email;
    private String phone;

    private String avatar;
}
