package estudos.security.project.security.entities.Dto;

import estudos.security.project.security.entities.Books;

import java.awt.print.Book;

public record BookResponse(Long id, String name, String descp, String gener) {
    public static BookResponse fromEntity(Books book){
        return new BookResponse(
                book.getId(),
                book.getName(),
                book.getDescp(),
                book.getGener()
        );
    }
    public static Books toEntity(BookResponse bookResponse){
        return Books.builder()
                .id(bookResponse.id)
                .descp(bookResponse.descp)
                .name(bookResponse.name)
                .name(bookResponse.name)
                .build();
    }
}

