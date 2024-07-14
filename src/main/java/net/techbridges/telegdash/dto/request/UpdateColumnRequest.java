package net.techbridges.telegdash.dto.request;

public record UpdateColumnRequest(
        Long attributeId,
        AttributeRequest attribute
) {
}
