package net.techbridges.telegdash.model;

import jakarta.persistence.*;
import net.techbridges.telegdash.model.enums.Member;

import java.util.List;

@Entity
public class Channel {
    @Id
    private Long id;
    private String name;
    private String description;
    private Long membersCount;
    @OneToMany
    private List<Member> members;
    @ManyToOne
    private Account channelOwner;
    @OneToMany
    private List<Attribute> attributes;
}
