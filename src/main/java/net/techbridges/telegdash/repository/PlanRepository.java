package net.techbridges.telegdash.repository;

import net.techbridges.telegdash.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PlanRepository extends JpaRepository<Plan, Long> {
}
