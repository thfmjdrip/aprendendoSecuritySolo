package estudos.security.project.security.service;

import estudos.security.project.security.entities.Role;
import estudos.security.project.security.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository repository;

    public Role findByName(String name){
        return repository.findByName(name).orElseThrow(() -> new UsernameNotFoundException("Role name not found"));
    }

    public Role findById(Long id) {
        return repository.getReferenceById(id);
    }

    public Role save(Role role){
        return repository.save(role);
    }


}
