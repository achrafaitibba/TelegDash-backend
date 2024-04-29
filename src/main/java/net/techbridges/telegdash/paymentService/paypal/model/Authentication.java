package net.techbridges.telegdash.paymentService.paypal.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Authentication {
    @JsonProperty("access_token")
    private String accessToken;
}
