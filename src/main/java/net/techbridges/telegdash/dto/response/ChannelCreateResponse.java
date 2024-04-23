package net.techbridges.telegdash.dto.response;

import lombok.Builder;
import net.techbridges.telegdash.dto.request.AttributeRequest;
import net.techbridges.telegdash.model.enums.Niche;

import java.util.List;

@Builder
public record ChannelCreateResponse(
        String channelId,
        String name,
        List<Niche> niches,
        String description,
        Long memberCount,
        List<AttributeRequest> customAttributes
) {
}
