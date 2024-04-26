package net.techbridges.telegdash.model.enums;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;


@Getter
@Entity
public enum Niche {
    BUSINESS,
    COOKING,
    CRYPTO,
    DESIGN,
    EDUCATION,
    ENTERTAINMENT,
    FASHION,
    FINANCE,
    GAMING,
    HEALTH,
    MARKETING,
    NEWS,
    PHOTOGRAPHY,
    POLITICS,
    PRIVATE_CONTENT,
    RELIGION,
    SCIENCE,
    SPORTS,
    TECHNOLOGY,
    TRADING,
    UNDEFINED;

    @Id
    private Long id;

}
