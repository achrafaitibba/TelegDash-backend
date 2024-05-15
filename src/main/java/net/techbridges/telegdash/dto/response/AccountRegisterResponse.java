package net.techbridges.telegdash.dto.response;

public record AccountRegisterResponse(
        String username,
        String freeTrialEndDate,
        Long planId,
        String accountType,
        String token,
        String refresh_token
){
}
