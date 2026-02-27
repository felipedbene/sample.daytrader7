package com.ibm.daytrader.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;

import com.ibm.daytrader.repository.AccountProfileRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/welcome", "/register", "/login", "/error/**",
                        "/css/**", "/js/**", "/images/**", "/ws/**",
                        "/h2-console/**", "/actuator/health",
                        "/api/charts/**").permitAll()
                .requestMatchers("/admin/**", "/config/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/tradehome", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/welcome")
                .invalidateHttpSession(true)
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/ws/**", "/h2-console/**")
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin()) // for H2 console
            )
            .exceptionHandling(ex -> ex
                // HTMX requests get 401 instead of redirect to /login (prevents infinite recursion)
                .defaultAuthenticationEntryPointFor(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                    new RequestHeaderRequestMatcher("HX-Request", "true"))
                .defaultAuthenticationEntryPointFor(
                    new LoginUrlAuthenticationEntryPoint("/login"),
                    request -> true)
            );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(AccountProfileRepository profileRepo) {
        return username -> {
            var profile = profileRepo.findById(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
            return User.withUsername(profile.getUserID())
                    .password(profile.getPassword())
                    .roles("USER", "ADMIN")
                    .build();
        };
    }

    @Bean
    @SuppressWarnings("deprecation")
    public PasswordEncoder passwordEncoder() {
        // Plaintext for backward compatibility with the original DayTrader password scheme.
        // In production, migrate to BCryptPasswordEncoder with DelegatingPasswordEncoder.
        return NoOpPasswordEncoder.getInstance();
    }
}
