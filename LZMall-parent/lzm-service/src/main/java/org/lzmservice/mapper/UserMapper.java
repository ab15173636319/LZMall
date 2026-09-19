package org.lzmservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.lzmservice.pojo.entity.User;

import java.util.Optional;

public interface UserMapper extends BaseMapper<User> {

    Optional<User> selectByUsername(String username);

}
