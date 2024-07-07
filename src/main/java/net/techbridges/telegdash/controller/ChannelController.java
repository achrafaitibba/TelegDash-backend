package net.techbridges.telegdash.controller;

import lombok.AllArgsConstructor;
import net.techbridges.telegdash.model.enums.GroupType;
import net.techbridges.telegdash.model.enums.Niche;
import net.techbridges.telegdash.service.ChannelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1.0/channels")
@AllArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    @GetMapping("/status/{groupType}/{channelId}")
    public ResponseEntity<Integer> checkAdminStatus(@PathVariable GroupType groupType, @PathVariable String channelId) {
        return ResponseEntity.ok().body(channelService.checkAdminStatus(groupType, channelId));
    }

    @GetMapping("/niches")
    public ResponseEntity<Niche[]> listChannels() {
        return ResponseEntity.ok().body(Niche.values());
    }

    @GetMapping("/group-types")
    public ResponseEntity<GroupType[]> listGroupTypes() {
        return ResponseEntity.ok().body(GroupType.values());
    }




}
