package net.techbridges.telegdash.paymentService.paypal.dto.request;

public record CreateSubscriptionRequest(
        String planId,
        String startTime,
        String returnUrl,
        String cancelUrl
) {

}