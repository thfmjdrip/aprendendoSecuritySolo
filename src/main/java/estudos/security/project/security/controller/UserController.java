package estudos.security.project.security.controller;

import estudos.security.project.security.entities.Dto.CreateUserDto;
import estudos.security.project.security.entities.Dto.UserResponse;
import estudos.security.project.security.entities.Role;
import estudos.security.project.security.entities.User;
import estudos.security.project.security.service.RoleService;
import estudos.security.project.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final RoleService roleService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody CreateUserDto dto){
        var roleBasic = roleService.findByName(Role.Values.BASIC.name());
        var userBasic = dto.toEntity(bCryptPasswordEncoder);
        userBasic.setRoleUser(roleBasic);
        try {
            userService.ifUser(userBasic.getUsername(), userBasic.getUserEmail());
            userService.save(userBasic);
        }catch (Exception e){
            System.out.println("is not possible to create a user");
            throw  new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT);
        }
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<User>> users(){
        var users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }


    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse>findUserById(@PathVariable Long userId){
        var user = UserResponse.fromEntity(userService.findById(userId));
        return ResponseEntity.ok(user);
    }

    @PostMapping("deleteBook/{id}")
    public ResponseEntity<Void>deleteUserBook(Long bookId, Long userId){
        userService.removeBookFromUser(bookId,userId);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/admin/{userId}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void>updateUser(@RequestBody UserResponse userResponse, Long userId ){
        userService.updateUser(userResponse,userId);
        return ResponseEntity.ok().build();
    }
}
