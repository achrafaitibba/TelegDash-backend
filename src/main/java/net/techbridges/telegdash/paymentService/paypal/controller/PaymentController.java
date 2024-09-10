package net.techbridges.telegdash.paymentService.paypal.controller;

import lombok.RequiredArgsConstructor;
import net.techbridges.telegdash.paymentService.paypal.dto.CreateSubscriptionRequest;
import net.techbridges.telegdash.paymentService.paypal.dto.PlanRequest;
import net.techbridges.telegdash.paymentService.paypal.dto.ReviseSubscriptionRequest;
import net.techbridges.telegdash.paymentService.paypal.model.PaypalPlan;
import net.techbridges.telegdash.paymentService.paypal.model.Product;
import net.techbridges.telegdash.paymentService.paypal.model.Subscription;
import net.techbridges.telegdash.paymentService.paypal.service.SubscriptionService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Service
public class PaymentController {

    private final SubscriptionService subscriptionService;


    public Subscription createSubscription(CreateSubscriptionRequest request) throws Exception {
        return subscriptionService.createSubscription(request);
    }

    public Subscription getSubscriptionDetails(@PathVariable  String subscriptionId) throws Exception {
        return subscriptionService.getSubscription(subscriptionId);
    }

    public String approveSubscriptionUrl(String subsId) throws Exception {
        return subscriptionService.approveSubscription(subsId);
    }

    public Object reviseSubscription(ReviseSubscriptionRequest request) throws Exception {
        return subscriptionService.reviseSubscription(request);
    }

    public Object upgradeSubscription(ReviseSubscriptionRequest request) throws Exception {
        return subscriptionService.upgradeSubscription(request);
    }

    public Product createProduct(String productName) throws Exception{
        return subscriptionService.createProduct(productName);
    }

    public PaypalPlan createPlan( PlanRequest request) throws Exception{
        return subscriptionService.createPlan(request);
    }

    public int cancelSubscription(String subscriptionId, String raison) throws Exception {
        return subscriptionService.cancelSubscription(subscriptionId, raison);
    }

}