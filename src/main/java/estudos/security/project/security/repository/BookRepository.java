package estudos.security.project.security.repository;

import estudos.security.project.security.entities.Books;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Book;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Books,Long> {
    Optional<Books> findByName(String name);
    boolean existsByName(String name);
    List<Books> findByGener(String name);
    Page<Books> findByUsersId(Long userId, Pageable pageable);
    boolean existsById(@NonNull Long id);
}
