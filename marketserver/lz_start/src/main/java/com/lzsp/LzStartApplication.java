package com.lzsp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动模块入口。
 *
 * <p>位于根包 {@code com.lzsp} 下，默认组件扫描可覆盖 lz_common、lz_config、lz_security、lz_user
 * 各模块的 {@code com.lzsp.*} 子包。</p>
 */
@SpringBootApplication
public class LzStartApplication {

	public static void main(String[] args) {
		SpringApplication.run(LzStartApplication.class, args);
	}

}
