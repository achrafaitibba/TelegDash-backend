package net.techbridges.telegdash.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import net.techbridges.telegdash.model.enums.ReminderFrequency;

@Entity
public class Plan {
    @Id
    private Long planId;
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
}
