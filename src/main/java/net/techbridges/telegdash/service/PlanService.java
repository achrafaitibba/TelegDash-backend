package net.techbridges.telegdash.service;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import net.techbridges.telegdash.dto.response.PlanResponse;
import net.techbridges.telegdash.exception.RequestException;
import net.techbridges.telegdash.paymentService.paypal.controller.PaymentController;
import net.techbridges.telegdash.paymentService.paypal.dto.PlanRequest;
import org.springframework.beans.factory.annotation.Value;
import net.techbridges.telegdash.configuration.token.JwtService;
import net.techbridges.telegdash.model.Plan;
import net.techbridges.telegdash.model.enums.ReminderFrequency;
import net.techbridges.telegdash.model.enums.SubscriptionType;
import net.techbridges.telegdash.repository.PlanRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanService {
    private final PlanRepository planRepository;
    @Lazy
    private final HttpServletRequest headers;
    private final JwtService jwtService;
    private final PaymentController paymentController;
    @Value("${app.sudo}")
    private String sudoEmail;

    public Plan createUpdatePlan(Plan plan) {
        if(!isSudo()){
            throw new RequestException("You are not allowed to create/update plans", HttpStatus.UNAUTHORIZED);
        }
        return planRepository.save(plan);
    }

    public void setInactive(Long id) {
        if(!isSudo()){
            throw new RequestException("You are not allowed to delete plans", HttpStatus.UNAUTHORIZED);
        }
        Plan plan = planRepository.findById(id).get();
        plan.setIsActive(false);
        planRepository.save(plan);
    }

    public List<PlanResponse> getAllActivePlans() {
        return planRepository.findAll().stream()
                .filter(plan -> plan.getIsActive() &&
                        (plan.getPlanName().toUpperCase().startsWith("PAID") ||
                                plan.getPlanName().startsWith("FREE")))
                .toList().stream().map(
                        plan -> new PlanResponse(plan.getPlanId(), plan.getPlanName())
                ).toList();
    }

    public List<Plan> getAll() {
        if(!isSudo()){
            throw new RequestException("You are not allowed to get all plans", HttpStatus.UNAUTHORIZED);
        }
        return planRepository.findAll();
    }

    public Plan getPlan(Long id) {
        if(!isSudo()){
            throw new RequestException("You are not allowed to get plans", HttpStatus.UNAUTHORIZED);
        }
        return planRepository.findById(id).orElse(null);
    }



    private boolean isSudo(){
        String token = headers.getHeader("Authorization").substring(7);
        String username = jwtService.extractUsername(token);
        return sudoEmail.equals(username);
    }

    public Object createPaypalPlan(PlanRequest plan) throws Exception {
        if(!isSudo()){
            throw new RequestException("You are not allowed to use this feature, only for app creator", HttpStatus.UNAUTHORIZED);
        }
        return paymentController.createPlan(plan);
    }

    @PostConstruct
    void initPlans(){
        planRepository.save(Plan
                .builder()
                .planId(1L)
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
                .planLevel(1)
                .build());
        planRepository.save(Plan
                .builder()
                .planId(2L)
                .subscriptionType(SubscriptionType.PAID)
                .paypalPlanId("P-6WS33287U8120924FMYWCGJI")
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
                .planLevel(2)
                .build());
        planRepository.save(Plan
                .builder()
                .planId(3L)
                .subscriptionType(SubscriptionType.PAID)
                .paypalPlanId("P-5FE956978J252473GMYWCITQ")
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
                .planLevel(3)
                .build());
        planRepository.save(Plan
                .builder()
                .planId(4L)
                .subscriptionType(SubscriptionType.PAID)
                .paypalPlanId("P-0RL94929W06965005M2OQBYQ")
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
                .planLevel(4)
                .build());


    }
}