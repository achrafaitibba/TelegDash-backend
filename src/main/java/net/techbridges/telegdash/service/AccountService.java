package net.techbridges.telegdash.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
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
import net.techbridges.telegdash.model.Plan;
import net.techbridges.telegdash.model.enums.Role;
import net.techbridges.telegdash.model.enums.SubscriptionType;
import net.techbridges.telegdash.paymentService.paypal.controller.PaymentController;
import net.techbridges.telegdash.paymentService.paypal.dto.CreateSubscriptionRequest;
import net.techbridges.telegdash.paymentService.paypal.model.Link;
import net.techbridges.telegdash.paymentService.paypal.model.Subscription;
import net.techbridges.telegdash.repository.AccountRepository;
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
    private final PlanService planService;
    private final PaymentController paymentController;

    @Transactional
    public AccountRegisterResponse register(AccountRegisterRequest account) throws Exception{
        String email = account.email();
        if (accountRepository.findByEmail(email).isEmpty()) {
            Plan usedPlan = planService.getPlan(account.planId());
            Account toSave = accountToRegister(account);
            if (usedPlan.getIsActive()) {
                var jwtToken = jwtService.generateToken(new HashMap<>(), toSave);
                var refreshToken = jwtService.generateRefreshToken(toSave);
                saveUserToken(toSave, jwtToken);
                if(usedPlan.getSubscriptionType().equals(SubscriptionType.PAID)){
                    Subscription subscription = createSubscription(account.email());
                    toSave.setSubscriptionId(subscription.getId());
                    accountRepository.save(toSave);
                    return new AccountRegisterResponse(toSave.getUsername(), toSave.getPlan().getPlanId(), extractSubscriptionUrl(subscription), jwtToken, refreshToken);
                }else{
                    toSave.setSubscriptionId("null");
                    accountRepository.save(toSave);
                    return new AccountRegisterResponse(email, toSave.getPlan().getPlanId(), "null", jwtToken, refreshToken);
                }
            } else {
                throw new RequestException("The plan is not Active for now", HttpStatus.CONFLICT);
            }
        } else {
            throw new RequestException("The email provided already exist", HttpStatus.CONFLICT);
        }

    }
    private Account accountToRegister(AccountRegisterRequest account) throws Exception{
        String email = InputChecker.normalizeEmail(account.email());
        return accountRepository.save(Account.builder()
                .email(email)
                .role(Role.OWNER)
                .password(passwordEncoder.encode(account.password()))
                .plan(planService.getPlan(account.planId()))
                .build());
    }

    public Subscription createSubscription(String username) throws Exception{
        Plan usedPlan = planService.getPlan(accountRepository.findByEmail(username).get().getPlan().getPlanId());
        LocalDate currentDate = LocalDate.now().plusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(formatter);
        return paymentController.createSubscription(new CreateSubscriptionRequest(usedPlan.getPaypalPlanId(), formattedDate, "https://app.telegdash.com","https://telegdash.com"));

    }

    private String extractSubscriptionUrl(Subscription subscription){
        return subscription.getLinks().stream().filter(link -> link.getRel().equals("approve")).map(Link::getHref).findFirst().get();
    }
    public AccountAuthResponse authenticate(AccountAuthRequest account) {
        String email = InputChecker.normalizeEmail(account.email());
        Optional<Account> toAuthenticate = accountRepository.findByEmail(email);
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
        saveUserToken(Account.builder().email(email).password(account.password()).build(), jwtToken);
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
            var user = this.accountRepository.findByEmail(username).orElseThrow();
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