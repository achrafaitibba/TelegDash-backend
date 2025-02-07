package net.techbridges.telegdash.dto.request;


import java.time.LocalDate;
import java.util.List;

public record MemberUpdateRequest(
        Long memberId,
        String billingFrequency,
        Integer billingPeriod,
        LocalDate startDate,
        List<ValueUpdateRequest> values
) {
}
