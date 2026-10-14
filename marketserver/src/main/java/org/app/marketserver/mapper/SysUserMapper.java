package org.app.marketserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.app.marketserver.pojo.entity.SysUser;

import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    @Select("""
                    SELECT DISTINCT p.code
                    FROM sys_user u
                    JOIN sys_user_role ur ON ur.user_id = u.id
                    JOIN sys_role_permission rp ON rp.role_id = ur.role_id
                    JOIN sys_permission p ON p.id = rp.permission_id
                    WHERE u.id = #{userId} AND u.status = 1
            """)
    List<String> selectPermissionCodesByUserId(Long userId);

    @Select("""
            select r.code
            from sys_user u
            join sys_user_role ur on ur.user_id=u.id
            join sys_role r on r.id=ur.role_id
            where u.id=#{userId} and u.status=1
            """)
    List<String> selectRoleCodesByUser(Long userId);

}
