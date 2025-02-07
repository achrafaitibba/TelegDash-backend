package net.techbridges.telegdash.annotation;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.techbridges.telegdash.configuration.token.JwtService;
import net.techbridges.telegdash.exception.RequestException;
import net.techbridges.telegdash.model.Account;
import net.techbridges.telegdash.paymentService.paypal.controller.PaymentController;
import net.techbridges.telegdash.repository.AccountRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;


@Slf4j
@Aspect
@Component
@AllArgsConstructor
public class SubscriptionAspect {
    private final PaymentController paymentController;
    private final HttpServletRequest headers;
    private final JwtService jwtService;
    private final AccountRepository accountRepository;

    @Before("@annotation(net.techbridges.telegdash.annotation.SubscriptionChecker)")
    public void checkSubscription(JoinPoint joinPoint) throws Exception{
        System.out.println("Before calling method: " + joinPoint.getSignature().getName());
        String token = headers.getHeader("Authorization").substring(7);
        Account account = accountRepository.findByEmail(jwtService.extractUsername(token)).get();
        String subscriptionId = account.getSubscriptionId();
        if(!subscriptionId.equals("null")){
            if(!isSubscriptionActive(subscriptionId)){
                throw new RequestException("Subscription is not active", HttpStatus.UNAUTHORIZED);
            }else{
                log.info("Subscription is active, ID: " + subscriptionId);
            }
        }else{
            log.info("Free plan, subscription is active");
        }
//        if(!subscriptionStatus(subscriptionId).equals("null") || !isSubscriptionActive(subscriptionId)){
//            throw new RequestException("Subscription is not active", HttpStatus.UNAUTHORIZED);
//        }else {
//            System.out.println("Subscription is active, ID: " + subscriptionId);
//        }

    }

    private String subscriptionStatus(String subsId) throws Exception{
        return paymentController.getSubscriptionDetails(subsId).getStatus().toString();
    }

    private Boolean isSubscriptionActive(String subsId)throws Exception{
        return subscriptionStatus(subsId).equals("ACTIVE");
    }
}
