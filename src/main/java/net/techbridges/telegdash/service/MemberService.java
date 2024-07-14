package net.techbridges.telegdash.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import net.techbridges.telegdash.annotation.SubscriptionChecker;
import net.techbridges.telegdash.configuration.token.JwtService;
import net.techbridges.telegdash.dto.request.MemberUpdateRequest;
import net.techbridges.telegdash.dto.request.ValueUpdateRequest;
import net.techbridges.telegdash.dto.response.CustomColumnMemberResponse;
import net.techbridges.telegdash.dto.response.MemberResponse;
import net.techbridges.telegdash.dto.response.ValueResponse;
import net.techbridges.telegdash.exception.RequestException;
import net.techbridges.telegdash.mapper.MemberMapper;
import net.techbridges.telegdash.model.*;
import net.techbridges.telegdash.model.enums.BillingFrequency;
import net.techbridges.telegdash.model.enums.MemberStatus;
import net.techbridges.telegdash.repository.*;
import net.techbridges.telegdash.telegdashTelethonClientGateway.controller.TelegDashPyApiController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MemberService {
    private final TelegramMemberRepository telegramMemberRepository;
    private final TelegDashPyApiController telegDashPyApiController;
    private final MemberRepository memberRepository;
    private final ValueRepository valueRepository;
    private final ChannelRepository channelRepository;
    private final PlanRepository planRepository;
    private final HttpServletRequest headers;
    private final JwtService jwtService;
    private final AccountRepository accountRepository;
    private final AttributeRepository attributeRepository;
    private final MemberMapper memberMapper;

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

            if (isColumnCreditAvailable()){
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



    @SubscriptionChecker
    public Object updateMember(MemberUpdateRequest member) {
                Optional<Member> toUpdate = memberRepository.findById(member.memberId());
        if(toUpdate.isEmpty()){
            throw new RequestException("Member doesn't exist", HttpStatus.NOT_FOUND);
        }
        toUpdate.get().setBillingFrequency(BillingFrequency.valueOf(member.billingFrequency()));
        toUpdate.get().setBillingPeriod(member.billingPeriod());
        toUpdate.get().setStartDate(member.startDate());
        toUpdate.get().setEndDate(member.endDate());
        if(isColumnCreditAvailable()){
            List<Value> values = valueRepository.findAllByMemberMemberId(member.memberId());
            if(!values.isEmpty()){
                List<ValueUpdateRequest> valueUpdateRequests = member.values();
                for (ValueUpdateRequest value : valueUpdateRequests) {
                    if(value.valueId() != null){
                        Optional<Value> valueToUpdate = valueRepository.findById(value.valueId());
                        valueToUpdate.get().setValue(value.value());
                        valueRepository.save(valueToUpdate.get());
                    }else {
                        Value toSave = new Value();
                        toSave.setValue(value.value());
                        toSave.setMember(toUpdate.get());
                        toSave.setAttribute(attributeRepository.findById(value.attributeId()).get());
                        valueRepository.save(toSave);

                    }
                }
            }else{
                List<ValueUpdateRequest> valueUpdateRequests = member.values();
                for (ValueUpdateRequest value : valueUpdateRequests ) {
                    Value newValue = new Value();
                    newValue.setValue(value.value());
                    newValue.setMember(toUpdate.get());
                    newValue.setAttribute(attributeRepository.findById(value.attributeId()).get());
                    valueRepository.save(newValue);
                }
            }
        }
        memberRepository.save(toUpdate.get());
        List<Value> values = valueRepository.findAllByMemberMemberId(member.memberId());
        if(!values.isEmpty()){
            return memberMapper.toCustomColumnMemberResponse(toUpdate.get());
        }else {
            return memberMapper.toResponse(toUpdate.get());
        }
    }

    private boolean isColumnCreditAvailable() {
        String token = headers.getHeader("Authorization").substring(7);
        String accountOwner = jwtService.extractUsername(token);
        return planRepository.findById(accountRepository.findByEmail(accountOwner).get().getPlan().getPlanId()).get().getCustomColumns() > 0;
    }
}
