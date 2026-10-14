package org.app.marketserver.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "app.security")
public class AuthorizeIgnoreUrl {
    private List<String> ignores;
}
