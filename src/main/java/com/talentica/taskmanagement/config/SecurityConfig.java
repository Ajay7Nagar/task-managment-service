package com.talentica.taskmanagement.config;

import com.talentica.taskmanagement.security.JwtAuthenticationEntryPoint;
import com.talentica.taskmanagement.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                    // Public endpoints
                    .antMatchers("/api/auth/**").permitAll()
                    .antMatchers("/api/health/**").permitAll()
                    
                    // Swagger endpoints
                    .antMatchers("/v3/api-docs/**").permitAll()
                    .antMatchers("/swagger-ui/**").permitAll()
                    .antMatchers("/swagger-ui.html").permitAll()
                    .antMatchers("/swagger-resources/**").permitAll()
                    .antMatchers("/webjars/**").permitAll()
                    
                    // Admin endpoints
                    .antMatchers("/api/admin/**").hasRole("ADMIN")
                    
                    // User management endpoints
                    .antMatchers(HttpMethod.GET, "/api/users/**").permitAll()
                    .antMatchers(HttpMethod.POST, "/api/users").permitAll()
                    .antMatchers(HttpMethod.PUT, "/api/users/**").hasAnyRole("ADMIN", "MANAGER")
                    .antMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
                    
                    // Task endpoints
                    .antMatchers(HttpMethod.GET, "/api/tasks/**").authenticated()
                    .antMatchers(HttpMethod.POST, "/api/tasks").authenticated()
                    .antMatchers(HttpMethod.PUT, "/api/tasks/**").authenticated()
                    .antMatchers(HttpMethod.DELETE, "/api/tasks/**").hasAnyRole("ADMIN", "MANAGER")
                    
                    // All other endpoints require authentication
                    .anyRequest().authenticated();

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
