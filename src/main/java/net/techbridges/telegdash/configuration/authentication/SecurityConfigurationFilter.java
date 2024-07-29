package net.techbridges.telegdash.configuration.authentication;



import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfigurationFilter {

    private final JwtAuthenticationFIlter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutService logoutHandler;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, ServerProperties serverProperties)throws Exception{
        httpSecurity
                /** to allow cors from all origins */
                .csrf()
                .disable()
                .cors()
                /////////////////
                .and()
                /** authorized endpoints: doesn't require authentication */
                .authorizeHttpRequests()
                .requestMatchers(
                        "/api/v1.0/accounts/register",
                        "/api/v1.0/accounts/refresh-token",
                        "/api/v1.0/accounts/authenticate",
                        "/api/v1.0/plans/**",
                        "/api/v1.0/channels/status/**",
                        "/api/v1.0/channels/niches",
                        "/api/v1.0/accounts/recover-password-url/**",
                        "/api/v1.0/accounts/test",
                        "/api/v1.0/accounts/feedback",
                        "swagger-ui/index.html",
                        "/swagger-ui/index.html#",
                        "/v2/api-docs",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/swagger-resources",
                        "/swagger-resources/**",
                        "/configuration/ui",
                        "/configuration/security",
                        "/swagger-ui/**",
                        "/webjars/**",
                        "/swagger-ui.html"
                )
                .permitAll()
                //////////////////////
                /** ask authentication for any other request */
                .anyRequest()
                .authenticated()
                .and()
                ////////////////////////////
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                /** enable frames, I used it for swagger documentation to put it inside an IFRAME tag */
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout()
                .logoutUrl("/api/v1.0/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler(
                        (request,
                         response,
                         authentication) ->
                        SecurityContextHolder.clearContext())
        ;
        return httpSecurity.build();
    }
}
