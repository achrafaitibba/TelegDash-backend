package net.techbridges.telegdash.service;

import lombok.AllArgsConstructor;
import net.techbridges.telegdash.annotation.SubscriptionChecker;
import net.techbridges.telegdash.dto.response.CustomColumnMemberResponse;
import net.techbridges.telegdash.dto.response.MemberResponse;
import net.techbridges.telegdash.dto.response.ValueResponse;
import net.techbridges.telegdash.exception.RequestException;
import net.techbridges.telegdash.model.*;
import net.techbridges.telegdash.model.enums.MemberStatus;
import net.techbridges.telegdash.repository.*;
import net.techbridges.telegdash.telegdashTelethonClientGateway.controller.TelegDashPyApiController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class MemberService {
    private final TelegramMemberRepository telegramMemberRepository;
    private final TelegDashPyApiController telegDashPyApiController;
    private final MemberRepository memberRepository;
    private final ValueRepository valueRepository;
    private final ChannelRepository channelRepository;


    @SubscriptionChecker
    public List<Object> getAllMembers(String channelId, Boolean sync) {
        if (channelRepository.findById(channelId).isEmpty()) {
            throw new RequestException("Channel doesn't exist", HttpStatus.NOT_FOUND);
        }
        if (sync) {
            synchronizeDatabase(channelId);
            return getAllSavedMembers(channelId);
        } else {
            return getAllSavedMembers(channelId);
        }
    }

    private List<Object> getAllSavedMembers(String channelId) {
        List<Object> memberResponses = new ArrayList<>();
        for (Member member : memberRepository.findAllByChannelChannelId(channelId)) {
            MemberResponse memberResponse =
                    new MemberResponse(
                            member.getMemberId(),
                            member.getChannel().getChannelId(),
                            member.getTelegramMember().getTelegramMemberId(),
                            member.getTelegramMember().getUsername(),
                            member.getTelegramMember().getFirstName(),
                            member.getTelegramMember().getLastName(),
                            member.getMemberStatus() == null ? "" : member.getMemberStatus().toString(),
                            member.getBillingFrequency() == null ? "" : member.getBillingFrequency().toString(),
                            member.getBillingPeriod(),
                            member.getStartDate(),
                            member.getEndDate());

            if (isColumnCreditAvailable(channelId)) {
                List<Value> values = valueRepository.findAllByMemberMemberId(member.getMemberId());
                memberResponses.add(new CustomColumnMemberResponse(
                        memberResponse,
                        values.stream().map(
                                value -> new ValueResponse(
                                        value.getId(),
                                        value.getValue(),
                                        value.getAttribute().getName(),
                                        value.getAttribute().getId())).toList()
                ));

            } else {
                memberResponses.add(memberResponse);
            }
        }
        return memberResponses;
    }

    private void synchronizeDatabase(String channelId) {
        List<TelegramMember> telegramMembers = getAllTelegramMembers(channelId, 50_000L);
        Channel channel = channelRepository.findById(channelId).get();
        for (TelegramMember telegramMember : telegramMembers) {
            if (memberRepository.findByTelegramMemberTelegramMemberId(telegramMember.getTelegramMemberId()).isEmpty()
                    && telegramMemberRepository.findById(telegramMember.getTelegramMemberId()).isEmpty()) {
                telegramMemberRepository.save(telegramMember);
                memberRepository.save(
                        Member.builder()
                                .channel(channel)
                                .telegramMember(telegramMember)
                                .memberStatus(MemberStatus.ACTIVE)
                                .build()
                );
            }
        }
    }

    private List<TelegramMember> getAllTelegramMembers(String channelId, Long limit) {
        return telegDashPyApiController.getAllMembers(channelId, limit).stream().map(
                basicMember -> TelegramMember
                        .builder()
                        .telegramMemberId(basicMember.telegramId())
                        .username(basicMember.username())
                        .firstName(basicMember.firstName())
                        .lastName(basicMember.lastName())
                        .build()
        ).toList();
    }

    private boolean isColumnCreditAvailable(String channelId) {
        return (channelRepository.findById(channelId).get().getAttributes().size() + 1) > 0;
    }
}
