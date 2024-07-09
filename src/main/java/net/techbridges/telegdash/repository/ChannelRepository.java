package net.techbridges.telegdash.repository;

import net.techbridges.telegdash.model.Account;
import net.techbridges.telegdash.model.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChannelRepository extends JpaRepository<Channel, String> {
    long countByChannelAdmin(Account admin);
    List<Channel> findAllByChannelAdmin(Account admin);
}
