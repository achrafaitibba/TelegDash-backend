package net.techbridges.telegdash.repository;

import net.techbridges.telegdash.model.Value;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ValueRepository extends JpaRepository<Value, Long> {

    List<Value> findAllByMemberMemberId(Long memberId);
}
