package estudos.security.project.security.config;

import estudos.security.project.security.entities.Role;
import estudos.security.project.security.entities.User;
import estudos.security.project.security.repository.UserRepository;
import estudos.security.project.security.service.RoleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class AdminConfig implements CommandLineRunner {

    private final RoleService roleService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        var userAdmin = userRepository.findByUsername("admin");

        if (userAdmin.isPresent()) {
            System.out.println("Admin já existe.");
            return;
        }

        // Garante a extração da Role do Optional ou lança exceção amigável
        Role roleAdm = roleService.findByName(Role.Values.ADMIN.name());
        var user = new User();
        user.setUsername("admin");
        user.setUserEmail("adminUser@gmail.com");
        user.setPassword(bCryptPasswordEncoder.encode("123"));
        user.setRoleUser(roleAdm);

        userRepository.save(user);
        System.out.println("Usuário Admin criado com sucesso!");
    }
}