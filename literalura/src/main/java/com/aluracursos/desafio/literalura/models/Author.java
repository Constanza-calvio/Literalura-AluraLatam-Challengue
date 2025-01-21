package com.aluracursos.desafio.literalura.models;


import com.aluracursos.desafio.literalura.Dto.AuthorData;
import jakarta.persistence.*;

import java.util.List;

//TABLA
@Entity
@Table(name = "autores")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private Integer fechaNacimiento;
    private Integer fechaDefuncion;

    @OneToMany(mappedBy = "autor",cascade = CascadeType.ALL,  fetch = FetchType.EAGER)
    private List<Book> libros;

    //CONSTRUCTOR POR DEFAULT
    public Author() {
    }
    //CONSTRUCTOR INICIALIZADOR
    public Author(AuthorData authorData)  {
        this.nombre = authorData.nombre() ;
        this.fechaNacimiento = authorData.fechaNacimiento();
        this.fechaDefuncion = authorData.fechaDefuncion();
    }

    @Override
    public String toString() {
        StringBuilder librosStr = new StringBuilder();
        librosStr.append("Libros: ");

        // Asegúrate de que 'libros' es una lista de tipo List<Book> en la clase Author
        for (int i = 0; i < libros.size(); i++) {
            librosStr.append(libros.get(i).getTitulo());  // 'libros.get(i)' accede al libro
            if (i < libros.size() - 1) {
                librosStr.append(", ");
            }
        }
        return String.format("********** Autor **********%nNombre: %s%n%s%nFecha de Nacimiento: %s%nFecha de Deceso: %s%n***************************%n",
                nombre, librosStr.toString(), fechaNacimiento, fechaDefuncion);
    }

    //GETTERS AND SETTERS
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Integer fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Integer getFechaDefuncion() {
        return fechaDefuncion;
    }

    public void setFechaDefuncion(Integer fechaDefuncion) {
        this.fechaDefuncion = fechaDefuncion;
    }

    public List<Book> getLibros() {
        return libros;
    }
    public void setLibros(List<Book> libros) {
        this.libros = libros;
    }
}
