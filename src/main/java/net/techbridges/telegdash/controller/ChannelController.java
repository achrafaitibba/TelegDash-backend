package net.techbridges.telegdash.controller;

import lombok.AllArgsConstructor;
import net.techbridges.telegdash.service.ChannelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1.0/channels")
@AllArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    @GetMapping("/status/{channelId}")
    public ResponseEntity<Integer> checkAdminStatus(@PathVariable String channelId) {
        return ResponseEntity.ok().body(channelService.checkAdminStatus(channelId));
    }
}
