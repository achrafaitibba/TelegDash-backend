package net.techbridges.telegdash.service;

import lombok.RequiredArgsConstructor;
import net.techbridges.telegdash.model.Plan;
import net.techbridges.telegdash.repository.PlanRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanService {
    private final PlanRepository planRepository;


    public Plan createPlan(Plan plan) {
        return planRepository.save(plan);
    }

    public Plan updatePlan(Plan plan) {
        return planRepository.save(plan);
    }
    public void deletePlan(Plan plan) {
        planRepository.delete(plan);
    }


}
