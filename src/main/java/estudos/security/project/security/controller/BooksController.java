package estudos.security.project.security.controller;


import estudos.security.project.security.entities.Books;
import estudos.security.project.security.entities.Dto.AssociateBookRequestDTO;
import estudos.security.project.security.entities.Dto.BookResponse;
import estudos.security.project.security.entities.Dto.CreateBookRequest;
import estudos.security.project.security.service.BookService;
import estudos.security.project.security.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Livros", description = "Gerenciamento de livros e associação com usuários")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books")
public class BooksController {
    private final BookService bookService;
    private final UserService userService;

    @Operation(summary = "Criar novo livro", description = "Cadastra um novo livro no sistema (requer role ADMIN)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Livro criado com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookResponse.class))),
        @ApiResponse(responseCode = "403", description = "Acesso negado - requer role ADMIN", content = @Content),
        @ApiResponse(responseCode = "409", description = "Livro com este nome já existe", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<BookResponse> createBook(@RequestBody CreateBookRequest createBookRequest) {
        var bookCreate = bookService.createBook(createBookRequest);
        var bookResponse = BookResponse.fromEntity(bookCreate);
        return ResponseEntity.ok(bookResponse);

    }

    @Operation(summary = "Listar todos os livros", description = "Retorna lista de todos os livros, com filtro opcional por gênero")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de livros",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookResponse.class))),
        @ApiResponse(responseCode = "404", description = "Nenhum livro encontrado para o gênero", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<BookResponse>> findAll(@RequestParam(required = false) String gener) {
        List<Books> booksList;

        if (gener != null && !gener.isBlank()) {
            booksList = bookService.findByGener(gener);
        } else {
            booksList = bookService.findALlBooks();
        }

        List<BookResponse> responseList = booksList.stream()
                .map(BookResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(responseList);
    }

    @Operation(summary = "Deletar livro", description = "Remove um livro do sistema (requer role ADMIN)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Livro deletado com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - requer role ADMIN", content = @Content),
        @ApiResponse(responseCode = "404", description = "Livro não encontrado", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteBookByAdm(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar livros do usuário", description = "Retorna lista paginada de livros associados a um usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista paginada de livros do usuário",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    @GetMapping("/{userId}/books")
    public ResponseEntity<Page<BookResponse>> getBooksByUser(@PathVariable Long userId, @PageableDefault(page = 0, size = 5) Pageable pageable) {
        Page<BookResponse> books = bookService.obterLivrosPorUsuario(userId, pageable).map(book -> BookResponse.fromEntity(book));
        return ResponseEntity.ok(books);

    }

    @Operation(summary = "Adicionar livro ao usuário", description = "Associa um livro existente ao carrinho do usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Livro adicionado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Usuário ou livro não encontrado", content = @Content)
    })
    @PostMapping("/{userId}/add")
    public ResponseEntity<Void> addBookToUser(@PathVariable Long userId, @RequestBody AssociateBookRequestDTO dto) {
        userService.addBook(userId, dto.id());
        return ResponseEntity.ok().build();
    }

}
