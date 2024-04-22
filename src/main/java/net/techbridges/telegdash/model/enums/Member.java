package net.techbridges.telegdash.model.enums;

import jakarta.persistence.*;
import net.techbridges.telegdash.model.Channel;

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
