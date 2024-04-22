package net.techbridges.telegdash.model;

import jakarta.persistence.*;
import net.techbridges.telegdash.model.enums.Niche;

import java.util.List;


@Entity
public class Channel {
    @Id
    private Long id;
    private String name;
    private List<Niche> niches;
    private String description;
    private Long membersCount;
    @ManyToOne
    private Account channelOwner;
    @OneToMany
    private List<Attribute> attributes;
}
