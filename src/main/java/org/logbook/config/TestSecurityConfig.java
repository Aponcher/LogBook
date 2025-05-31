package org.logbook.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Currently configured to NO SECURITY until we are areday - simply here to allow for testing and such while package is included
 */
@Slf4j
@TestConfiguration
@AllArgsConstructor
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain testNoOpFilter(HttpSecurity http) throws Exception {
        log.info("[AUDIT] Security is disabled for testing");
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz.anyRequest().permitAll());
        return http.build();
    }

//    @Bean
//    public UserDetailService userDetailService(UserRepository userRepository) {
//        return new UserDetailService(userRepository);
//    }
}
