package net.techbridges.telegdash.service;

import lombok.RequiredArgsConstructor;
import net.techbridges.telegdash.dto.request.ChannelCreateRequest;
import net.techbridges.telegdash.dto.response.ChannelCreateResponse;
import net.techbridges.telegdash.repository.ChannelRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChannelService {
    private final ChannelRepository channelRepository;

    public ChannelCreateResponse createChannel(ChannelCreateRequest request) {

        return null;
    }
}
