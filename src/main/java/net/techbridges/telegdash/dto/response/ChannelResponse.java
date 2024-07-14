package net.techbridges.telegdash.dto.response;

import net.techbridges.telegdash.model.enums.Niche;

import java.util.List;

public record ChannelResponse(
        String channelId,
        String channelName,
        List<Niche> niches,
        String description,
        Long membersCount
) {
}
