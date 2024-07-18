package net.techbridges.telegdash.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.techbridges.telegdash.annotation.SubscriptionChecker;
import net.techbridges.telegdash.configuration.token.JwtService;
import net.techbridges.telegdash.dto.request.ChannelCreateRequest;
import net.techbridges.telegdash.dto.request.MemberUpdateRequest;
import net.techbridges.telegdash.dto.request.ValueUpdateRequest;
import net.techbridges.telegdash.dto.response.MemberResponse;
import net.techbridges.telegdash.exception.RequestException;
import net.techbridges.telegdash.mapper.MemberMapper;
import net.techbridges.telegdash.model.*;
import net.techbridges.telegdash.model.enums.BillingFrequency;
import net.techbridges.telegdash.model.enums.MemberStatus;
import net.techbridges.telegdash.repository.*;
import net.techbridges.telegdash.telegdashTelethonClientGateway.controller.TelegDashPyApiController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
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
                    memberMapper.toResponse(member);
            if (isColumnCreditAvailable()) {
                memberResponses.add(memberMapper.toCustomColumnMemberResponse(member));

            } else {
                memberResponses.add(memberResponse);
            }
        }
        return memberResponses;
    }

    private void synchronizeDatabase(String channelId) {
        setStatusExpired(channelId);
        List<TelegramMember> telegramMembers = getAllTelegramMembers(channelId, 50_000L);
        Channel channel = channelRepository.findById(channelId).get();
        for (TelegramMember telegramMember : telegramMembers) {
            Optional<Member> currentMember = memberRepository.findByTelegramMemberTelegramMemberIdAndChannelChannelId(telegramMember.getTelegramMemberId(), channelId);
            if (currentMember.isEmpty()) {
                telegramMemberRepository.save(telegramMember);
                memberRepository.save(
                        Member.builder()
                                .channel(channel)
                                .telegramMember(telegramMember)
                                .memberStatus(MemberStatus.ACTIVE)
                                .build()
                );
            }else if (!currentMember.get().getMemberStatus().equals(MemberStatus.EXPIRED)){
                currentMember.get().setMemberStatus(MemberStatus.ACTIVE);
                memberRepository.save(currentMember.get());
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
        if (toUpdate.isEmpty()) {
            throw new RequestException("Member doesn't exist", HttpStatus.NOT_FOUND);
        }
        toUpdate.get().setBillingFrequency(BillingFrequency.valueOf(member.billingFrequency()));
        toUpdate.get().setBillingPeriod(member.billingPeriod());
        toUpdate.get().setStartDate(member.startDate());
        toUpdate.get().setEndDate(member.endDate());
        if (isColumnCreditAvailable()) {
            List<Value> values = valueRepository.findAllByMemberMemberId(member.memberId());
            if (!values.isEmpty()) {
                List<ValueUpdateRequest> valueUpdateRequests = member.values();
                for (ValueUpdateRequest value : valueUpdateRequests) {
                    Optional<Value> valueToUpdate = valueRepository.findById(value.valueId());
                    if (valueToUpdate.isPresent()) {
                        valueToUpdate.get().setValue(value.value());
                        valueRepository.save(valueToUpdate.get());
                    } else {
                        Value toSave = new Value();
                        toSave.setValue(value.value());
                        toSave.setMember(toUpdate.get());
                        toSave.setAttribute(attributeRepository.findById(value.attributeId()).get());
                        valueRepository.save(toSave);

                    }
                }
            } else {
                List<ValueUpdateRequest> valueUpdateRequests = member.values();
                for (ValueUpdateRequest value : valueUpdateRequests) {
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
        if (!values.isEmpty()) {
            return memberMapper.toCustomColumnMemberResponse(toUpdate.get());
        } else {
            return memberMapper.toResponse(toUpdate.get());
        }
    }

    private boolean isColumnCreditAvailable() {
        String token = headers.getHeader("Authorization").substring(7);
        String accountOwner = jwtService.extractUsername(token);
        return planRepository.findById(accountRepository.findByEmail(accountOwner).get().getPlan().getPlanId()).get().getCustomColumns() > 0;
    }

    @SubscriptionChecker
    public List<MemberResponse> kickMembers(String channelId, List<String> memberIds) {
        List<Member> members = new ArrayList<>();
        if (!memberIds.isEmpty()) {
            telegDashPyApiController.kickMembers(channelId, memberIds);
            for (String id : memberIds) {
                Optional<Member> toKick = memberRepository.findByTelegramMemberTelegramMemberIdAndChannelChannelId(id, channelId);
                toKick.get().setMemberStatus(MemberStatus.KICKED);
                memberRepository.save(toKick.get());
            }
            members = memberRepository.findAllByChannelChannelId(channelId);

        }
        return members.stream().map(
                memberMapper::toResponse
        ).toList();
    }

    private void setStatusExpired(String channelId) {
        List<Member> members = memberRepository.findAllByChannelChannelId(channelId);
        for (Member member : members) {
            LocalDate endDate = member.getEndDate();
            if(endDate != null){
                boolean isExpired = endDate.isBefore(LocalDate.now());
                if (isExpired) {
                    member.setMemberStatus(MemberStatus.EXPIRED);
                    memberRepository.save(member);
                }
            }

        }

    }


    @Scheduled(fixedRateString = "604800000")
    public void scheduleKicking() {
        log.info("Schedule autokicking expired subscriptions started at {}", LocalDate.now());
        for (Channel channel : channelWithAutoKickEnabled()) {
            log.info("Has enabled autoKicking {}", channel.getName() + "\n");
            autoKickMembers(channel.getChannelId());
        }
    }

    private List<Channel> channelWithAutoKickEnabled() {
        log.info("Check channels with autoKicking enabled");
        return channelRepository.findAll().stream().filter(
                channel -> channel.getAutoKick()
        ).toList();
    }

    public void autoKickMembers(String channelID) {
        Channel channel = channelRepository.findById(channelID).get();
        if (isAutoKickAuthorized(channel)) {
            log.info("Has autoKicking authorized {}", channel.getName() + "\n");
            List<Member> members = memberRepository.findAllByChannelChannelId(channelID);
            List<String> expiredMembers = new ArrayList<>();
            for (Member member : members) {
                log.info("Checking if member " + member.getMemberId() + " is expired");
                if (isExpired(member.getMemberId(), channel.getAutoKickAfterDays()) && member.getMemberStatus() != MemberStatus.KICKED) {
                    expiredMembers.add(member.getTelegramMember().getTelegramMemberId());
                    log.info("Member will be kicked {}::", member.getTelegramMember().getTelegramMemberId());
                }else{
                    log.info("Wont't be kicked " + member.getMemberId());
                }
            }
            kickMembers(channelID, expiredMembers);
        }
    }


    private boolean isExpired(Long memberID, Integer extendDays) {
        log.info("Checking if expired : {}", memberID + "\n");
        if (memberRepository.findById(memberID).get().getEndDate() == null) {
            log.info("Member endDate is null {}", memberRepository.findById(memberID).get().getTelegramMember().getTelegramMemberId());
            return false;
        } else{
            Member member = memberRepository.findById(memberID).get();
            LocalDate endDate = member.getEndDate();
            return endDate.plusDays(extendDays).isBefore(LocalDate.now());
        }
    }

    @SubscriptionChecker
    private boolean isAutoKickAuthorized(Channel channel) {
        String accountOwner = channel.getChannelAdmin().getUsername();
        Plan plan = planRepository.findById(accountRepository.findByEmail(accountOwner).get().getPlan().getPlanId()).get();
        return plan.getAutoKickingMember();
    }

}