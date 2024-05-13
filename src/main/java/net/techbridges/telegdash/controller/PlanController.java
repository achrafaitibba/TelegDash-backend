package net.techbridges.telegdash.controller;

import lombok.RequiredArgsConstructor;
import net.techbridges.telegdash.model.Plan;
import net.techbridges.telegdash.service.PlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1.0/plans")
@RequiredArgsConstructor
public class PlanController {
    private final PlanService planService;


    @PostMapping
    public Plan addPlan(@RequestBody Plan plan) {
        System.out.println(plan.toString());
        return planService.createPlan(plan);
    }
}
