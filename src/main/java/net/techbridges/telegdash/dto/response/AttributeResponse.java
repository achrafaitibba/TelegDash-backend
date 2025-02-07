package net.techbridges.telegdash.dto.response;

public record AttributeResponse(
        Long attributeId,
        String attributeName,
        String valueType
) {
}
