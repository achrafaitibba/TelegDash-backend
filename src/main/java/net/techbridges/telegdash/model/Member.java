package net.techbridges.telegdash.model;

import jakarta.persistence.*;
import net.techbridges.telegdash.model.enums.BillingFrequency;
import net.techbridges.telegdash.model.enums.MemberStatus;

@Entity
public class Member {
    @Id
    private Long memberId;
    @Column(unique=true)
    private String telegramId;
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
