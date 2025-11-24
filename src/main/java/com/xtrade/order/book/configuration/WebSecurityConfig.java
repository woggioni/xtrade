package com.xtrade.order.book.configuration;

import com.xtrade.order.book.OrderBookApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.stream.Stream;

@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityContextHolderStrategy securityContextHolderStrategy() {
        return SecurityContextHolder.getContextHolderStrategy();
    }
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        final var users = Stream.of(
            "user",
            "user2",
            "user3"
        ).map(name -> {
            return User.builder().passwordEncoder(passwordEncoder::encode)
                .username(name)
                .password("password")
                .roles("USER")
                .build();
        }).toArray(UserDetails[]::new);
        return new InMemoryUserDetailsManager(users);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserDetailsService userDetailsService) throws Exception {
        http
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers(
                    Stream.of(
                        "/login",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/h2-console/",
                        "/h2-console/**"
                    ).map(AntPathRequestMatcher::new)
                    .toArray(RequestMatcher[]::new)
                )
                .anonymous()
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
            )
            .userDetailsService(userDetailsService)
            .csrf(AbstractHttpConfigurer::disable)
            .headers(headers -> {
                headers.frameOptions(
                    fc -> fc.disable()
                );
            })
            .exceptionHandling(eh -> {
                Logger log = LoggerFactory.getLogger(OrderBookApplication.class);
                eh.accessDeniedHandler((request, response, ade) -> {
                    log.error(ade.getMessage(), ade);
                });
            });

        return http.build();
    }
}
