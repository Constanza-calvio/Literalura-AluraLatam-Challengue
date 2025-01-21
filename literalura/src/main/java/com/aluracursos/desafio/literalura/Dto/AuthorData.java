package com.aluracursos.desafio.literalura.Dto;


import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AuthorData(
        @JsonAlias("name") String nombre,
        @JsonAlias("birth_year") Integer fechaNacimiento,
        @JsonAlias("death_year") Integer fechaDefuncion
){
}
