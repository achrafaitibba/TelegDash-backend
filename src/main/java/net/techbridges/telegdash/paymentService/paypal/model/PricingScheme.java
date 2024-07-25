package net.techbridges.telegdash.paymentService.paypal.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PricingScheme {
    private int version;
    @JsonProperty("fixed_price")
    private FixedPrice fixedPrice;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private String createTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private String updateTime;
}
