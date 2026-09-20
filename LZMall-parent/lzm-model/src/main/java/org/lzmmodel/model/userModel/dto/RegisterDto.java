package org.lzmmodel.model.userModel.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
public class RegisterDto {

    @NotNull(message = "用户名不能为空")
    @Size(min = 6, message = "用户名长度不能小于6位")
    private String username;

    @NotNull(message = "密码不能为空")
    @Size(min = 6, message = "密码长度不能小于6位")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "密码只能是数字和字母组合")
    private String password;

    @NotNull(message = "确认密码不能为空")
    private String validatePassword;

}
