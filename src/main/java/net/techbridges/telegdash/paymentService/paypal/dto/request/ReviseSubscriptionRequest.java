package net.techbridges.telegdash.paymentService.paypal.dto.request;


public record ReviseSubscriptionRequest(
        String subscriptionId,
        String planId
) {
}
