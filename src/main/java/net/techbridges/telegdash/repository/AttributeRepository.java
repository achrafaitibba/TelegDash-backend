package net.techbridges.telegdash.repository;

import net.techbridges.telegdash.model.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttributeRepository extends JpaRepository<Attribute, Long> {
    List<Attribute> findAllByChannelChannelId(String channelID);
}
