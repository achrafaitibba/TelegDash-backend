package net.techbridges.telegdash.dto.request;

public record ValueUpdateRequest(
        Long attributeId,
        Long valueId,
        String value
) {
}
