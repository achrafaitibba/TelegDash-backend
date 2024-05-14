package net.techbridges.telegdash.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import net.techbridges.telegdash.model.enums.ReminderFrequency;

@Entity
@Data
public class Plan {
    @Id
    @GeneratedValue
    private Long planId;
    private String paypalPlanId;
    private String planName;
    private String description;
    private Integer channels;
    private Long members;
    private Boolean reminder;
    private Integer reminders;
    private ReminderFrequency reminderFrequency;
    private Integer customColumns;
    private Boolean kickingMember;
    private Boolean autoKickingMember;
    private Boolean isActive = true;
}
