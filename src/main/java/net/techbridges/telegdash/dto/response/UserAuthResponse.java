package net.techbridges.telegdash.dto.response;

public record UserAuthResponse(
        String username,
        String token,
        String refresh_token
){
}
