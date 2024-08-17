package net.techbridges.telegdash.controller;

import lombok.RequiredArgsConstructor;
import net.techbridges.telegdash.service.AccountService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1.0/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final AccountService accountService;

    @PostMapping("/cancel")
    public String cancelSubscription(@RequestBody String raison) throws Exception {
        return accountService.cancelSubscription(raison);
    }
}
