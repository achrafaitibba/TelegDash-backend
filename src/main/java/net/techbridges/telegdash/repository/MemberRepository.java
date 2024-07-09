package net.techbridges.telegdash.repository;

import net.techbridges.telegdash.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
