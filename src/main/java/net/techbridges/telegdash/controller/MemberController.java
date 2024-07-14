package net.techbridges.telegdash.controller;

import lombok.AllArgsConstructor;
import net.techbridges.telegdash.dto.request.MemberUpdateRequest;
import net.techbridges.telegdash.dto.response.MemberResponse;
import net.techbridges.telegdash.model.enums.BillingFrequency;
import net.techbridges.telegdash.model.enums.MemberStatus;
import net.techbridges.telegdash.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/members")
@AllArgsConstructor
public class MemberController {
    private final MemberService memberService;


    @GetMapping("/memberStatus")
    public ResponseEntity<MemberStatus[]> listMemberStatus() {
        return ResponseEntity.ok().body(MemberStatus.values());
    }
    @GetMapping("/billingFrequencies")
    public ResponseEntity<BillingFrequency[]> listBillingFrequencies() {
        return ResponseEntity.ok().body(BillingFrequency.values());
    }

    @GetMapping("/{channelId}/{sync}")
    public ResponseEntity<List<Object>> getAllMembers(@PathVariable("channelId") String channelId, @PathVariable("sync") Boolean sync) {
        return ResponseEntity.ok().body(memberService.getAllMembers(channelId, sync));
    }

    @PostMapping("/update")
    public ResponseEntity<Object> updateMember(@RequestBody MemberUpdateRequest member) {
        return ResponseEntity.ok().body(memberService.updateMember(member));
    }


    @PostMapping("/delete/{channelId}")
    public ResponseEntity<List<MemberResponse>> kickMembers(@PathVariable String channelId, @RequestBody List<String> memberIds) {
        return ResponseEntity.ok().body(memberService.kickMembers(channelId, memberIds));
    }
}
