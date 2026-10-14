package org.app.marketserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.app.marketserver.mapper")
public class MarketserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketserverApplication.class, args);
    }

}
