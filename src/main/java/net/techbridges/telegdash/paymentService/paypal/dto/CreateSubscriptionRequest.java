package net.techbridges.telegdash.paymentService.paypal.dto;

public record CreateSubscriptionRequest(
        String planId,
        String startTime,
        String returnUrl,
        String cancelUrl
) {

}