package net.techbridges.telegdash.repository;

import net.techbridges.telegdash.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findAllByChannelChannelId(String channelId);
    Optional<Member> findByTelegramMemberTelegramMemberIdAndChannelChannelId(String telegramMemberId, String channelId);
    Optional<Member> findByTelegramMemberTelegramMemberId(String telegramMemberId);
}
