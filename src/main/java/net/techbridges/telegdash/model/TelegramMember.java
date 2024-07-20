package net.techbridges.telegdash.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelegramMember {
    @Id
    @Column(unique=true)
    private String telegramMemberId;
    private String username;
    private String firstName;
    private String lastName;
}
