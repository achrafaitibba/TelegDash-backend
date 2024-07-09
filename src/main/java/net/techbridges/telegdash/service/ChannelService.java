package net.techbridges.telegdash.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import net.techbridges.telegdash.annotation.SubscriptionChecker;
import net.techbridges.telegdash.dto.request.ChannelCreateRequest;
import net.techbridges.telegdash.dto.response.ChannelResponse;
import net.techbridges.telegdash.exception.RequestException;
import net.techbridges.telegdash.model.Account;
import net.techbridges.telegdash.model.Channel;
import net.techbridges.telegdash.model.Plan;
import net.techbridges.telegdash.model.enums.GroupType;
import net.techbridges.telegdash.model.enums.Niche;
import net.techbridges.telegdash.repository.AccountRepository;
import net.techbridges.telegdash.repository.ChannelRepository;
import net.techbridges.telegdash.repository.MemberRepository;
import net.techbridges.telegdash.telegdashTelethonClientGateway.controller.TelegDashPyApiController;
import net.techbridges.telegdash.utils.InputChecker;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ChannelService {
    private final TelegDashPyApiController telegDashPyApiController;
    private final ChannelRepository channelRepository;
    private final AccountRepository accountRepository;
    private final MemberRepository memberRepository;

    @SubscriptionChecker
    public Integer checkAdminStatus(GroupType groupType, String channelId){
        try{
            return telegDashPyApiController.checkAdminStatus(InputChecker.channelUsernameBuilder(groupType, channelId));
        }catch (Exception e){
            throw new RequestException("Something is wrong with the provided ID, details : "  + e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @SubscriptionChecker
    @Transactional
    public ChannelResponse createChannel(ChannelCreateRequest channel){
        String channelID = InputChecker.channelUsernameBuilder(GroupType.valueOf(channel.groupType()), channel.channelId());
        long newChannelMembersCount = getMembersCountByChannel(channelID);
        Optional<Account> channelOwner = accountRepository.findByEmail(channel.channelOwnerMail());
        Channel savedChannel = new Channel();
        if(isPlanCreditAvailable(channelOwner.get(), newChannelMembersCount)){
            savedChannel = channelRepository.save(
                    Channel.builder()
                            .channelId(channelID)
                            .name(channel.name())
                            .groupType(GroupType.valueOf(channel.groupType()))
                            .niches(
                            channel.niches().stream().map(
                                    n -> Niche.valueOf(n)
                            ).toList()
                            )
                            .description(channel.description())
                            .membersCount(newChannelMembersCount)
                            .channelAdmin(channelOwner.get())
                            .build()
            );
        }
        return new ChannelResponse(
                savedChannel.getName(),
                savedChannel.getNiches(),
                savedChannel.getDescription(),
                savedChannel.getMembersCount()
        );
    }

    private long getChannelCountByAccount(String email){
        return channelRepository.countByChannelAdmin(accountRepository.findByEmail(email).get());
    }

    private Integer getMembersCountByChannel(String channelId){
        return telegDashPyApiController.getMembersCount(channelId);
    }

    /**
     * - created channels count
     * - all member's channels count
     */
    private boolean isPlanCreditAvailable(Account account, long newChannelMembersCount){
        Plan chosenPlan = account.getPlan();
        long channelsCount = getChannelCountByAccount(account.getUsername()) + 1;
        if(channelsCount > chosenPlan.getChannels()){
           throw new RequestException("You have reached the limit of channels, upgrade your plan", HttpStatus.FORBIDDEN);
        }
        List<Channel> channelsByAccount = channelRepository.findAllByChannelAdmin(account);
        long membersCountOfChannels = newChannelMembersCount;
        for(Channel channel : channelsByAccount){
            membersCountOfChannels += channel.getMembersCount();
        }
        if(membersCountOfChannels > chosenPlan.getMembers()){
            throw new RequestException("You have reached the limit of members, upgrade your plan", HttpStatus.FORBIDDEN);
        }
        return true;
    }
}
