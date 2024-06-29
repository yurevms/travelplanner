package ru.travelplanner.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.travelplanner.model.UserAuthority;

@Slf4j
@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(expressionInterceptUrlRegistry ->
                        expressionInterceptUrlRegistry
                                .requestMatchers("/registration", "/login", "/actuator/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/tour/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/tour/booking").permitAll()
                                .requestMatchers(HttpMethod.POST, "/tour").hasAnyAuthority(UserAuthority.MODERATOR.getAuthority(), UserAuthority.ADMIN.getAuthority())
                                .requestMatchers(HttpMethod.DELETE, "/tour/**").hasAnyAuthority(UserAuthority.MODERATOR.getAuthority(), UserAuthority.ADMIN.getAuthority())
                                .requestMatchers(HttpMethod.POST, "/registration/**").hasAuthority(UserAuthority.ADMIN.getAuthority())
                                .requestMatchers(HttpMethod.GET, "/registration/**").hasAuthority(UserAuthority.ADMIN.getAuthority())
                                .anyRequest().hasAuthority(UserAuthority.MODERATOR_FULL.getAuthority()))
                .formLogin(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
