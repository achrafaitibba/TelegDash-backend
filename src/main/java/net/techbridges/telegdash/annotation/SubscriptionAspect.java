package net.techbridges.telegdash.annotation;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import net.techbridges.telegdash.configuration.token.JwtService;
import net.techbridges.telegdash.exception.RequestException;
import net.techbridges.telegdash.paymentService.paypal.controller.PaymentController;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;


@Aspect
@Component
@AllArgsConstructor
public class SubscriptionAspect {
    private final PaymentController paymentController;
    private final HttpServletRequest headers;
    private final JwtService jwtService;


    @Before("@annotation(net.techbridges.telegdash.annotation.SubscriptionValidator)")
    public void checkSubscription(JoinPoint joinPoint) throws Exception{
        System.out.println("Before calling method: " + joinPoint.getSignature().getName());
        String token = headers.getHeader("Authorization").substring(7);
        String subscriptionId = jwtService.extractAllClaims(token).get("subscriptionId").toString();

        if (!isSubscriptionActive(subscriptionId)) {
            throw new RequestException("Subscription is not active", HttpStatus.UNAUTHORIZED);
        }else {
            System.out.println("Subscription is active, ID: " + subscriptionId);
        }
    }

    private String subscriptionStatus(String subsId) throws Exception{
        return paymentController.getSubscriptionDetails(subsId).getStatus().toString();
    }

    private Boolean isSubscriptionActive(String subsId)throws Exception{
        return subscriptionStatus(subsId).equals("ACTIVE");
    }
}
