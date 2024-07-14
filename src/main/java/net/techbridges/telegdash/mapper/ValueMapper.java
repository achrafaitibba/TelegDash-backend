package net.techbridges.telegdash.mapper;

import lombok.AllArgsConstructor;
import net.techbridges.telegdash.dto.response.ValueResponse;
import net.techbridges.telegdash.model.Value;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class ValueMapper {

    public ValueResponse toResponse(Value value) {
        return new ValueResponse(
                value.getId(),
                value.getValue(),
                value.getAttribute().getName(),
                value.getAttribute().getId()
        );
    }
}
