package net.techbridges.telegdash.dto.request;

public record AddColumnChannel(
        String channelOwnerMail,
        String channelId,
        AttributeRequest attribute
) {
}
