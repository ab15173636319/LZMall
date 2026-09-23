package org.lzmcommon.properties;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "redis.cache-key")
public class RedisProperties {
    private String saveUserInfo;
}
