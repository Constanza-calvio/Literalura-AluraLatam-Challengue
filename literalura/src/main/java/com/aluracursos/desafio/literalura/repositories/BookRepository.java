package com.aluracursos.desafio.literalura.repositories;

import com.aluracursos.desafio.literalura.Dto.Idioma;
import com.aluracursos.desafio.literalura.models.Author;
import com.aluracursos.desafio.literalura.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByTitulo(String titulo);
    List<Book> findByAutor(Author autor);


    List<Book> findByIdiomas(Idioma idioma);


}
