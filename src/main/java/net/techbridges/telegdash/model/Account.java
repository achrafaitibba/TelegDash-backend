package net.techbridges.telegdash.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.techbridges.telegdash.configuration.token.Token;
import net.techbridges.telegdash.model.enums.AccountType;
import net.techbridges.telegdash.model.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
public class Account implements UserDetails {

    @Id
    private String username;
    private String password;
    private Role role;
    @OneToOne
    private Plan plan;
    @OneToOne
    private Plan trialPlan;
    private AccountType accountType;
    @OneToMany
    private List<Payment> payments;
    private String freeTrialEndDate;
    @OneToMany(mappedBy = "account")
    private List<Token> tokens;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}