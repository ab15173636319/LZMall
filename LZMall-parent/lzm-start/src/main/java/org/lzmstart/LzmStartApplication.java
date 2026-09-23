package org.lzmstart;

import org.lzmcommon.properties.RedisProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = {
        "org.lzmstart",
        "org.lzmweb",
        "org.lzmservice",
        "org.lzmsecurity",
        "org.lzmcommon"
})
@MapperScan("org.lzmservice.**.mapper")
@EnableConfigurationProperties(RedisProperties.class)
public class LzmStartApplication {

    public static void main(String[] args) {
        SpringApplication.run(LzmStartApplication.class, args);
    }

}
