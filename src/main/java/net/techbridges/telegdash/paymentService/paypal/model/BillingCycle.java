package net.techbridges.telegdash.paymentService.paypal.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BillingCycle {
    @JsonProperty("pricing_scheme")
    private PricingScheme pricingScheme;
    private Frequency frequency;
    @JsonProperty("tenure_type")
    private String tenureType;
    private int sequence;
    @JsonProperty("total_cycles")
    private int totalCycles;

}
