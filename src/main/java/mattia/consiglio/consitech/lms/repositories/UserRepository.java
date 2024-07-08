package mattia.consiglio.consitech.lms.repositories;

import mattia.consiglio.consitech.lms.entities.User;
import mattia.consiglio.consitech.lms.entities.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsernameOrEmail(String username, String email);


    Optional<User> findByUsernameOrEmail(String username, String email);

    int countByRole(UserRole role);
}
