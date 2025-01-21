package com.aluracursos.desafio.literalura.models;

import com.aluracursos.desafio.literalura.Dto.BooksData;
import com.aluracursos.desafio.literalura.Dto.Idioma;
import jakarta.persistence.*;

@Entity
@Table(name = "libros")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String titulo;
    @Enumerated(EnumType.STRING)
    private Idioma idiomas;
    private  double numeroDescargas;
    @ManyToOne (fetch = FetchType.EAGER,cascade = CascadeType.PERSIST)
    @JoinColumn(name = "autor_id", nullable = false)
    private Author autor;

    //CONSTRUCTOR POR DEFAULT
    public Book() {
    }

    //CONSTRUCTOR INICIALIZADOR
    public Book (BooksData booksData) {
        this.titulo = booksData.titulo();
        this.idiomas = Idioma.fromString(booksData.idiomas().toString().split(",")[0].trim());
        this.numeroDescargas = booksData.numeroDescargas();
    }

    @Override
    public String toString() {
        return "******************************************************************" + "\n" +
                "   Titulo: " + (titulo != null ? titulo : "N/A") + "\n" +
                "   Nombre autor: " + (autor != null ? autor.getNombre() : "Autor Desconocido") + "\n" +
                "   Idioma: " + (idiomas != null ? idiomas : "N/A") + "\n" +
                "   Numero de descargas: " + numeroDescargas + "\n" +
                "******************************************************************";
    }

    //GETTERS AND SETTERS
    public Long getId() {
        return Id;
    }
    public void setId(Long id) {
        Id = id;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Idioma getIdiomas() {
        return idiomas;
    }

    public void setIdiomas(Idioma idiomas) {
        this.idiomas = idiomas;
    }

    public double getNumeroDescargas() {
        return numeroDescargas;
    }
    public void setNumeroDescargas(double numeroDescargas) {
        this.numeroDescargas = numeroDescargas;
    }
    public Author getAutor() {
        return autor;
    }
    public void setAutor(Author autor) {
        this.autor = autor;
    }
    // Getter para autor
    public String getNombreAutor() {
        return autor != null ? autor.getNombre() : "Autor Desconocido";
    }
}
