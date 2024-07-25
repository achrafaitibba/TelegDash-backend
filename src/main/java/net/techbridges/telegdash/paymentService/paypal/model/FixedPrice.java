package net.techbridges.telegdash.paymentService.paypal.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FixedPrice {
    @JsonProperty("currency_code")
    private String currencyCode;
    private String value;
}
