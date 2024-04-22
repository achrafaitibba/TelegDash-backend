package net.techbridges.telegdash.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import net.techbridges.telegdash.model.enums.ValueType;

@Entity
public class Attribute {
    @Id
    private Long id;
    private String name;
    private ValueType valueType;
}
