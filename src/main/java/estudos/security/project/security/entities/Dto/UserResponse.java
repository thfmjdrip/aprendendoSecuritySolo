package estudos.security.project.security.entities.Dto;

import estudos.security.project.security.entities.Books;
import estudos.security.project.security.entities.User;

import java.util.List;

public record UserResponse(String name, String email, List<BookResponse> bookResponses){
    public static UserResponse fromEntity(User user){
        return new UserResponse(
                user.getUsername(),
                user.getUserEmail(),
                user.getBooksList().stream().map(book -> BookResponse.fromEntity(book)).toList()
        );
    }
}
