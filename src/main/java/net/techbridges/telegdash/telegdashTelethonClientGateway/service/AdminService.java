package net.techbridges.telegdash.telegdashTelethonClientGateway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
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

    public String sendMessageToUsers(RestTemplate restTemplate, String phoneNumber, Long[] users, String message){
        HashMap<String, Object> requestBody = new HashMap<>();
        for(Object user: users){
            System.out.println("User id: " + user.toString());
        }
        requestBody.put("phone_number", phoneNumber);
        requestBody.put("usernames", users);
        requestBody.put("message", message);
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, auth.authenticate());
        ResponseEntity<HashMap> responseEntity = restTemplate.exchange(
                urlBuilder().concat("/send-user-reminder"),
                HttpMethod.POST,
                requestEntity,
                HashMap.class
        );
        return String.valueOf(responseEntity.getBody().get("message"));
    }

    public String createSession(RestTemplate restTemplate, String phoneNumber){
        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("phone_number", phoneNumber);
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, auth.authenticate());
        ResponseEntity<HashMap> responseEntity = restTemplate.exchange(
                urlBuilder().concat("/create-session"),
                HttpMethod.POST,
                requestEntity,
                HashMap.class
        );
        return String.valueOf(responseEntity.getBody().get("message"));
    }

    public String submitCode(RestTemplate restTemplate, String phoneNumber, String code){
        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("phone_number", phoneNumber);
        requestBody.put("code", code);
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, auth.authenticate());
        ResponseEntity<HashMap> responseEntity = restTemplate.exchange(
                urlBuilder().concat("/submit-code"),
                HttpMethod.POST,
                requestEntity,
                HashMap.class
        );
        return String.valueOf(responseEntity.getBody().get("message"));
    }
}
