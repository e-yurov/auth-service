package com.rc.mentorship.authservice.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "auth.keycloak")
@Component
@Getter
@Setter
@NoArgsConstructor
public class AuthKeycloakProperties {
    private String clientId;
    private String clientSecret;
    private String tokenUri;
}
