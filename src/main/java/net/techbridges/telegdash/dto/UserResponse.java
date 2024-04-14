package net.techbridges.telegdash.dto;

public record UserResponse (
        String username,
        String token,
        String refresh_token
){
}
