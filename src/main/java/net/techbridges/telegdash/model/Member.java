package net.techbridges.telegdash.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.techbridges.telegdash.model.enums.BillingFrequency;
import net.techbridges.telegdash.model.enums.MemberStatus;

import java.time.LocalDate;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;
    @ManyToOne
    private Channel channel;
    @OneToOne
    private TelegramMember telegramMember;
    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus;
    @Enumerated(EnumType.STRING)
    private BillingFrequency billingFrequency;
    private Integer billingPeriod;
    private LocalDate startDate;
    private LocalDate endDate; // todo, start data + (billing frequency * billingPeriod)
}
