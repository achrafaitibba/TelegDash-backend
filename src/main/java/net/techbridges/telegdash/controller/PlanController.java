package net.techbridges.telegdash.controller;

import lombok.RequiredArgsConstructor;
import net.techbridges.telegdash.model.Plan;
import net.techbridges.telegdash.service.PlanService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/plans")
@RequiredArgsConstructor
public class PlanController {
    private final PlanService planService;


    @PostMapping
    public Plan addPlan(@RequestBody Plan plan) {
        return planService.createPlan(plan);
    }

    @DeleteMapping("/{id}")
    public void deletePlan(@PathVariable Long id) {
        planService.deletePlan(id);
    }

    @GetMapping
    public List<Plan> getAllPlans() {
        return planService.getAllPlans();
    }

    @GetMapping("/{id}")
    public Plan getPlan(@PathVariable Long id) {
        return planService.getPlan(id);
    }


}
