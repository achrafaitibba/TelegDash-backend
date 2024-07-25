package net.techbridges.telegdash.paymentService.paypal.dto;

public record PlanRequest(
        String productId,
        String planName,
        int sequence,
        String intervalUnit,
        int  intervalCount,
        String planType,
        double price
) {
}
