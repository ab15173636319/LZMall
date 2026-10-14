package org.app.marketserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.app.marketserver.mapper.SysUserMapper;
import org.app.marketserver.pojo.entity.SysUser;
import org.app.marketserver.security.LoginUser;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginUserDetailsService implements UserDetailsService {

    private final SysUserMapper userMapper;

    @NullMarked
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new UsernameNotFoundException("用户状态异常");
        }

        List<String> roles = userMapper.selectRoleCodesByUser(user.getId());
        List<String> permissions = userMapper.selectPermissionCodesByUserId(user.getId());

        return new LoginUser(user.getId(), user.getUsername(), roles, permissions);
    }
}
