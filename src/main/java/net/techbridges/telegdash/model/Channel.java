package net.techbridges.telegdash.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.techbridges.telegdash.model.enums.GroupType;
import net.techbridges.telegdash.model.enums.Niche;

import java.util.List;


@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Channel {
    @Id
    private String channelId;
    private String name;
    private GroupType groupType;
    @OneToMany
    private List<Niche> niches;
    private String description;
    private Long membersCount;
    @ManyToOne
    private Account channelAdmin;
    @OneToMany
    private List<Attribute> attributes;

}
