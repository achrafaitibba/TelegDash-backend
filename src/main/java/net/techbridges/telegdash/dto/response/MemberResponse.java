package net.techbridges.telegdash.dto.response;


import java.util.Date;

public record MemberResponse(
        Long memberId,
        String channelId,
        String telegramMemberId,
        String username,
        String firstName,
        String lastName,
        String memberStatus,
        String billingFrequency,
        Integer billingPeriod,
        Date startDate,
        Date endDate) {
}
