
package net.techbridges.telegdash.paymentService.paypal.service;

\import lombok.RequiredArgsConstructor;
import net.techbridges.telegdash.paymentService.paypal.dto.request.CreateSubscriptionRequest;
import net.techbridges.telegdash.paymentService.paypal.model.BaseUrl;
import net.techbridges.telegdash.paymentService.paypal.model.Subscription;
import org.springframework.http.*;
\import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class SubscriptionService {
    /**
     * todo
     * create
     * check status
     * cancel
     * upgrade
     * downgrade
     */
    private final PaypalAuthenticationService authenticationService;
    private final HttpHeaders httpHeaders;
    private final RestTemplate restTemplate;
    private final BaseUrl baseUrl;

    public Subscription createSubscription(CreateSubscriptionRequest subscription) throws Exception {
        String token = authenticationService.generateToken().getAccessToken();
        httpHeaders.add("Prefer", "return=representation");
        httpHeaders.add("Authorization", "Bearer " + token);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON_UTF8));
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("plan_id", subscription.planId());
        requestBody.put("start_time", subscription.startTime() + "T00:00:00Z");
        Map<String, String> applicationContext = new HashMap<>();
        applicationContext.put("return_url", subscription.returnUrl());
        applicationContext.put("cancel_url", subscription.cancelUrl());
        requestBody.put("application_context", applicationContext);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, httpHeaders);
        ResponseEntity<Subscription> response = restTemplate.exchange(
                baseUrl.getBaseUrl() + "v1/billing/subscriptions",
                HttpMethod.POST,
                requestEntity,
                Subscription.class
        );
        return response.getBody();
    }


}