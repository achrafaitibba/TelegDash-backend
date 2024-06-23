package net.techbridges.telegdash.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Subscriber {
    @Id
    private String email;
    @OneToOne
    private Name name; //todo, use given name for email headers
    private String payerId;
    @OneToOne
    private ShippingAddress countryCode;

    @Entity
    record Name(@Id @GeneratedValue Long id, @JsonProperty("given_name") String givenName, String surname) {}
    @Entity
    record ShippingAddress(@Id @GeneratedValue Long id, @JsonProperty("address") Address address) {}
    @Entity
    record Address(@Id @GeneratedValue Long id, @JsonProperty("country_code") String countryCode){}
}
