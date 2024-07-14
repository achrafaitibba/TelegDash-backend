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
    @Enumerated(EnumType.STRING)
    private GroupType groupType;
    @Enumerated(EnumType.STRING)
    @Column(length = 2000)
    private List<Niche> niches;
    private String description;
    private Long membersCount;
    @ManyToOne
    private Account channelAdmin;

}
