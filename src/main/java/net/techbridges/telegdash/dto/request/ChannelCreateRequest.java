package net.techbridges.telegdash.dto.request;

import lombok.Builder;
import net.techbridges.telegdash.model.enums.Niche;

import java.util.List;

@Builder
public record ChannelCreateRequest(
        String channelId,
        String name,
        List<Niche> niches,
        String description,
        Long memberCount,
        String accountEmail,
        List<AttributeRequest> customAttributes
) {
}
