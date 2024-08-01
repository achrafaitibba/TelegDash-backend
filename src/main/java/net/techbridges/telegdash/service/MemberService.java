package net.techbridges.telegdash.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.techbridges.telegdash.annotation.SubscriptionChecker;
import net.techbridges.telegdash.configuration.token.JwtService;
import net.techbridges.telegdash.dto.request.MemberUpdateRequest;
import net.techbridges.telegdash.dto.request.ValueUpdateRequest;
import net.techbridges.telegdash.dto.response.CustomColumnMemberResponse;
import net.techbridges.telegdash.exception.RequestException;
import net.techbridges.telegdash.mapper.MemberMapper;
import net.techbridges.telegdash.model.*;
import net.techbridges.telegdash.model.enums.BillingFrequency;
import net.techbridges.telegdash.model.enums.MemberStatus;
import net.techbridges.telegdash.repository.*;
import net.techbridges.telegdash.telegdashTelethonClientGateway.controller.TelegDashPyApiController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public List<CustomColumnMemberResponse> getAllMembers(String channelId, Boolean sync, Integer page, Integer size) {
        Optional<Channel> channel = channelRepository.findById(channelId);
        if (channel.isEmpty()) {
            throw new RequestException("Channel doesn't exist", HttpStatus.NOT_FOUND);
        }

        if (sync && isSyncAuthorized(channel.get())) {
            synchronizeDatabase(channelId);
            return getAllSavedMembers(channelId, page, size);
        } else if (sync && !isSyncAuthorized(channel.get())) {
            throw new RequestException("You're not authorized to synchronize more than 1 time every 24H", HttpStatus.FORBIDDEN);
        } else {
            return getAllSavedMembers(channelId, page, size);
        }
    }

    private boolean isSyncAuthorized(Channel channel){
        LocalDateTime lastSync = channel.getLastSync();
        if(lastSync == null) {
            log.info("Is authorized to sync: {}", true);
            return true;
        }else {
            boolean result = lastSync.plusHours(24).isBefore(LocalDateTime.now());
            log.info("Last sync time is {}", lastSync);
            log.info("Is authorized to sync: {}", result);
            return result;
        }
    }

    private List<CustomColumnMemberResponse> getAllSavedMembers(String channelId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        List<CustomColumnMemberResponse> members = new ArrayList<>();
        Page<Member> memberPage = memberRepository.findAllByChannelChannelId(channelId, pageable);
        for (Member member : memberPage) {
            CustomColumnMemberResponse memberResponse =
                    memberMapper.toCustomColumnMemberResponse(member);
            if (isColumnCreditAvailable()) {
                members.add(memberMapper.toCustomColumnMemberResponse(member));
            } else {
                members.add(memberResponse);
            }
        }
        return members;
    }

    private void synchronizeDatabase(String channelId) {
        if(isChannelMembersCreditAvailable(channelId, telegDashPyApiController.getMembersCount(channelId))){
            setStatusExpired(channelId);
            List<Member> members = memberRepository.findAllByChannelChannelId(channelId);
            for (Member member : members) {
                member.setMemberStatus(MemberStatus.KICKED);
                memberRepository.save(member);
            }
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
            channel.setLastSync(LocalDateTime.now());
            channel.setMembersCount(Long.valueOf(telegDashPyApiController.getMembersCount(channelId)));
            channelRepository.save(channel);
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
        if(toUpdate.get().getBillingPeriod() != null ){
            toUpdate.get().setEndDate(calculateEndDate(toUpdate.get()));
        }
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
    public String kickMembers(String channelId, List<String> memberIds) {
        if (!memberIds.isEmpty()) {
            telegDashPyApiController.kickMembers(channelId, memberIds);
            for (String id : memberIds) {
                Optional<Member> toKick = memberRepository.findByTelegramMemberTelegramMemberIdAndChannelChannelId(id, channelId);
                toKick.get().setMemberStatus(MemberStatus.KICKED);
                memberRepository.save(toKick.get());
            }
        }
        return "Successfully kicked";
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


    //every 2 weeks
    @Scheduled(fixedRateString = "1208000000")
    public void scheduleAutoKicking() {
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

    //todo, test subscription not paid yet
    @SubscriptionChecker
    private boolean isAutoKickAuthorized(Channel channel) {
        String accountOwner = channel.getChannelAdmin().getUsername();
        Plan plan = planRepository.findById(accountRepository.findByEmail(accountOwner).get().getPlan().getPlanId()).get();
        return plan.getAutoKickingMember();
    }

    //1 per week
    @Scheduled(fixedRateString = "604800000")
    public void scheduleUsersReminder() {
        log.info("scheduled Users Reminder started at {}", LocalDateTime.now());
        for(Channel channel : channelsWithReminderEnabled()) {
            telegDashPyApiController.sendMessageToUsers(
                    channel.getChannelAdmin().getPhoneNumber(),
                    expiredMembersTelegramIDs(channel),
                    "Your subscription for :"+channel.getName()+" is expiring soon, to avoid being removed of the channel, renew your subscription."
                    );
        }
    }

    private Long[] expiredMembersTelegramIDs(Channel channel) {
        List<Member> membersByChannel = memberRepository.findAllByChannelChannelId(channel.getChannelId());
        List<Long> expiredMembers = new ArrayList<>();
        for (Member member : membersByChannel) {
            log.info("Checking if member " + member.getMemberId() + " is expired");
            if (isExpired(member.getMemberId(), channel.getAutoKickAfterDays()) && member.getMemberStatus() != MemberStatus.KICKED) {
                expiredMembers.add(Long.valueOf(member.getTelegramMember().getTelegramMemberId()));
                log.info("Member will be kicked {}::", member.getTelegramMember().getTelegramMemberId());
            } else {
                log.info("Won't be kicked " + member.getMemberId());
            }
        }
        return expiredMembers.toArray(new Long[0]);
    }

    private List<Channel> channelsWithReminderEnabled() {
        List<Account> accounts = accountRepository.findAll().stream().filter(
                this::isReminderEnabled
        ).toList();
        List<Channel> channelsWithReminder = new ArrayList<>();
        for(Account account : accounts) {
            channelsWithReminder.addAll(channelRepository.findAllByChannelAdmin(account));
        }
        return channelsWithReminder;
    }

    @SubscriptionChecker
    private boolean isReminderEnabled(Account account){
        Plan plan = planRepository.findById(accountRepository.findByEmail(account.getEmail()).get().getPlan().getPlanId()).get();
        return plan.getReminder();
    }

    private boolean isChannelMembersCreditAvailable(String channelId, long channelMembersCount){
        Optional<Channel> toCheck = channelRepository.findById(channelId);
        Account account = toCheck.get().getChannelAdmin();
        Plan chosenPlan = account.getPlan();
        List<Channel> channelsByAccount = channelRepository.findAllByChannelAdmin(account);
        long membersCountOfChannels = channelMembersCount;
        for(Channel channel : channelsByAccount){
            membersCountOfChannels += channel.getMembersCount();
        }
        System.out.println("membersCountOfChannels"  + membersCountOfChannels);
        membersCountOfChannels -= toCheck.get().getMembersCount();
        System.out.println("membersCountOfChannels reduced" + membersCountOfChannels);
        if(membersCountOfChannels > chosenPlan.getMembers()){
            throw new RequestException("You have reached the limit of members, upgrade your plan", HttpStatus.FORBIDDEN);
        }
        return true;
    }

    private LocalDate calculateEndDate(Member member) {
        LocalDate startDate = member.getStartDate();
        BillingFrequency billingFrequency = member.getBillingFrequency();
        int billingPeriod = member.getBillingPeriod();
        LocalDate endDate = null;
        switch (billingFrequency) {
            case DAY:
                endDate = startDate.plusDays(billingPeriod);
                break;
            case WEEK:
                endDate = startDate.plusWeeks(billingPeriod);
                break;
            case MONTH:
                endDate = startDate.plusMonths(billingPeriod);
                break;
            case YEAR:
                endDate = startDate.plusYears(billingPeriod);
                break;
        }
        return endDate;
    }
}