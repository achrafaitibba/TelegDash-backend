package net.techbridges.telegdash.dto.request;

import java.util.List;

public record ChannelCreateRequest(
        String channelOwnerMail,
        String channelId,
        String name,
        String groupType,
        List<String> niches,
        String description
) {
}
