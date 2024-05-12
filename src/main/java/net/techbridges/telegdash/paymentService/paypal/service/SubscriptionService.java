
package net.techbridges.telegdash.paymentService.paypal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.techbridges.telegdash.exception.RequestException;
import net.techbridges.telegdash.paymentService.paypal.dto.request.CreateSubscriptionRequest;
import net.techbridges.telegdash.paymentService.paypal.dto.request.ReviseSubscriptionRequest;
import net.techbridges.telegdash.paymentService.paypal.model.BaseUrl;
import net.techbridges.telegdash.paymentService.paypal.model.Link;
import net.techbridges.telegdash.paymentService.paypal.model.Subscription;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("api")
public class SubscriptionService {
    /**
     * todo
     * cancel
     */
    private final PaypalAuthenticationService authenticationService;
    private final HttpHeaders httpHeaders;
    private final RestTemplate restTemplate;
    private final BaseUrl baseUrl;


    private void requestHeaders() throws Exception {
        httpHeaders.clear();
        String token = authenticationService.generateToken().getAccessToken();
        httpHeaders.add("Authorization", "Bearer " + token);
    }

    public Subscription createSubscription(CreateSubscriptionRequest subscription) throws Exception {
        requestHeaders();
        httpHeaders.add("Prefer", "return=representation");
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

    public Subscription getSubscription(String subscriptionId) throws Exception {
        requestHeaders();
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(httpHeaders);
        ResponseEntity<Subscription> response = restTemplate.exchange(
                baseUrl.getBaseUrl() + "v1/billing/subscriptions/" + subscriptionId,
                HttpMethod.GET,
                requestEntity,
                Subscription.class
        );
        return response.getBody();
    }

    public String approveSubscription(Subscription subscription) throws Exception {
        return subscription.getLinks().stream()
                .filter(link -> link.getRel().equals("approve"))
                .findFirst()
                .map(Link::getHref)
                .orElse("");
    }

    public Subscription updateSubscription(ReviseSubscriptionRequest request) throws Exception {
        requestHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("plan_id", request.planId());
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, httpHeaders);
        ResponseEntity<Subscription> response = restTemplate.postForEntity(
                baseUrl.getBaseUrl() + "v1/billing/subscriptions/".concat(request.subscriptionId()).concat("/revise"),
                requestEntity,
                Subscription.class
        );
        return getSubscription(request.subscriptionId());
    }

    public Object reviseSubscription(ReviseSubscriptionRequest request) throws Exception {
        return isSubscriptionActive(request.subscriptionId()) ? updateSubscription(request) : new RequestException("The subscription is not ACTIVE yet", HttpStatus.CONFLICT).getMessage();
    }
    private Boolean isSubscriptionActive(String subscriptionId) throws Exception {
        return getSubscription(subscriptionId).getStatus().toString().equals("ACTIVE");
    }
}