package net.techbridges.telegdash.dto.response;


import java.time.LocalDate;

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
        LocalDate startDate,
        LocalDate endDate) {
}
