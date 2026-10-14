package org.app.marketserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.app.marketserver.pojo.entity.SysRefreshToken;

@Mapper
public interface SysRefreshMapper extends BaseMapper<SysRefreshToken> {
}
