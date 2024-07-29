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

    public String approveSubscription(Subscription subscription) throws Exception {
        return subscriptionService.approveSubscription(subscription);
    }

    public Object reviseSubscription(@RequestBody  ReviseSubscriptionRequest request) throws Exception {
        return subscriptionService.reviseSubscription(request);
    }

    //todo, restrict access
    public Product createProduct(@RequestBody String productName) throws Exception{
        return subscriptionService.createProduct(productName);
    }

    //todo, restrict access
    public PaypalPlan createPlan(@RequestBody PlanRequest request) throws Exception{
        return subscriptionService.createPlan(request);
    }
}