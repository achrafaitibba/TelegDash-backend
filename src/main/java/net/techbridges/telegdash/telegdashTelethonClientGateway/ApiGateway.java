package net.techbridges.telegdash.telegdashTelethonClientGateway;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;


@RestController
@RequestMapping("/count")
@RequiredArgsConstructor
public class ApiGateway {
    @Value("${api.base_url}")
    private String BASEURL;
    @Value("${api.auth.username}")
    private String username;
    @Value("${api.auth.password}")
    private String password;
    private final RestTemplate restTemplate;

    public Integer getMembersCount(String channelId){
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        HttpEntity<Object> requestEntity = new HttpEntity<>(channelId, headers);
        ResponseEntity<HashMap> responseEntity = restTemplate.exchange(
                BASEURL.concat("/members/"+channelId+"/count"),
                HttpMethod.GET,
                requestEntity,
                HashMap.class
        );
        return Integer.valueOf(responseEntity.getBody().get("count").toString());
    }










}
