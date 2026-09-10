package estudos.security.project.security.repository;

import estudos.security.project.security.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUserEmail(String email);
    boolean existsByUserEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
