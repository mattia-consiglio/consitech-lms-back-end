package mattia.consiglio.consitech.lms.repositories;

import mattia.consiglio.consitech.lms.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    public Optional<Role> findByRole(String role);

    public boolean existsByRole(String role);
}
