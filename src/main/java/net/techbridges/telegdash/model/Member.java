package net.techbridges.telegdash.model;

import jakarta.persistence.*;
import net.techbridges.telegdash.model.enums.BillingFrequency;
import net.techbridges.telegdash.model.enums.MemberStatus;

import java.util.Date;

@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;
    @Column(unique=true)
    private String telegramMemberId;
    private String username;
    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus;
    @Enumerated(EnumType.STRING)
    private BillingFrequency billingFrequency;
    private Integer billingPeriod;
    private Date startDate;
    private Date endDate; // todo, start data + (billing frequency * billingPeriod)
    @ManyToOne
    private Channel channel;
    //todo, firstName + lastName should be hidden in the members table, optional
    private String firstName;
    private String lastName;
}
