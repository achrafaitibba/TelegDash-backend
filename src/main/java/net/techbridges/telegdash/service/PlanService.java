package net.techbridges.telegdash.service;

import lombok.RequiredArgsConstructor;
import net.techbridges.telegdash.model.Plan;
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

}
