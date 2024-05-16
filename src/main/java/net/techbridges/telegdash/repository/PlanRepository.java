package net.techbridges.telegdash.repository;

import net.techbridges.telegdash.model.Plan;
import net.techbridges.telegdash.model.enums.PlanType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    Optional<Plan> findByPlanType(PlanType planType);
}
