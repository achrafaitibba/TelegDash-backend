package net.techbridges.telegdash.paymentService.paypal.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Frequency {
    @JsonProperty("interval_unit")
    private String intervalUnit;
    @JsonProperty("interval_count")
    private int intervalCount;

}
