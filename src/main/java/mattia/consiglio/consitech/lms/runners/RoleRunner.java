package mattia.consiglio.consitech.lms.runners;

import mattia.consiglio.consitech.lms.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleRunner implements CommandLineRunner {
    @Autowired
    private RoleService roleService;

    @Override
    public void run(String... args) throws Exception {
        if (!roleService.existsRole("ADMIN")) {
            roleService.createRole("ADMIN");
        }
        if (!roleService.existsRole("USER")) {
            roleService.createRole("USER");
        }
    }
}
