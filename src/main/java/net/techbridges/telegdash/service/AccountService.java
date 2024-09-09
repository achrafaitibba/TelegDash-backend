package net.techbridges.telegdash.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.techbridges.telegdash.annotation.SubscriptionChecker;
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
import net.techbridges.telegdash.model.Feedback;
import net.techbridges.telegdash.model.Plan;
import net.techbridges.telegdash.model.enums.Role;
import net.techbridges.telegdash.model.enums.SubscriptionType;
import net.techbridges.telegdash.paymentService.paypal.controller.PaymentController;
import net.techbridges.telegdash.paymentService.paypal.dto.CreateSubscriptionRequest;
import net.techbridges.telegdash.paymentService.paypal.dto.ReviseSubscriptionRequest;
import net.techbridges.telegdash.paymentService.paypal.model.Link;
import net.techbridges.telegdash.paymentService.paypal.model.Subscription;
import net.techbridges.telegdash.repository.AccountRepository;
import net.techbridges.telegdash.repository.FeedbackRepository;
import net.techbridges.telegdash.repository.PlanRepository;
import net.techbridges.telegdash.utils.InputChecker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;
    private final PlanRepository planRepository;
    private final PaymentController paymentController;
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final FeedbackRepository feedbackRepository;
    private final HttpServletRequest headers;
    @Value("${app.security.allowed.origin}")
    private String origin;

    @Transactional
    public AccountRegisterResponse register(AccountRegisterRequest account) throws Exception{
        String email = InputChecker.normalizeEmail(account.email());
        if (accountRepository.findByEmail(email).isEmpty()) {
            Plan usedPlan = planRepository.findById(account.planId()).get();
            Account toSave = accountToRegister(account);
            if (usedPlan.getIsActive()) {

                if(usedPlan.getSubscriptionType().equals(SubscriptionType.PAID)){
                    Subscription subscription = createSubscription(account.email());
                    Map<String, Object> claims = new HashMap<>();
                    claims.put("subscriptionId", subscription.getId());
                    var jwtToken = jwtService.generateToken(claims, toSave);
                    var refreshToken = jwtService.generateRefreshToken(toSave);
                    saveUserToken(toSave, jwtToken);
                    toSave.setSubscriptionId(subscription.getId());
                    accountRepository.save(toSave);
                    return new AccountRegisterResponse(toSave.getUsername(), toSave.getPlan().getPlanId(), extractSubscriptionUrl(subscription), jwtToken, refreshToken);
                }else{
                    Map<String, Object> claims = new HashMap<>();
                    claims.put("subscriptionId", "null");
                    var jwtToken = jwtService.generateToken(claims, toSave);
                    var refreshToken = jwtService.generateRefreshToken(toSave);
                    saveUserToken(toSave, jwtToken);
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
                .plan(planRepository.findById(account.planId()).get())
                .build());
    }

    public Subscription createSubscription(String username) throws Exception{
        Plan usedPlan = planRepository.findById(accountRepository.findByEmail(username).get().getPlan().getPlanId()).get();
        LocalDate currentDate = LocalDate.now().plusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(formatter);
        return paymentController.createSubscription(new CreateSubscriptionRequest(usedPlan.getPaypalPlanId(), formattedDate, "https://app.telegdash.com","https://app.telegdash.com"));

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
        Map<String, Object> claims = new HashMap<>();
        claims.put("subscriptionId", toAuthenticate.get().getSubscriptionId());
        var jwtToken = jwtService.generateToken(claims, toAuthenticate.get());
        var refreshToken = jwtService.generateRefreshToken(toAuthenticate.get());
        /** revoking previous tokens in case user is connected in another device*/
        //revokeAllUserTokens(user);
        saveUserToken(Account.builder().email(email).password(account.password()).build(), jwtToken);
        return new AccountAuthResponse(email, jwtToken, refreshToken, toAuthenticate.get().getPhoneNumber());
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
                var _response = new AccountAuthResponse(username, newToken, refreshToken, user.getPhoneNumber());
                new ObjectMapper()
                        .writeValue(
                                response.getOutputStream(),
                                _response);
            }
        }
    }

    public String generatePasswordRecoverUrl(String email) {
        Optional<Account> account = accountRepository.findByEmail(email);
        if (account.isEmpty()) {
            throw new RequestException("User not found", HttpStatus.NOT_FOUND);
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(account.get().getUsername());
        var jwtToken = jwtService.generateRecoverPasswordToken(userDetails);
        saveUserToken(Account.builder().email(email).password(account.get().getPassword()).build(), jwtToken);
        return origin.concat("/recover-password?token=".concat(jwtToken));
    }


    public AccountAuthResponse recoverPassword(String email, String newPassword) {
        Account account = accountRepository.findByEmail(email).orElseThrow(() -> new RequestException("User not found with email: " + email, HttpStatus.NOT_FOUND));
        account.setPassword(bCryptPasswordEncoder.encode(newPassword));
        accountRepository.save(account);
        Map<String, Object> claims = new HashMap<>();
        claims.put("subscriptionId", account.getSubscriptionId());
        var jwtToken = jwtService.generateToken(claims, account);
        var refreshToken = jwtService.generateRefreshToken(account);
        saveUserToken(Account.builder().email(email).password(account.getPassword()).build(), jwtToken);
        return new AccountAuthResponse(email, jwtToken, refreshToken, account.getPhoneNumber());
    }

    public Feedback saveFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }


    public String cancelSubscription(String raison) throws Exception{
        String response = "";
        String token = headers.getHeader("Authorization").substring(7);
        String subscriptionId = jwtService.extractAllClaims(token).get("subscriptionId").toString();
        if(!"null".equals(subscriptionId)){
            int cancelReq = paymentController.cancelSubscription(subscriptionId, raison);
            if(cancelReq == 1){
                response = "Subscription cancelled";
            }else {
                response = "An error occurred, you may need to cancel via your paypal account";
            }
        }else{
            response = "Not subscribed yet";
        }
        return response;
    }
    public List<Integer> upgradeStatus() {
        List<Integer> plans = new ArrayList<>();
        String token = headers.getHeader("Authorization").substring(7);
        String email = jwtService.extractUsername(token);
        Optional<Account> account = accountRepository.findByEmail(email);
        Optional<Plan> plan = planRepository.findById(account.get().getPlan().getPlanId());
        Integer planLevel = plan.get().getPlanLevel();
        for(int i = planLevel + 1; i <= 4; i++){
            plans.add(i);
        }
        return plans;
    }

    public String upgrade(Long planId) throws Exception {
        String token = headers.getHeader("Authorization").substring(7);
        String email = jwtService.extractUsername(token);
        Optional<Account> account = accountRepository.findByEmail(email);
        Plan plan = planRepository.findById(planId).get();
        if(plan.getPlanLevel() < planRepository.findById(account.get().getPlan().getPlanId()).get().getPlanLevel()){
            throw new RequestException("We don't support downgrading plans for now", HttpStatus.BAD_REQUEST);
        }
        if("null".equals(account.get().getSubscriptionId())){
            account.get().setPlan(plan);
            accountRepository.save(account.get());
            Subscription newSub = createSubscription(email);
            account.get().setSubscriptionId(newSub.getId());
            accountRepository.save(account.get());
            return extractSubscriptionUrl(newSub);
        }else{
            try{
                paymentController.reviseSubscription(new ReviseSubscriptionRequest(account.get().getSubscriptionId(), plan.getPaypalPlanId()));
                account.get().setPlan(plan);
                accountRepository.save(account.get());
                return "Success";
            }catch (Exception e){
                log.error(e.getMessage());
                throw new RequestException("Error occurred in our server", HttpStatus.BAD_REQUEST);
            }
        }
    }

    @SubscriptionChecker
    public Integer subscriptionStatus() {
        return 1;
    }

    public String paymentStatus() throws Exception {
        String token = headers.getHeader("Authorization").substring(7);
        String email = jwtService.extractUsername(token);
        Optional<Account> account = accountRepository.findByEmail(email);
        String subsId = account.get().getSubscriptionId();
        if(subsId.equals("null")){
            return "Not Subscribed yet";
        }
        System.out.println("Subs: " + subsId);
        return paymentController.getSubscriptionDetails(subsId).getStatus().toString();
    }

    public String approveSubscription() throws Exception{
        String token = headers.getHeader("Authorization").substring(7);
        String email = jwtService.extractUsername(token);
        Optional<Account> account = accountRepository.findByEmail(email);
        String subsId = account.get().getSubscriptionId();
        return paymentController.approveSubscriptionUrl(subsId);
    }

}