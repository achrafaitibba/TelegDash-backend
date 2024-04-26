package net.techbridges.telegdash.model;

import jakarta.persistence.*;
import net.techbridges.telegdash.model.enums.Niche;

import java.util.List;


@Entity
public class Channel {
    @Id
    private String channelId;
    private String name;
    @OneToMany
    private List<Niche> niches;
    private String description;
    private Long membersCount;
    @ManyToOne
    private Account channelAdmin;
    @OneToMany
    private List<Attribute> attributes;

}
