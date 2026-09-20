package estudos.security.project.security.service;

import estudos.security.project.security.entities.Books;
import estudos.security.project.security.entities.Dto.CreateBookRequest;
import estudos.security.project.security.entities.User;
import estudos.security.project.security.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.awt.print.Book;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository repository;

    public Books save(Books book){
        return repository.save(book);
    }

    @Transactional
    public Books createBook(CreateBookRequest request){
        if (existsBookByName(request.name())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Já existe um livro cadastrado com este nome: " + request.name()
            );
        }


        var book = Books.builder()
                .name(request.name())
                .descp(request.descp())
                .gener(request.gener())
                .build();

        return save(book);
    }

    public Books findByName(String name){
        return repository.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Não existe esse livro: " + name
                ));
    }

    public List<Books> findByGener(String gener){
        var books = repository.findByGener(gener);
        if(books.isEmpty()){
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND,"Nao existe esse genero");
        }
        return books;
    }

    public Books findById(Long id){
        Books book = repository.getReferenceById(id);
        return book;
    }

    public List<Books>findALlBooks(){
        return repository.findAll();
    }


    public boolean existsBookByName(String name){
        return repository.existsByName(name);
    }

    public boolean exitsById(Long id){return repository.existsById(id);}

    @Transactional
    public void deleteBook(Long id) {
        // 1. Busca o livro
        Books book = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Livro não encontrado"));

        // 2. Remove o vínculo desse livro com todos os usuários associados
        for (User user : book.getUsers()) {
            user.getBooksList().remove(book);
        }

        // 3. Deleta o livro com segurança (as linhas em tb_users_books somem)
        repository.delete(book);
    }

    public Page<Books> obterLivrosPorUsuario(Long userId, Pageable pageable){
        var books = repository.findByUsersId(userId,pageable);
        return books;
    }

}
