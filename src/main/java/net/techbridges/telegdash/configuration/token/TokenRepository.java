package net.techbridges.telegdash.configuration.token;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, UUID> {
    @Query("""
    select t from Token t inner join Account a on t.account.email = a.email
    where a.email = :username and (t.expired = false or t.revoked = false)
""")
    List<Token> findAllValidTokensByAccount(String username);
    Optional<Token> findByToken(String token);
}
