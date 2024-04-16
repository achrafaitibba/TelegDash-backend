package net.techbridges.telegdash.telegdashTelethonClientGateway.service;


import net.techbridges.telegdash.telegdashTelethonClientGateway.model.BasicMember;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class MemberService {
    @Value("${api.base_url}")
    private String BASEURL;

    public String urlBuilder() {
        return BASEURL.concat("/members");
    }

    public Integer getMembersCount(HttpHeaders httpHeaders, RestTemplate restTemplate, String channelId) {
        HttpEntity<Object> requestEntity = new HttpEntity<>(channelId, httpHeaders);
        ResponseEntity<HashMap> responseEntity = restTemplate.exchange(
                urlBuilder().concat("/" + channelId + "/count"),
                HttpMethod.GET,
                requestEntity,
                HashMap.class
        );
        return Integer.valueOf(responseEntity.getBody().get("count").toString());
    }


    public List<BasicMember> getAllMembers(HttpHeaders httpHeaders, RestTemplate restTemplate, String channelId, double limit) {

        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("channel_id", channelId);
        requestBody.put("limit", limit);
        HttpEntity<HashMap<String, Object>> requestEntity = new HttpEntity<>(requestBody, httpHeaders);
        ResponseEntity<List> responseEntity = restTemplate.exchange(
                urlBuilder(),
                HttpMethod.POST,
                requestEntity,
                List.class
        );
        List<BasicMember> members = new ArrayList<>();
        List<HashMap<String, Object>> responseData = responseEntity.getBody();
        responseData.forEach(memberData -> {
            String telegramId = memberData.get("member_id") != null ? memberData.get("member_id").toString() : "No Data";
            String username = memberData.get("username") != null ? memberData.get("username").toString() : "No Data";
            String firstName = memberData.get("firstName") != null ? memberData.get("firstName").toString() : "No Data";
            String lastName = memberData.get("lastName") != null ? memberData.get("lastName").toString() : "No Data";
            members.add(new BasicMember(telegramId, username, firstName, lastName));
        });

        return members;
    }


    //todo, check members if they exist, before kicking them, if a record throws an error, continue kicking the rest of members
    public String kickMember(HttpHeaders httpHeaders, RestTemplate restTemplate, String channelId, List<String> memberIds) {
        //todo, check the return
        /**
         * Make the client/frontend "not the user" see the kicked members list
         * {message=Member kicked successfully: 1111111}
         * {message=Member kicked successfully: 1111111}
         * {message=Member kicked successfully: 1111111}
         */

        //todo, use array instead of list of Strings
        memberIds.forEach(
                memberId -> {
                    HashMap<String, Object> requestBody = new HashMap<>();
                    requestBody.put("channel_id", channelId);
                    requestBody.put("member_id", memberId);
                    HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, httpHeaders);
                    ResponseEntity<HashMap> responseEntity = restTemplate.exchange(
                            urlBuilder().concat("/kick"),
                            HttpMethod.POST,
                            requestEntity,
                            HashMap.class
                    );
                    System.out.println(responseEntity.getBody().toString());
                }
        );

        //todo, change return type & message
        return "Done";

    }
}
