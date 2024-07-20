package net.techbridges.telegdash.mapper;

import net.techbridges.telegdash.dto.response.AttributeResponse;
import net.techbridges.telegdash.model.Attribute;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AttributeMapper {

    public List<AttributeResponse> toAttributeResponse(List<Attribute> attributes) {

        return attributes.stream().map(
                attribute -> new AttributeResponse(
                        attribute.getId(),
                        attribute.getName(),
                        attribute.getValueType().toString()
                )
        ).toList();
    }
}
