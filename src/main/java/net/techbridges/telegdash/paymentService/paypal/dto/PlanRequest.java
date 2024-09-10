package net.techbridges.telegdash.paymentService.paypal.dto;

public record PlanRequest(
        //documentation
        //https://developer.paypal.com/docs/api/subscriptions/v1/#plans_create

        String productId,
        String planName,
        //int sequence,
        String intervalUnit,
        int  intervalCount,
        String planType,
        double price
) {
}
