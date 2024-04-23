package net.techbridges.telegdash.repository;

import net.techbridges.telegdash.model.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

}
