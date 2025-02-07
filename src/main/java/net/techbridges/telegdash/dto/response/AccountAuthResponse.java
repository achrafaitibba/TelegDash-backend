package net.techbridges.telegdash.dto.response;

public record AccountAuthResponse(
        String email,
        String token,
        String refreshToken,
        String phoneNumber
) {
}
