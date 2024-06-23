package net.techbridges.telegdash.dto.request;

public record AccountRegisterRequest(
        String email,
        String password,
        Long planId
) {
}
