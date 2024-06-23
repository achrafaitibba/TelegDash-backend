package net.techbridges.telegdash.dto.response;

public record AccountRegisterResponse(
        String username,
        Long planId,
        String subscriptionUrl,
        String token,
        String refresh_token
){
}
