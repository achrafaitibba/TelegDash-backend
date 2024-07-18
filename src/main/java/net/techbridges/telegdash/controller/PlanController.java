package net.techbridges.telegdash.controller;

import lombok.RequiredArgsConstructor;
import net.techbridges.telegdash.dto.response.PlanResponse;
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
    public Plan createUpdatePlan(@RequestBody Plan plan) {
        return planService.createUpdatePlan(plan);
    }

    @DeleteMapping("/{id}")
    public void setInactive(@PathVariable Long id) {
        planService.setInactive(id);
    }

    @GetMapping("/active")
    public List<PlanResponse> getAllActivePlans() {
        return planService.getAllActivePlans();
    }
    @GetMapping
    public List<Plan> getAll() {
        return planService.getAll();
    }

    @GetMapping("/{id}")
    public Plan getPlan(@PathVariable Long id) {
        return planService.getPlan(id);
    }


}
