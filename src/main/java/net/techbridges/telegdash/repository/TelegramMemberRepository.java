package net.techbridges.telegdash.repository;

import net.techbridges.telegdash.model.TelegramMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelegramMemberRepository extends JpaRepository<TelegramMember, String> {
}
