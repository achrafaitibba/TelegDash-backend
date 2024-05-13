package net.techbridges.telegdash.dto.request;

public record AccountRegisterRequest(
        String username,
        String password,
        Long planId
) {
}
