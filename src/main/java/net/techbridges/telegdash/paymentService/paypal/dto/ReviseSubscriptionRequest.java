package net.techbridges.telegdash.paymentService.paypal.dto;


public record ReviseSubscriptionRequest(
        String subscriptionId,
        String planId
) {
}
