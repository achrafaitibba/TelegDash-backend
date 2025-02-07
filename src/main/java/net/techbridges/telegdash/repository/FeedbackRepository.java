package net.techbridges.telegdash.repository;

import net.techbridges.telegdash.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

}
