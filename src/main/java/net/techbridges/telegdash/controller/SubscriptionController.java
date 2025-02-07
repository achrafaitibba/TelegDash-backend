package net.techbridges.telegdash.controller;

import lombok.RequiredArgsConstructor;
import net.techbridges.telegdash.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final AccountService accountService;

    @PostMapping("/cancel")
    public String cancelSubscription(@RequestBody String raison) throws Exception {
        return accountService.cancelSubscription(raison);
    }

    @GetMapping("/upgradeStatus")
    public ResponseEntity<List<Integer>> upgradeStatus() {
        return ResponseEntity.ok().body(accountService.upgradeStatus());
    }

    @GetMapping("/upgrade/{planId}")
    public ResponseEntity<String> upgrade(@PathVariable Long planId) throws Exception{
        return ResponseEntity.ok().body(accountService.upgrade(planId));
    }

    @GetMapping("/status")
    public ResponseEntity<Integer> status() {
        return ResponseEntity.ok().body(accountService.subscriptionStatus());
    }

    @GetMapping("/paymentStatus")
    public ResponseEntity<String> paymentStatus() throws Exception{
        return ResponseEntity.ok().body(accountService.paymentStatus());
    }

    @GetMapping("/approve")
    public ResponseEntity<String> approve() throws Exception{
        return ResponseEntity.ok().body(accountService.approveSubscription());
    }
}
