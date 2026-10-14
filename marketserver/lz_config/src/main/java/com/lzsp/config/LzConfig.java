package com.lzsp.config;

import org.springframework.context.annotation.Configuration;

/**
 * 公共配置模块的配置入口。
 *
 * <p>公共配置模块只作为依赖库被启动模块引入，因此这里使用普通的 {@link Configuration}，
 * 而不是 {@code @SpringBootApplication}。后续的 MyBatis-Plus、Redis、Jackson 等配置类统一放在本包下。</p>
 */
@Configuration
public class LzConfig {

}
