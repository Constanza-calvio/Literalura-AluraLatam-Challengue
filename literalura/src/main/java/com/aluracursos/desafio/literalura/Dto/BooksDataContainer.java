package com.aluracursos.desafio.literalura.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public record BooksDataContainer(int count,
                                 List<BooksData> results) {
}
