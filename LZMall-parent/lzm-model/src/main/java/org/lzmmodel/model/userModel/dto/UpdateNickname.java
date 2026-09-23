package org.lzmmodel.model.userModel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateNickname {
    @NotBlank(message = "不能为空")
    @Size(min = 1, message = "昵称长度不能小于1位")
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$", message = "只能是数字、字母和汉字组合")
    private String nickname;
}
