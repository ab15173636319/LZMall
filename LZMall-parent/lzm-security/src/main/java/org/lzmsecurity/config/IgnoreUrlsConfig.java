package org.lzmsecurity.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ConfigurationProperties(prefix = "ignore-urls")
public class IgnoreUrlsConfig {

    private List<String> urls = new ArrayList<>();

}
