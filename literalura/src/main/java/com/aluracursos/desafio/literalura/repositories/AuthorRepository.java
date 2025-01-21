package com.aluracursos.desafio.literalura.repositories;

import com.aluracursos.desafio.literalura.models.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByNombre(String nombre);

    @Query(value = "SELECT * FROM autores a WHERE a.fecha_Nacimiento <= :year " +
            "AND (a.fecha_Defuncion IS NULL OR a.fecha_Defuncion > :year)",
            nativeQuery = true)
    List<Author> findAuthorsAliveInYear(@Param("year") int year);
}
