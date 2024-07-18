package net.techbridges.telegdash.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import net.techbridges.telegdash.annotation.SubscriptionChecker;
import net.techbridges.telegdash.dto.request.AddColumnChannel;
import net.techbridges.telegdash.dto.request.ChannelCreateRequest;
import net.techbridges.telegdash.dto.request.UpdateColumnRequest;
import net.techbridges.telegdash.dto.response.ChannelResponse;
import net.techbridges.telegdash.exception.RequestException;
import net.techbridges.telegdash.model.*;
import net.techbridges.telegdash.model.enums.GroupType;
import net.techbridges.telegdash.model.enums.Niche;
import net.techbridges.telegdash.repository.AccountRepository;
import net.techbridges.telegdash.repository.AttributeRepository;
import net.techbridges.telegdash.repository.ChannelRepository;
import net.techbridges.telegdash.repository.ValueRepository;
import net.techbridges.telegdash.telegdashTelethonClientGateway.controller.TelegDashPyApiController;
import net.techbridges.telegdash.utils.InputChecker;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ChannelService {
    private final TelegDashPyApiController telegDashPyApiController;
    private final ChannelRepository channelRepository;
    private final AccountRepository accountRepository;
    private final AttributeRepository attributeRepository;
    private final ValueRepository valueRepository;
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
        if(isChannelMemberCreditAvailable(channelOwner.get(), newChannelMembersCount)){
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
                            .autoKick(channel.autoKick())
                            .autoKickAfterDays(channel.autoKickAfterDays())
                            .build()
            );
        }
        return new ChannelResponse(
                savedChannel.getChannelId(),
                savedChannel.getName(),
                savedChannel.getNiches(),
                savedChannel.getDescription(),
                savedChannel.getMembersCount(),
                savedChannel.getAutoKick(),
                savedChannel.getAutoKickAfterDays()
        );
    }

    private long getChannelCountByAccount(String email){
        return channelRepository.countByChannelAdmin(accountRepository.findByEmail(email).get());
    }

    private Integer getMembersCountByChannel(String channelId){
        return telegDashPyApiController.getMembersCount(channelId);
    }


    private boolean isChannelMemberCreditAvailable(Account account, long newChannelMembersCount){
        Plan chosenPlan = account.getPlan();
        long channelsCount = getChannelCountByAccount(account.getUsername());
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


    @SubscriptionChecker
    @Transactional
    public ChannelResponse addColumn(AddColumnChannel request) {
        Channel toUpdate = channelRepository.findById(request.channelId()).get();
        if(isColumnCreditAvailable(accountRepository.findByEmail(request.channelOwnerMail()).get(),request.channelId())){
            Attribute newAttribute = attributeRepository.save( Attribute
                    .builder()
                    .name(request.attribute().name())
                    .valueType(request.attribute().valueType())
                    .channel(toUpdate)
                    .build());
            newAttribute.setChannel(toUpdate);
            attributeRepository.save(newAttribute);
            channelRepository.save(toUpdate);
        }
        return new ChannelResponse(
                toUpdate.getChannelId(),
                toUpdate.getName(),
                toUpdate.getNiches(),
                toUpdate.getDescription(),
                toUpdate.getMembersCount(),
                toUpdate.getAutoKick(),
                toUpdate.getAutoKickAfterDays()
        );
    }

    private boolean isColumnCreditAvailable(Account account, String channelId){
        int chosenPlanAttributes = account.getPlan().getCustomColumns();
        int channelAttributesCount = attributeRepository.findAllByChannelChannelId(channelId).size() + 1;
        if(chosenPlanAttributes < channelAttributesCount){
            throw new RequestException("You have reached the limit of custom columns, upgrade your plan", HttpStatus.FORBIDDEN);
        }
        return true;
    }

    @SubscriptionChecker
    public ChannelResponse updateColumn(Long attributeId, UpdateColumnRequest request) {
        Optional<Attribute> attribute = attributeRepository.findById(attributeId);
        if(attribute.isEmpty()){
            throw new RequestException("The attribute id provided doesn't exist", HttpStatus.FORBIDDEN);
        }
        attribute.get().setName(request.attribute().name());
        attribute.get().setValueType(request.attribute().valueType());
        attributeRepository.save(attribute.get());
        Channel channel = channelRepository.findById(attribute.get().getChannel().getChannelId()).get();
        return new ChannelResponse(
                channel.getChannelId(),
                channel.getName(),
                channel.getNiches(),
                channel.getDescription(),
                channel.getMembersCount(),
                channel.getAutoKick(),
                channel.getAutoKickAfterDays()
        );    }

    @SubscriptionChecker
    public ChannelResponse deleteColumn(Long attributeId) {
        Optional<Attribute> attribute = attributeRepository.findById(attributeId);
        if(attribute.isEmpty()){
            throw new RequestException("The attribute id provided doesn't exist", HttpStatus.FORBIDDEN);
        }
        List<Value> values = valueRepository.findAllByAttribute(attribute.get());
        for(Value value : values){
            valueRepository.delete(value);
        }
        attributeRepository.delete(attribute.get());
        Channel channel = channelRepository.findById(attribute.get().getChannel().getChannelId()).get();
        return new ChannelResponse(
                channel.getChannelId(),
                channel.getName(),
                channel.getNiches(),
                channel.getDescription(),
                channel.getMembersCount(),
                channel.getAutoKick(),
                channel.getAutoKickAfterDays()
        );
    }

    public List<ChannelResponse> getAllByType(String email, String groupType) {
        List<Channel> all = channelRepository.findAllByChannelAdmin(accountRepository.findByEmail(email).get());
        return all.stream().filter(channel -> channel.getGroupType().toString().endsWith(groupType)).toList().stream().map(
                channel -> new ChannelResponse(
                        channel.getChannelId(),
                        channel.getName(),
                        channel.getNiches(),
                        channel.getDescription(),
                        channel.getMembersCount(),
                        channel.getAutoKick(),
                        channel.getAutoKickAfterDays()
                )
        ).toList();
    }
}
