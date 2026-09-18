package org.lzmstart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "org.lzmstart",
        "org.lzmweb",
        "org.lzmservice",
        "org.lzmsecurity",
        "org.lzmcommon"
})
public class LzmStartApplication {

    public static void main(String[] args) {
        SpringApplication.run(LzmStartApplication.class, args);
    }

}
