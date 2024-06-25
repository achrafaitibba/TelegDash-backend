package net.techbridges.telegdash.paymentService.paypal.controller;

import lombok.RequiredArgsConstructor;
import net.techbridges.telegdash.paymentService.paypal.dto.CreateSubscriptionRequest;
import net.techbridges.telegdash.paymentService.paypal.dto.ReviseSubscriptionRequest;
import net.techbridges.telegdash.paymentService.paypal.model.Subscription;
import net.techbridges.telegdash.paymentService.paypal.service.SubscriptionService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentController {

    private final SubscriptionService subscriptionService;


    public Subscription createSubscription(CreateSubscriptionRequest request) throws Exception {
        return subscriptionService.createSubscription(request);
    }

    public Subscription getSubscriptionDetails(String subscriptionId) throws Exception {
        return subscriptionService.getSubscription(subscriptionId);
    }

    public String approveSubscription(Subscription subscription) throws Exception {
        return subscriptionService.approveSubscription(subscription);
    }

    public Object reviseSubscription(ReviseSubscriptionRequest request) throws Exception {
        return subscriptionService.reviseSubscription(request);
    }
}