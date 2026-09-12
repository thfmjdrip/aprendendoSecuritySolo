package estudos.security.project.security.controller;


import estudos.security.project.security.entities.Books;
import estudos.security.project.security.entities.Dto.BookResponse;
import estudos.security.project.security.entities.Dto.CreateBookRequest;
import estudos.security.project.security.service.BookService;
import estudos.security.project.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books")
public class BooksController {
    private final BookService bookService;
    private final UserService userService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<BookResponse>createBook(@RequestBody CreateBookRequest createBookRequest){
        var bookCreate = bookService.createBook(createBookRequest);
        var bookResponse = BookResponse.fromEntity(bookCreate);
        return ResponseEntity.ok(bookResponse);

    }

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

    @DeleteMapping("delete/{id}")
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<Void> deleteBookByAdm(@PathVariable Long id){
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

}
