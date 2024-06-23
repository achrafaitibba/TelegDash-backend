package net.techbridges.telegdash.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.techbridges.telegdash.model.enums.SubscriptionStatus;
import net.techbridges.telegdash.paymentService.paypal.model.Link;

import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Subscription {
    @Id
    private String id;
    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;
    private String statusUpdateTime;
    private String planId;
    private String startTime;
    @OneToOne
    private Subscriber subscriber;
    private String createTime;
    @OneToMany
    private List<Link> links ;


}