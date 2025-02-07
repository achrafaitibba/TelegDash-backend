package net.techbridges.telegdash.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Value {
    @Id
    @GeneratedValue
    private Long id;
    private String value;
    @ManyToOne
    private Attribute attribute;
    @JsonIgnore
    @ManyToOne
    private Member member;
}
