package net.techbridges.telegdash.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.techbridges.telegdash.configuration.token.JwtService;
import net.techbridges.telegdash.configuration.token.Token;
import net.techbridges.telegdash.configuration.token.TokenRepository;
import net.techbridges.telegdash.configuration.token.TokenType;
import net.techbridges.telegdash.dto.request.UserAuthRequest;
import net.techbridges.telegdash.dto.response.UserAuthResponse;
import net.techbridges.telegdash.model.User;
import net.techbridges.telegdash.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;


    public UserAuthResponse register(UserAuthRequest user) {
        User toSave = userRepository.save(User.builder()
                .username(user.username())
                .password(passwordEncoder.encode(user.password()))
                .build());
        /** Instead of initiating an empty hashmap you can create a list of claims and add them to the hashmap
         Such as birthdate, account status... and any other data needed to be sent to the client whiting the token
         Example:
         Map<String, Object> currentDate = new HashMaps<>();
         currentDate.put("now", LocalDateTime.now()....);
         Claims could be : email, pictureLink, roles & groups , authentication time...
        */
        var jwtToken = jwtService.generateToken(new HashMap<>(), toSave);
        var refreshToken = jwtService.generateRefreshToken(toSave);
        saveUserToken(toSave, jwtToken);
        return new UserAuthResponse(user.username(), jwtToken, refreshToken);
    }

    public UserAuthResponse authenticate(UserAuthRequest user) {
        Optional<User> toAuthenticate = userRepository.findByUsername(user.username());
        if (!toAuthenticate.isPresent()) {
            System.out.println("Account doesn't exist");
        } else if (!passwordEncoder.matches(user.password(), toAuthenticate.get().getPassword())) {
            System.out.println("The password you entered is incorrect");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.username(),
                        user.password()
                )
        );
        var jwtToken = jwtService.generateToken(new HashMap<>(), toAuthenticate.get());
        var refreshToken = jwtService.generateRefreshToken(toAuthenticate.get());
        /** @Ignore revoking previous tokens in case user is connected in another device*/
        //revokeAllUserTokens(user);
        saveUserToken(User.builder().username(user.username()).password(user.password()).build(), jwtToken);
        return new UserAuthResponse(user.username(), jwtToken, refreshToken);

    }


    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }



    public void refreshToken(HttpServletRequest request,
                             HttpServletResponse response
    ) throws Exception {
        final String authHeader = request.getHeader("Authorization");
        final String refreshToken;
        final String username;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        /** Extract user email from JWT token; because we set the email as username in the user Model */
        username = jwtService.extractUsername(refreshToken);
        if (username != null) {
            var user = this.userRepository.findByUsername(username).orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var newToken = jwtService.generateToken(new HashMap<>(), user);
                jwtService.revokeAllUserTokens(user.getUsername());
                saveUserToken(user, newToken);
                var _response = new UserAuthResponse(username, newToken, refreshToken);
                new ObjectMapper()
                        .writeValue(
                                response.getOutputStream(),
                                _response);
            }
        }
    }


}
