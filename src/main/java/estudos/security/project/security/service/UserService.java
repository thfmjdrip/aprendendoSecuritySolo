package estudos.security.project.security.service;

import estudos.security.project.security.entities.Dto.LoginRequest;
import estudos.security.project.security.entities.User;
import estudos.security.project.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

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

    public User findByUserName(String userName) {
        return repository.findByUsername(userName).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public boolean isLoginCorrect(LoginRequest loginRequest, PasswordEncoder encoder) {

        User user = findByUserEmail(loginRequest.email());
        var passed = encoder.matches(loginRequest.password(), user.getPassword());
        if (!passed){
            throw  new BadCredentialsException("Login ou passord invalida");
        }
        return passed;
    }



}
