package net.techbridges.telegdash.telegdashTelethonClientGateway.service;


import lombok.RequiredArgsConstructor;
import net.techbridges.telegdash.exception.RequestException;
import net.techbridges.telegdash.telegdashTelethonClientGateway.model.BasicMember;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {
    @Value("${api.telegdash}")
    private String BASEURL;
    private final AuthenticationService auth;

    public String urlBuilder() {
        return BASEURL.concat("/members");
    }

    public Integer getMembersCount(RestTemplate restTemplate, String channelId) {
        try {
            HttpEntity<Object> requestEntity = new HttpEntity<>(channelId, auth.authenticate());
            ResponseEntity<HashMap> responseEntity = restTemplate.exchange(
                    urlBuilder().concat("/" + channelId + "/count"),
                    HttpMethod.GET,
                    requestEntity,
                    HashMap.class
            );
            return Integer.valueOf(responseEntity.getBody().get("count").toString());
        } catch (HttpClientErrorException e) {
            throw new RequestException(e.getResponseBodyAsString().substring(1, e.getResponseBodyAsString().length() - 2), HttpStatus.valueOf(e.getStatusCode().value()));
        }
    }


    public List<BasicMember> getAllMembers(RestTemplate restTemplate, String channelId, double limit) {
        try {
            HashMap<String, Object> requestBody = new HashMap<>();
            requestBody.put("channel_id", channelId);
            requestBody.put("limit", limit);
            HttpEntity<HashMap<String, Object>> requestEntity = new HttpEntity<>(requestBody, auth.authenticate());
            ResponseEntity<List> responseEntity = restTemplate.exchange(
                    urlBuilder(),
                    HttpMethod.POST,
                    requestEntity,
                    List.class
            );
            List<HashMap<String, Object>> responseData = responseEntity.getBody();
            List<BasicMember> members = new ArrayList<>();
            responseData.forEach(memberData -> {
                String telegramId = memberData.get("member_id") != null ? memberData.get("member_id").toString() : "No Data";
                String username = memberData.get("username") != null ? memberData.get("username").toString() : "No Data";
                String firstName = memberData.get("firstName") != null ? memberData.get("firstName").toString() : "No Data";
                String lastName = memberData.get("lastName") != null ? memberData.get("lastName").toString() : "No Data";
                members.add(new BasicMember(telegramId, username, firstName, lastName));
            });
            return members;

        } catch (HttpClientErrorException e) {
            throw new RequestException(e.getResponseBodyAsString().substring(1, e.getResponseBodyAsString().length() - 2), HttpStatus.valueOf(e.getStatusCode().value()));
        }
    }


    public int kickMember(RestTemplate restTemplate, String channelId, List<String> memberIds) {
        try {
            memberIds.forEach(
                    memberId -> {
                        HashMap<String, Object> requestBody = new HashMap<>();
                        requestBody.put("channel_id", channelId);
                        requestBody.put("member_id", memberId);
                        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, auth.authenticate());
                        ResponseEntity<HashMap> responseEntity = restTemplate.exchange(
                                urlBuilder().concat("/kick"),
                                HttpMethod.POST,
                                requestEntity,
                                HashMap.class
                        );
                        System.out.println(responseEntity.getBody().toString());
                    }
            );

            return 1;
        } catch (HttpClientErrorException e) {
            throw new RequestException(e.getResponseBodyAsString().substring(1, e.getResponseBodyAsString().length() - 2), HttpStatus.valueOf(e.getStatusCode().value()));
        }
    }
}
