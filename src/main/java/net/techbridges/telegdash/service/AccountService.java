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
import net.techbridges.telegdash.dto.request.AccountRegisterRequest;
import net.techbridges.telegdash.dto.response.AccountAuthResponse;
import net.techbridges.telegdash.dto.response.AccountRegisterResponse;
import net.techbridges.telegdash.exception.RequestException;
import net.techbridges.telegdash.model.Account;
import net.techbridges.telegdash.model.enums.AccountType;
import net.techbridges.telegdash.repository.AccountRepository;
import net.techbridges.telegdash.repository.PlanRepository;
import net.techbridges.telegdash.utils.InputChecker;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private final PlanRepository planRepository;

    public AccountRegisterResponse register(AccountRegisterRequest account) {
        String email = InputChecker.normalizeEmail(account.username());
        if (accountRepository.findByUsername(email).isEmpty()) {
            if (planRepository.findById(account.planId()).get().getIsActive()) {
                LocalDate currentDate = LocalDate.now();
                LocalDate freeTrialEndDate = currentDate.plusDays(7);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                String formattedDate = freeTrialEndDate.format(formatter);
                Account toSave = accountRepository.save(Account.builder()
                        .username(email)
                        .password(passwordEncoder.encode(account.password()))
                        .plan(planRepository.findById(account.planId()).get())
                        .accountType(AccountType.TRIAL)
                        .freeTrialEndDate(formattedDate)
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
                return new AccountRegisterResponse(email, formattedDate, toSave.getPlan().getPlanId(), toSave.getAccountType().toString(), jwtToken, refreshToken);
            } else {
                throw new RequestException("The plan is not Active for now", HttpStatus.CONFLICT);
            }
        } else {
            throw new RequestException("The username provided already exist", HttpStatus.CONFLICT);
        }

    }

    public AccountAuthResponse authenticate(AccountAuthRequest account) {
        String email = InputChecker.normalizeEmail(account.username());
        Optional<Account> toAuthenticate = accountRepository.findByUsername(email);
        if (!toAuthenticate.isPresent()) {
            throw new RequestException("Account doesn't exist", HttpStatus.CONFLICT);
        } else if (!passwordEncoder.matches(account.password(), toAuthenticate.get().getPassword())) {
            throw new RequestException("The password you entered is incorrect", HttpStatus.CONFLICT);
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        account.password()
                )
        );
        var jwtToken = jwtService.generateToken(new HashMap<>(), toAuthenticate.get());
        var refreshToken = jwtService.generateRefreshToken(toAuthenticate.get());
        /** revoking previous tokens in case user is connected in another device*/
        //revokeAllUserTokens(user);
        saveUserToken(Account.builder().username(email).password(account.password()).build(), jwtToken);
        return new AccountAuthResponse(email, jwtToken, refreshToken);

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
