package net.techbridges.telegdash.dto.response;

public record ValueResponse(
        Long id,
        String value,
        String columnName,
        Long columnId
) {
}
