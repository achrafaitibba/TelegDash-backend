package net.techbridges.telegdash.paymentService.paypal.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Subscriber {
    @JsonProperty("email_address")
    private String email;
    @JsonProperty("name")
    private Name name; //todo, use given name for email headers
    @JsonProperty("payer_id")
    private String payerId;
    @JsonProperty("shipping_address")
    private ShippingAddress countryCode;

    record Name(@JsonProperty("given_name") String givenName, String surname) {}
    record ShippingAddress(@JsonProperty("address") Address address) {}
    record Address(@JsonProperty("country_code") String countryCode){}
}
