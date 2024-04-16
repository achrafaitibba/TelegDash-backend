package net.techbridges.telegdash.telegdashTelethonClientGateway.dataSource;


import lombok.RequiredArgsConstructor;
import net.techbridges.telegdash.telegdashTelethonClientGateway.model.BasicMember;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@RestController
@RequestMapping("/count")
@RequiredArgsConstructor
public class ApiGateway {
    @Value("${api.base_url}")
    private String BASEURL;
    private final RestTemplate restTemplate;
    private final HttpHeaders httpHeaders;


    public Integer getMembersCount(String channelId){
        HttpEntity<Object> requestEntity = new HttpEntity<>(channelId, httpHeaders);
        ResponseEntity<HashMap> responseEntity = restTemplate.exchange(
                BASEURL.concat("/members/"+channelId+"/count"),
                HttpMethod.GET,
                requestEntity,
                HashMap.class
        );
        return Integer.valueOf(responseEntity.getBody().get("count").toString());
    }
@GetMapping
    public List<BasicMember> getAllMembers(String channelId, double limit) {

        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("channel_id", channelId);
        requestBody.put("limit", limit);
        HttpEntity<HashMap<String, Object>> requestEntity = new HttpEntity<>(requestBody, httpHeaders);
        ResponseEntity<List> responseEntity = restTemplate.exchange(
                BASEURL.concat("/members"),
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
            String lastName = memberData.get("lastName") != null ? memberData.get("lastName").toString()  : "No Data";
            members.add(new BasicMember(telegramId, username, firstName, lastName));
        });

        return members;
    }

    //todo, check members if they exist, before kicking them, if a record throws an error, continue kicking the rest of members
    public String kickMember(String channelId, List<String> memberIds) {
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
                            BASEURL.concat("/members/kick"),
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


    public String sendMessageToAdmin(String chatId, String message){
        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("chat_id", chatId);
        requestBody.put("message", message);
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, httpHeaders);
        ResponseEntity<HashMap> responseEntity = restTemplate.exchange(
                BASEURL.concat("/admin/send_reminder"),
                HttpMethod.POST,
                requestEntity,
                HashMap.class
        );
        return String.valueOf(responseEntity.getBody().get("message"));
    }


    //todo handle exceptions for each endpoint








}
