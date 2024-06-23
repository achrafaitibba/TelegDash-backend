package net.techbridges.telegdash.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Link {
    @Id
    @GeneratedValue
    private Long id;
    private String href;
    private String rel;
}
