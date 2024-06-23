package net.techbridges.telegdash.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.techbridges.telegdash.model.Plan;
import net.techbridges.telegdash.model.enums.ReminderFrequency;
import net.techbridges.telegdash.model.enums.SubscriptionType;
import net.techbridges.telegdash.repository.PlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanService {
    private final PlanRepository planRepository;


    public Plan createPlan(Plan plan) {
        return planRepository.save(plan);
    }

    public void deletePlan(Long id) {
        planRepository.deleteById(id);
    }

    public List<Plan> getAllPlans() {
        return planRepository.findAll();
    }

    public Plan getPlan(Long id) {
        return planRepository.findById(id).orElse(null);
    }


    @PostConstruct
    void initPlans(){
        createPlan(Plan
                .builder()
                .subscriptionType(SubscriptionType.PAID)
                .paypalPlanId("P-7YK11192208348409MZ4CSEA")
                .planName("Paid-Basic-Monthly")
                .description("Paid-Basic-Monthly")
                .channels(5)
                .members(2500L)
                .reminder(true)
                .reminders(1)
                .reminderFrequency(ReminderFrequency.MONTH)
                .customColumns(1)
                .kickingMember(true)
                .autoKickingMember(true)
                .isActive(true)
                .build());
        createPlan(Plan
                .builder()
                .subscriptionType(SubscriptionType.PAID)
                .paypalPlanId("P-2D941743PY8788919MZ4CTJY")
                .planName("Paid-Premium-Monthly")
                .description("Paid-Premium-Monthly")
                .channels(1000)
                .members(50_000L)
                .reminder(true)
                .reminders(1)
                .reminderFrequency(ReminderFrequency.WEEK)
                .customColumns(5)
                .kickingMember(true)
                .autoKickingMember(true)
                .isActive(true)
                .build());
        createPlan(Plan
                .builder()
                .subscriptionType(SubscriptionType.PAID)
                .paypalPlanId("P-5XX02396AK7680000MZ4CUDQ")
                .planName("Paid-Basic-Yearly")
                .description("Paid-Basic-Yearly")
                .channels(5)
                .members(2500L)
                .reminder(true)
                .reminders(1)
                .reminderFrequency(ReminderFrequency.MONTH)
                .customColumns(1)
                .kickingMember(true)
                .autoKickingMember(true)
                .isActive(true)
                .build());
        createPlan(Plan
                .builder()
                .subscriptionType(SubscriptionType.PAID)
                .paypalPlanId("P-2D473719TF116552EMZ4CU2Y")
                .planName("Paid-Premium-Yearly")
                .description("Paid-Premium-Yearly")
                .channels(1000)
                .members(50_000L)
                .reminder(true)
                .reminders(1)
                .reminderFrequency(ReminderFrequency.WEEK)
                .customColumns(5)
                .kickingMember(true)
                .autoKickingMember(true)
                .isActive(true)
                .build());
        createPlan(Plan
                .builder()
                .subscriptionType(SubscriptionType.FREE)
                .paypalPlanId("XXXXXXXXXXXXXXXXXXXXXX")
                .planName("FREE")
                .description("FREE")
                .channels(1)
                .members(100L)
                .reminder(false)
                .reminders(0)
                .reminderFrequency(ReminderFrequency.MONTH)
                .customColumns(0)
                .kickingMember(true)
                .autoKickingMember(false)
                .isActive(true)
                .build());
        createPlan(Plan
                .builder()
                .subscriptionType(SubscriptionType.PAID)
                .paypalPlanId("P-6WS33287U8120924FMYWCGJI")
                .planName("sandbox")
                .description("sandbox")
                .channels(1)
                .members(100L)
                .reminder(false)
                .reminders(0)
                .reminderFrequency(ReminderFrequency.MONTH)
                .customColumns(0)
                .kickingMember(true)
                .autoKickingMember(false)
                .isActive(true)
                .build());
    }
}