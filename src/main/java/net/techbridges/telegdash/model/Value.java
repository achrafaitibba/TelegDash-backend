package net.techbridges.telegdash.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Value {
    @Id
    private Long id;
    private String value;
    @ManyToOne
    private Attribute attribute;
}
