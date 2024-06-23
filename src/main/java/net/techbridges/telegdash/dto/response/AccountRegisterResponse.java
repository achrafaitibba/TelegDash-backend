package net.techbridges.telegdash.dto.response;

public record AccountRegisterResponse(
        String email,
        Long planId,
        String subscriptionUrl,
        String token,
        String refresh_token
){
}
