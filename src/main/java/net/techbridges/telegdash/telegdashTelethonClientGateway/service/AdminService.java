package net.techbridges.telegdash.telegdashTelethonClientGateway.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Service
public class AdminService {
    @Value("${api.base_url}")
    private String BASEURL;

    public String urlBuilder(){
        return BASEURL.concat("/admin");
    }

    public String sendMessageToAdmin(HttpHeaders httpHeaders, RestTemplate restTemplate, String chatId, String message){
        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("chat_id", chatId);
        requestBody.put("message", message);
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, httpHeaders);
        ResponseEntity<HashMap> responseEntity = restTemplate.exchange(
                urlBuilder().concat("/send_reminder"),
                HttpMethod.POST,
                requestEntity,
                HashMap.class
        );
        return String.valueOf(responseEntity.getBody().get("message"));
    }
}
