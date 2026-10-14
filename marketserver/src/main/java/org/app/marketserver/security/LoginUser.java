package org.app.marketserver.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
public class LoginUser implements UserDetails {

    private final Long userId;

    private final String username;

    private final Collection<? extends GrantedAuthority> authorities;

    public LoginUser(
            Long userId,
            String username,
            List<String> roleCodes,
            List<String> permissionCodes
    ) {
        this.userId = userId;
        this.username = username;
        Set<String> authSet = new LinkedHashSet<>();
        for (String role : roleCodes) {
            authSet.add("ROLE_" + role.toUpperCase());
        }
        for (String per : permissionCodes) {
            if (StringUtils.hasText(per)) {
                authSet.add(per.toUpperCase());
            }
        }
        this.authorities = authSet.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @NullMarked
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return "";
    }

    @NullMarked
    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
