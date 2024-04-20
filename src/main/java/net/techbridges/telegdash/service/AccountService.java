package net.techbridges.telegdash.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.techbridges.telegdash.configuration.token.JwtService;
import net.techbridges.telegdash.configuration.token.Token;
import net.techbridges.telegdash.configuration.token.TokenRepository;
import net.techbridges.telegdash.configuration.token.TokenType;
import net.techbridges.telegdash.dto.request.AccountAuthRequest;
import net.techbridges.telegdash.dto.response.AccountAuthResponse;
import net.techbridges.telegdash.exception.RequestException;
import net.techbridges.telegdash.model.Account;
import net.techbridges.telegdash.repository.AccountRepository;
import net.techbridges.telegdash.utils.EmailChecker;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;


    public AccountAuthResponse register(AccountAuthRequest account) {
        String email = EmailChecker.normalizeEmail(account.username());
        if(accountRepository.findByUsername(email).isEmpty()){
            Account toSave = accountRepository.save(Account.builder()
                    .username(email)
                    .password(passwordEncoder.encode(account.password()))
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
            return new AccountAuthResponse(email, jwtToken, refreshToken);
        }else {
            throw new RequestException("The username provided already exist", HttpStatus.CONFLICT);
        }

    }

    public AccountAuthResponse authenticate(AccountAuthRequest account) {
        Optional<Account> toAuthenticate = accountRepository.findByUsername(account.username());
        if (!toAuthenticate.isPresent()) {
            System.out.println("Account doesn't exist");
        } else if (!passwordEncoder.matches(account.password(), toAuthenticate.get().getPassword())) {
            System.out.println("The password you entered is incorrect");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        account.username(),
                        account.password()
                )
        );
        var jwtToken = jwtService.generateToken(new HashMap<>(), toAuthenticate.get());
        var refreshToken = jwtService.generateRefreshToken(toAuthenticate.get());
        /** @Ignore revoking previous tokens in case user is connected in another device*/
        //revokeAllUserTokens(user);
        saveUserToken(Account.builder().username(account.username()).password(account.password()).build(), jwtToken);
        return new AccountAuthResponse(account.username(), jwtToken, refreshToken);

    }


    private void saveUserToken(Account account, String jwtToken) {
        var token = Token.builder()
                .account(account)
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
            var user = this.accountRepository.findByUsername(username).orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var newToken = jwtService.generateToken(new HashMap<>(), user);
                jwtService.revokeAllUserTokens(user.getUsername());
                saveUserToken(user, newToken);
                var _response = new AccountAuthResponse(username, newToken, refreshToken);
                new ObjectMapper()
                        .writeValue(
                                response.getOutputStream(),
                                _response);
            }
        }
    }


}
