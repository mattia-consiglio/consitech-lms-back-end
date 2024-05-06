package mattia.consiglio.consitech.lms.services;

import mattia.consiglio.consitech.lms.entities.Role;
import mattia.consiglio.consitech.lms.exceptions.BadRequestException;
import mattia.consiglio.consitech.lms.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public Role getRole(String name) {
        return roleRepository.findByRole(name).orElseThrow(() -> new BadRequestException("Role not found"));
    }

    public boolean existsRole(String name) {
        return roleRepository.existsByRole(name);
    }

    public Role createRole(String role) {

        return roleRepository.save(new Role(role));
    }
}
