package net.techbridges.telegdash.telegdashTelethonClientGateway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AdminService {
    @Value("${api.telegdash}")
    private String BASEURL;
    private final AuthenticationService auth;

    public String urlBuilder(){
        return BASEURL.concat("/admin");
    }

    public String sendMessageToAdmin(RestTemplate restTemplate, String chatId, String message){
        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("chat_id", chatId);
        requestBody.put("message", message);
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, auth.authenticate());
        ResponseEntity<HashMap> responseEntity = restTemplate.exchange(
                urlBuilder().concat("/send_reminder"),
                HttpMethod.POST,
                requestEntity,
                HashMap.class
        );
        return String.valueOf(responseEntity.getBody().get("message"));
    }

    public Integer checkAdminStatus(RestTemplate restTemplate, String channelId){
        HttpEntity<Object> requestEntity = new HttpEntity<>(channelId, auth.authenticate());
        ResponseEntity<Integer> responseEntity = restTemplate.exchange(
                urlBuilder().concat("/status/"+channelId),
                HttpMethod.GET,
                requestEntity,
                Integer.class
        );
        return responseEntity.getBody();
    }
}
