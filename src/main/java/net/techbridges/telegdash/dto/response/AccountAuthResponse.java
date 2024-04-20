package net.techbridges.telegdash.dto.response;

public record AccountAuthResponse(
        String username,
        String token,
        String refresh_token
){
}
