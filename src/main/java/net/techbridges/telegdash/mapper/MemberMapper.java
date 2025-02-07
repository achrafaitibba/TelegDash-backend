package net.techbridges.telegdash.mapper;

import lombok.AllArgsConstructor;
import net.techbridges.telegdash.dto.response.CustomColumnMemberResponse;
import net.techbridges.telegdash.dto.response.MemberResponse;
import net.techbridges.telegdash.model.Member;
import net.techbridges.telegdash.model.Value;
import net.techbridges.telegdash.repository.ValueRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class MemberMapper {
    private final ValueRepository valueRepository;
    private final ValueMapper valueMapper;

    public MemberResponse toResponse(Member member) {
        return new MemberResponse(
                member.getMemberId(),
                member.getChannel().getChannelId(),
                member.getTelegramMember().getTelegramMemberId(),
                member.getTelegramMember().getUsername(),
                member.getTelegramMember().getFirstName(),
                member.getTelegramMember().getLastName(),
                member.getMemberStatus().toString(),
                member.getBillingFrequency() == null ? "" : member.getBillingFrequency().toString(),
                member.getBillingPeriod(),
                member.getStartDate(),
                member.getEndDate()
        );
    }

    //todo, avoid using repos, only the mapping allowed on this class mapper, data flow
    public CustomColumnMemberResponse toCustomColumnMemberResponse(Member member) {
        List<Value> valueList = valueRepository.findAllByMemberMemberId(member.getMemberId());
        return new CustomColumnMemberResponse(
                toResponse(member),
                valueList.stream().map(
                        valueMapper::toResponse
                ).toList()
        );
    }

    public Member toMember(MemberResponse memberResponse) {
        return null;
    }
}
