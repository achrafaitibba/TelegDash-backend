package net.techbridges.telegdash.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Member {
    @Id
    private Long memberId;
    private String username;
    private String firstName;
    private String lastName;
    private MemberStatus memberStatus;
    private BillingFrequency billingFrequency;
    private Integer billingPeriod;
    private Integer subscriptionLength; // todo, billing frequency * billing period
    @ManyToOne
    private Channel channel;
}
