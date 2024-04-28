package net.techbridges.telegdash.telegdashTelethonClientGateway.controller;


import lombok.RequiredArgsConstructor;
import net.techbridges.telegdash.telegdashTelethonClientGateway.model.BasicMember;
import net.techbridges.telegdash.telegdashTelethonClientGateway.service.AdminService;
import net.techbridges.telegdash.telegdashTelethonClientGateway.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiController {

    private final MemberService memberService;
    private final AdminService adminService;
    private final RestTemplate restTemplate;


    public Integer getMembersCount(String channelId) {
        return memberService.getMembersCount(restTemplate, channelId);
    }


    public List<BasicMember> getAllMembers(String channelId, double limit) {
        return memberService.getAllMembers(restTemplate, channelId, limit);
    }

    public int kickMember(String channelId, List<String> memberIds) {
        return memberService.kickMember(restTemplate, channelId, memberIds);
    }

    public String sendMessageToAdmin(String chatId, String message) {
        return adminService.sendMessageToAdmin(restTemplate, chatId, message);
    }

}
