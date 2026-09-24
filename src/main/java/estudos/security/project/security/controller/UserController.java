package estudos.security.project.security.controller;

import estudos.security.project.security.entities.Dto.CreateUserDto;
import estudos.security.project.security.entities.Dto.UserResponse;
import estudos.security.project.security.entities.Role;
import estudos.security.project.security.entities.User;
import estudos.security.project.security.service.RoleService;
import estudos.security.project.security.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Tag(name = "Usuários", description = "Gerenciamento de usuários e carrinho de livros")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final RoleService roleService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @Operation(summary = "Registrar novo usuário", description = "Cria um novo usuário com role BASIC")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário criado com sucesso"),
        @ApiResponse(responseCode = "422", description = "Usuário ou email já existe", content = @Content),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody CreateUserDto dto) {
        var roleBasic = roleService.findByName(Role.Values.BASIC.name());
        var userBasic = dto.toEntity(bCryptPasswordEncoder);
        userBasic.setRoleUser(roleBasic);
        try {
            userService.ifUser(userBasic.getUsername(), userBasic.getUserEmail());
            userService.save(userBasic);
        } catch (Exception e) {
            System.out.println("is not possible to create a user");
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Listar todos os usuários", description = "Retorna lista de todos os usuários (requer role ADMIN)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuários",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "403", description = "Acesso negado - requer role ADMIN", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<User>> users() {
        var users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }


    @Operation(summary = "Buscar usuário por ID", description = "Retorna dados do usuário incluindo livros associados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> findUserById(@PathVariable Long userId) {
        var user = UserResponse.fromEntity(userService.findById(userId));
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Remover livro do carrinho do usuário", description = "Remove um livro específico da lista de livros do usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Livro removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário ou livro não encontrado", content = @Content)
    })
    @PostMapping("/deleteBook/{id}")
    public ResponseEntity<Void> deleteUserBook(@PathVariable("id") Long bookId, @RequestParam Long userId) {
        userService.removeBookFromUser(userId, bookId);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "Atualizar usuário", description = "Atualiza nome e email do usuário (requer role ADMIN)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - requer role ADMIN", content = @Content),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @PostMapping("/admin/{userId}")
    public ResponseEntity<Void> updateUser(@RequestBody UserResponse userResponse, @PathVariable Long userId) {
        userService.updateUser(userResponse, userId);
        return ResponseEntity.ok().build();
    }
}
