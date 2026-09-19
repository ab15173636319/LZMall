package org.lzmstart;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "org.lzmstart",
        "org.lzmweb",
        "org.lzmservice",
        "org.lzmsecurity",
        "org.lzmcommon"
})
@MapperScan("org.lzmservice.**.mapper")
public class LzmStartApplication {

    public static void main(String[] args) {
        SpringApplication.run(LzmStartApplication.class, args);
    }

}
