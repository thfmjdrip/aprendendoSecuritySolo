package estudos.security.project.security.entities.Dto;

import estudos.security.project.security.entities.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public record CreateUserDto(String username, String email, String password) {
    public User toEntity(BCryptPasswordEncoder bCryptPasswordEncoder){
        return User.builder()
                .username(this.username)
                .password(bCryptPasswordEncoder.encode(this.password))
                .userEmail(this.email)
                .build();
    }
}
