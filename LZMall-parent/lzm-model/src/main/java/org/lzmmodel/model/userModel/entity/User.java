package org.lzmmodel.model.userModel.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.lzmcommon.entity.BaseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
@AllArgsConstructor
@NoArgsConstructor
// 这些是 UserDetails 接口的派生只读属性：序列化时无需写出，反序列化时也无对应 setter。
// 若不忽略，getAuthorities() 返回的 List.of() 会被 JSON 序列化器当成不可变集合类型写入，
// 反序列化时无法还原（ImmutableCollections 无法构造）。
@JsonIgnoreProperties({"authorities", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "enabled"})
public class User extends BaseEntity implements UserDetails {

    private String username;
    private String password;

    private String nickname;
    private String email;
    private String phone;

    private String avatar;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    // 账号是否过期
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    // 账号是否被锁定
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    // 密码是否过期
    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    // 账号是否启用
    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
