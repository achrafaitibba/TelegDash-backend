package net.techbridges.telegdash.telegdashTelethonClientGateway.dataSource;


import lombok.RequiredArgsConstructor;
import net.techbridges.telegdash.telegdashTelethonClientGateway.model.BasicMember;
import net.techbridges.telegdash.telegdashTelethonClientGateway.service.AdminService;
import net.techbridges.telegdash.telegdashTelethonClientGateway.service.MemberService;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RequiredArgsConstructor
public class ApiGateway {

    private final RestTemplate restTemplate;
    private final HttpHeaders httpHeaders;
    private final MemberService memberService;
    private final AdminService adminService;

    public Integer getMembersCount(String channelId) {
        return memberService.getMembersCount(httpHeaders, restTemplate, channelId);
    }

    public List<BasicMember> getAllMembers(String channelId, double limit) {
        return memberService.getAllMembers(httpHeaders, restTemplate, channelId, limit);
    }

    public int kickMember(String channelId, List<String> memberIds) {
        return memberService.kickMember(httpHeaders, restTemplate, channelId, memberIds);
    }

    public String sendMessageToAdmin(String chatId, String message) {
        return adminService.sendMessageToAdmin(httpHeaders, restTemplate, chatId, message);
    }

}
