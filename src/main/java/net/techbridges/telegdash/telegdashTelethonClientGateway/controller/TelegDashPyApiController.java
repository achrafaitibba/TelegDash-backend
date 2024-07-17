package net.techbridges.telegdash.telegdashTelethonClientGateway.controller;


import lombok.RequiredArgsConstructor;
import net.techbridges.telegdash.telegdashTelethonClientGateway.model.BasicMember;
import net.techbridges.telegdash.telegdashTelethonClientGateway.service.AdminService;
import net.techbridges.telegdash.telegdashTelethonClientGateway.service.TelegramMemberService;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TelegDashPyApiController {

    private final TelegramMemberService memberService;
    private final AdminService adminService;
    private final RestTemplate restTemplate;


    public Integer getMembersCount(String channelId) {
        return memberService.getMembersCount(restTemplate, channelId);
    }


    public List<BasicMember> getAllMembers(String channelId, double limit) {
        return memberService.getAllMembers(restTemplate, channelId, limit);
    }

    public int kickMembers(String channelId, List<String> memberIds) {
        return memberService.kickMembers(restTemplate, channelId, memberIds);
    }

    public String sendMessageToAdmin(String phoneNumber, Object[] users, String message){
        return adminService.sendMessageToUsers(restTemplate, phoneNumber, users, message);
    }

    public String sendMessageToAdmin(String chatId, String message) {
        return adminService.sendMessageToAdmin(restTemplate, chatId, message);
    }

    /**
     * 1 = BOT is admin
     * 2 = Bot is admin and have ban_user right > for groups
     * 3 = Bot is admin and doesn't have ban_user right > for groups
     * 4 = Bot is not admin
     */
    public Integer checkAdminStatus(String channelId){
        return adminService.checkAdminStatus(restTemplate, channelId);
    }

}
