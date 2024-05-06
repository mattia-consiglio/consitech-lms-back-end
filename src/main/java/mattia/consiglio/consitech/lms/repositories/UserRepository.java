package mattia.consiglio.consitech.lms.repositories;

import mattia.consiglio.consitech.lms.entities.User;
import mattia.consiglio.consitech.lms.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    public Optional<User> findByUsername(String username);

    public boolean existsByUsername(String username);

    public boolean existsByEmail(String email);

    public boolean existsByUsernameOrEmail(String username, String email);


    public Optional<User> findByUsernameOrEmail(String username, String email);

    public int countByRole(UserRole role);
}
