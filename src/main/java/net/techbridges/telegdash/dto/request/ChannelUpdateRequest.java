package net.techbridges.telegdash.dto.request;

import java.util.List;

public record ChannelUpdateRequest(
        String name,
        List<String> niches,
        String description,
        boolean autoKick,
        int autoKickAfterDays
        ) {
}
