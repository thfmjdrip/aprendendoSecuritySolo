package estudos.security.project.security.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tb_books")
public class Books {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "book_id")
    private Long id;
    private String name;
    private String descp;
    private String gener;
    @ManyToMany(mappedBy = "booksList")
    private List<User> users = new ArrayList<>();
}
