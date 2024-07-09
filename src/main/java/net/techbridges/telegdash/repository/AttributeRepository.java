package net.techbridges.telegdash.repository;

import net.techbridges.telegdash.model.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttributeRepository extends JpaRepository<Attribute, Long> {
}
