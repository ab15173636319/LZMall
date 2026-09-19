package org.lzmservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.lzmservice.pojo.entity.User;

import java.util.Optional;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    Optional<User> selectByUsername(String username);

}
