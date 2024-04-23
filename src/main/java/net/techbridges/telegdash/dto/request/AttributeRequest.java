package net.techbridges.telegdash.dto.request;

import net.techbridges.telegdash.model.enums.ValueType;

public record AttributeRequest(
        String name,
        ValueType valueType
) {
}
