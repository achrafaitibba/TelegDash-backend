package net.techbridges.telegdash.paymentService.paypal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import net.techbridges.telegdash.paymentService.paypal.model.Authentication;
import net.techbridges.telegdash.paymentService.paypal.model.BaseUrl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PaypalAuthenticationService {
    @Value("${payment.paypal.client-id}")
    private String clientId;
    @Value("${payment.paypal.secret}")
    private String secret;

    private final BaseUrl baseUrl;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final HttpHeaders httpHeaders;


    public HttpHeaders authenticate() {
        httpHeaders.setBasicAuth(clientId, secret);
        return httpHeaders;
    }

    public Authentication generateToken() throws Exception {
        LinkedMultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "client_credentials");
        requestBody.add("ignoreCache", "true");
        requestBody.add("return_authn_schemes", "true");
        requestBody.add("return_client_metadata", "true");
        requestBody.add("return_unconsented_scopes", "true");
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, authenticate());
        String response = restTemplate.postForObject(baseUrl.getBaseUrl() + "v1/oauth2/token", requestEntity, String.class);
        return objectMapper.readValue(response, Authentication.class);

    }
}
