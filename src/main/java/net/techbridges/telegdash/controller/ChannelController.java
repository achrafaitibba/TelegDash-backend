package net.techbridges.telegdash.controller;

import lombok.AllArgsConstructor;
import net.techbridges.telegdash.dto.request.AddColumnChannel;
import net.techbridges.telegdash.dto.request.ChannelCreateRequest;
import net.techbridges.telegdash.dto.request.UpdateColumnRequest;
import net.techbridges.telegdash.dto.response.ChannelResponse;
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
    public ResponseEntity<Niche[]> listNiches() {
        return ResponseEntity.ok().body(Niche.values());
    }

    @GetMapping("/group-types")
    public ResponseEntity<GroupType[]> listGroupTypes() {
        return ResponseEntity.ok().body(GroupType.values());
    }

    @PostMapping()
    public ResponseEntity<ChannelResponse> createChannel(@RequestBody ChannelCreateRequest channel) {
        return ResponseEntity.ok().body(channelService.createChannel(channel));
    }

    @PutMapping()
    public ResponseEntity<ChannelResponse> addColumn(@RequestBody AddColumnChannel request) {
        return ResponseEntity.ok().body(channelService.addColumn(request));
    }

    @PostMapping("/customColumn/update/{attributeId}")
    public ResponseEntity<ChannelResponse> updateColumn(@PathVariable Long attributeId, @RequestBody UpdateColumnRequest request) {
        return ResponseEntity.ok().body(channelService.updateColumn(attributeId, request));
    }

}
