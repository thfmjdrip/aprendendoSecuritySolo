package estudos.security.project.security.service;

import estudos.security.project.security.entities.Books;
import estudos.security.project.security.entities.Dto.BookResponse;
import estudos.security.project.security.entities.Dto.LoginRequest;
import estudos.security.project.security.entities.User;
import estudos.security.project.security.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.awt.print.Book;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final BookService service;

    public List<User>findAllUsers(){
        return repository.findAll();
    }

    public User save(User user){
        return repository.save(user);
    }

    public boolean existsByEmail(String email){
        return  repository.existsByUserEmail(email);
    }

    public User findByUserEmail(String email) {
        return repository.findByUserEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    public void ifUser(String username,String email){
        try {
            existsByEmail(email);
            existsByUsername(username);
        }catch (BadCredentialsException bad){
            throw new BadCredentialsException(bad.getMessage()+"not possible to create user");
        }
    }

    public boolean existsById(Long id){return repository.existsById(id);}

    public User findByUserName(String userName) {
        return repository.findByUsername(userName).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User findById(Long id){
        if (!existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found");
        }
        return repository.getReferenceById(id);
    }

    public boolean isLoginCorrect(LoginRequest loginRequest, PasswordEncoder encoder) {

        User user = findByUserEmail(loginRequest.email());
        var passed = encoder.matches(loginRequest.password(), user.getPassword());
        if (!passed){
            throw  new BadCredentialsException("Login ou passord invalida");
        }
        return passed;
    }

    @Transactional
    public void addBook(Long userId, Long bookId){
        User user = repository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado: " + userId));

        Books book = service.findById(bookId);

        if (!user.getBooksList().contains(book)){
            user.getBooksList().add(book);
        }

    }
}
