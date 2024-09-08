package net.techbridges.telegdash.configuration.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.techbridges.telegdash.configuration.token.JwtService;
import net.techbridges.telegdash.configuration.token.Token;
import net.techbridges.telegdash.configuration.token.TokenRepository;
import net.techbridges.telegdash.exception.RequestException;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository ITokenRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            final String authHeader = request.getHeader("Authorization");
            final String jwt;
            final String username;
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                /**
                 * It will go to the next filter in the filterChain, because all filters are chained (Invoke the next filter)
                 * The last element of the chain is the target resource/servlet.
                 */
                filterChain.doFilter(request, response);
                return; /** empty return: means the program will not execute the rest of the code */
            }
            jwt = authHeader.substring(7); /**  7 = "Bearer".length + 1 , space*
             /**
             * extract username
             * "could be email/ID or any attribute set to be username in UserDetails instance" from JWT token;
             * in this case I set username in the user Model
             */
            username = jwtService.extractUsername(jwt);

            Token token = ITokenRepository.findByToken(jwt).orElseThrow(() -> new RequestException("Token not found", HttpStatus.UNAUTHORIZED));
            if (token.isExpired() || token.isRevoked()) {
                throw new RequestException("Token is expired or revoked", HttpStatus.FORBIDDEN);
            }

            /**
             * @SecurityContextHolder.getContext().getAuthentication()
             * even in a stateless application,
             * the server may need to maintain some context during the processing of a request.
             * In a stateless application, the SecurityContext is typically cleared after each request, as the server doesn't store any session state. However, during the processing of a single request, the server may temporarily use the SecurityContext to hold authentication information.
             * The check SecurityContextHolder.getContext().getAuthentication() == null
             * is likely used to ensure that the authentication process
             * (loading user details, validating the token, and setting the authentication in the context)
             * is only performed once per request. If the context already contains an authentication object,
             * it means that the authentication process has already been performed,
             * and there's no need to repeat it.
             **/
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                var isTokenValid = ITokenRepository.findByToken(jwt)
                        .map(t -> !t.isExpired() && !t.isRevoked())
                        .orElse(false);
                if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (RequestException e) {
            response.setStatus(e.getHttpStatus().value());
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"message\": \"" + e.getMessage() + "\", " +
                            "\"httpStatus\": \"" + e.getHttpStatus() + "\", " +
                            "\"httpStatusNumber\": " + e.getHttpStatus().value() + ", " +
                            "\"timestamp\": \"" + ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT) + "\"}"
            );
        }
    }
}
