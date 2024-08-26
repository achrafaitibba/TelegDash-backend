package net.techbridges.telegdash.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.techbridges.telegdash.model.enums.ReminderFrequency;
import net.techbridges.telegdash.model.enums.SubscriptionType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Plan {
    @Id
    @GeneratedValue
    private Long planId;
    @Enumerated(EnumType.STRING)
    private SubscriptionType subscriptionType;
    private String paypalPlanId;
    private String planName;
    private String description;
    private Integer channels;
    private Long members;
    private Boolean reminder;
    private Integer reminders;
    @Enumerated(EnumType.STRING)
    private ReminderFrequency reminderFrequency;
    private Integer customColumns;
    private Boolean kickingMember;
    private Boolean autoKickingMember;
    private Boolean isActive = true;
    private Integer planLevel;
}