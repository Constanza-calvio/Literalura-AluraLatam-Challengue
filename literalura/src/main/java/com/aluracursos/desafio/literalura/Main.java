package com.aluracursos.desafio.literalura;

import com.aluracursos.desafio.literalura.Dto.AuthorData;
import com.aluracursos.desafio.literalura.Dto.BooksData;
import com.aluracursos.desafio.literalura.Dto.BooksDataContainer;
import com.aluracursos.desafio.literalura.models.Author;
import com.aluracursos.desafio.literalura.models.Book;
import com.aluracursos.desafio.literalura.repositories.AuthorRepository;
import com.aluracursos.desafio.literalura.repositories.BookRepository;
import com.aluracursos.desafio.literalura.services.ConsumeApi;
import com.aluracursos.desafio.literalura.services.ConvertData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class Main {

    //conexion base de datos
    private static final String URL_BASE = "https://gutendex.com/books/";
    //consumirApi
    private ConsumeApi consumeApi = new ConsumeApi();
    private ConvertData conversor = new ConvertData();
    private Scanner teclado = new Scanner(System.in);
    @Autowired
    private AuthorRepository authorRepo;
    @Autowired
    private BookRepository bookRepo;

    private List<Author> autores;
    private List<Book> libros;

    public Main(BookRepository bookRepo, AuthorRepository authorRepo) {
        this.bookRepo = bookRepo;
        this.authorRepo = authorRepo;
    }

    //Menu
    public void mostrarMenu() {
        var opcion = 1;

        while (opcion != 0) {
            System.out.println(" ************************************** ");
            System.out.println(" Bienvenidos a nuestra biblioteca GUTENDEX ");
            System.out.println(" ¿Que deseas hacer hoy?");
            System.out.println("*********************************\n");

            var menu = """
                    1 - Buscar libros por título
                    2 - Mostrar libros registrados
                    3 - Mostrar autores registrados
                    4 - Autores vivos en determinado año
                    5 - Buscar libros por idioma
                    6 - Salir
                    
                    
                    0 - Salir
                    
                    """;
            System.out.println(menu);
            while (!teclado.hasNextInt()) {
                System.out.println("Formato inválido, ingrese un número que este disponible en el menú!");
                teclado.nextLine();
            }
            System.out.println(" ************************************************** ");
            opcion = teclado.nextInt();
            teclado.nextLine();
            switch (opcion) {
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2:
                    mostrarLibrosHistorial();
                    break;
                case 3:
                    mostrarAutoresRegistrados();
                    break;
                case 4:
                    autoresVivosPorAnio();
                    break;
                case 5:
                    buscarLibrosPorIdioma();
                    break;
                case 6:
                    salir();
                case 0:
                    System.out.println("¡Gracias por usar nuestra App, Nos vemos pronto!");
                    break;
                default:
                    System.out.printf("Opción inválida\n");
            }
        }

    }

    private void salir() {
        System.out.println("¡Gracias por usar nuestra App, Nos vemos pronto!");
    }

    private BooksData booksData() {
        System.out.println("Escribe el nombre del libro: ");
        var nombreLibro = teclado.nextLine();
        var json = consumeApi.obtainData(URL_BASE + nombreLibro.replace(" ", "%20"));
        BooksData datosLibro = conversor.takeData(json, BooksData.class);
        return datosLibro;

    }

    //CREACION DE NUESTRO LIBRO PARA HACER EL HISTORIAL DE CONSULTA LIBROS
    private Book crearLibro(BooksData datosLibro, Author autor) {
        Book libro = new Book(datosLibro);
        return bookRepo.save(libro);
    }

    //METODO PARA MOSTAR LIBRO POR TITULO
    private void buscarLibroPorTitulo() {
        System.out.println("Por favor ingrese el nombre del libro que desea buscar:");

// Revisión en la base de datos
        var tituloBuscado = teclado.nextLine();
        Optional<Book> libroEnBD = bookRepo.findByTitulo(tituloBuscado);

        if (libroEnBD.isPresent()) {
            // Si el libro ya existe en la base de datos
            System.out.println("Parece que ya has consultado por este título. ¡Aquí te muestro los datos!");
            System.out.println(libroEnBD.get());
            return;
        }

// Si el libro no está en la base de datos, buscar en la API
        try {
            System.out.println("Buscando el libro en la API...");

            // Realizar la consulta a la API
            String jsonResponse = consumeApi.obtainData(URL_BASE + "?search=" + tituloBuscado.replace(" ", "%20"));

            // Deserialización de la respuesta JSON en un BooksDataContainer
            ObjectMapper mapper = new ObjectMapper();
            BooksDataContainer contenedor = mapper.readValue(jsonResponse, BooksDataContainer.class);

            // Filtrar el libro que coincida con el título
            Optional<BooksData> libroEncontrado = contenedor.results().stream()
                    .filter(libro -> libro.titulo().equalsIgnoreCase(tituloBuscado))
                    .findFirst();

            if (libroEncontrado.isPresent()) {
                // Crear la instancia de Book a partir del BooksData encontrado
                BooksData data = libroEncontrado.get();
                Book nuevoLibro = new Book(data);

                // Buscar si el autor ya existe en la base de datos
                Optional<Author> autorExistente = authorRepo.findByNombre(data.autor().isEmpty() ? "Autor Desconocido" : data.autor().get(0).nombre());

                Author autor;
                if (autorExistente.isPresent()) {
                    // Si el autor ya existe, usar la instancia existente
                    autor = autorExistente.get();
                } else {
                    // Si el autor no existe, crear uno nuevo y asignar los datos del autor
                    autor = new Author();
                    autor.setNombre(data.autor().isEmpty() ? "Autor Desconocido" : data.autor().get(0).nombre());
                    autor.setFechaNacimiento(data.autor().isEmpty() ? null : data.autor().get(0).fechaNacimiento());
                    autor.setFechaDefuncion(data.autor().isEmpty() ? null : data.autor().get(0).fechaDefuncion());
                    authorRepo.save(autor); // Guardar el nuevo autor en la base de datos
                }

                // Asignar el autor al libro
                nuevoLibro.setAutor(autor);

                // Guardar el libro en la base de datos
                bookRepo.save(nuevoLibro);

                // Mostrar los resultados
                System.out.println("Libro encontrado en la API y guardado en la base de datos:");
                System.out.println(nuevoLibro.toString());
            } else {
                System.out.println("No se encontró ningún libro con el título: " + tituloBuscado + " en la API.");
            }

        } catch (Exception e) {
            System.err.println("Error al buscar el libro en la API: " + e.getMessage());
        }System.out.println("Por favor ingrese el nombre del libro que desea buscar:");

// Revisión en la base de datos
        var titulo = teclado.nextLine();
        Optional<Book> librobusqueda = bookRepo.findByTitulo(titulo);

        if (librobusqueda.isPresent()) {
            // Si el libro ya existe en la base de datos
            System.out.println("Parece que ya has consultado por este título. ¡Aquí te muestro los datos!");
            System.out.println(librobusqueda.get());
            return;
        }

// Si el libro no está en la base de datos, buscar en la API
        try {
            System.out.println("Buscando el libro en la API...");

            // Realizar la consulta a la API
            String jsonResponse = consumeApi.obtainData(URL_BASE + "?search=" + titulo.replace(" ", "%20"));

            // Deserialización de la respuesta JSON en un BooksDataContainer
            ObjectMapper mapper = new ObjectMapper();
            BooksDataContainer contenedor = mapper.readValue(jsonResponse, BooksDataContainer.class);

            // Filtrar el libro que coincida con el título
            Optional<BooksData> libroEncontrado = contenedor.results().stream()
                    .filter(libro -> libro.titulo().equalsIgnoreCase(titulo))
                    .findFirst();

            if (libroEncontrado.isPresent()) {
                // Crear la instancia de Book a partir del BooksData encontrado
                BooksData data = libroEncontrado.get();
                Book nuevoLibro = new Book(data);

                // Buscar si el autor ya existe en la base de datos
                Optional<Author> autorExistente = authorRepo.findByNombre(data.autor().isEmpty() ? "Autor Desconocido" : data.autor().get(0).nombre());

                Author autor;
                if (autorExistente.isPresent()) {
                    // Si el autor ya existe, usar la instancia existente
                    autor = autorExistente.get();
                } else {
                    // Si el autor no existe, crear uno nuevo y asignar los datos del autor
                    autor = new Author();
                    autor.setNombre(data.autor().isEmpty() ? "Autor Desconocido" : data.autor().get(0).nombre());
                    autor.setFechaNacimiento(data.autor().isEmpty() ? null : data.autor().get(0).fechaNacimiento());
                    autor.setFechaDefuncion(data.autor().isEmpty() ? null : data.autor().get(0).fechaDefuncion());
                    authorRepo.save(autor); // Guardar el nuevo autor en la base de datos
                }

                // Asignar el autor al libro
                nuevoLibro.setAutor(autor);

                // Guardar el libro en la base de datos
                bookRepo.save(nuevoLibro);

                // Mostrar los resultados
                System.out.println("Libro encontrado en la API y guardado en la base de datos:");
                System.out.println(nuevoLibro.toString());
            } else {
                System.out.println("No se encontró ningún libro con el título: " + titulo + " en la API.");
            }

        } catch (Exception e) {
            System.err.println("Error al buscar el libro en la API: " + e.getMessage());
        }
    }

    //METODO PARA MOSTRAR LIBROS REGISTRADOS
    private void mostrarLibrosHistorial() {
        List<Book> librosRegistrados = bookRepo.findAll();
        librosRegistrados.stream()
                .sorted(Comparator.comparing(libro -> libro.getAutor().getNombre()))
                .forEach(System.out::println);
    }

    //METODO PARA MOSTRAR AUTORES REGISTRADOS
    private void mostrarAutoresRegistrados() {
        System.out.println("buscando autores consultados previamente...");
        List<Author> autoresRegistrados = authorRepo.findAll();
        // Ordena y recorre los autores
        autoresRegistrados.stream()
                .sorted(Comparator.comparing(Author::getNombre)) // Ordenar por nombre del autor
                .forEach(author -> {
                    // Utiliza el toString del record AuthorData para mostrar los datos del autor
                    AuthorData authorData = new AuthorData(author.getNombre(),
                            author.getFechaNacimiento(),
                            author.getFechaDefuncion());
                    System.out.println(authorData);
                    // Lista los libros del autor
                    System.out.println("Libros registrados de " + author.getNombre());
                    author.getLibros().forEach(libro -> System.out.println("  - " + libro.getTitulo()));
                    System.out.println();
                });
    }

    //METODO PARA MOSTRAR AUTORES VIVOS SEGUN AÑO
    private void autoresVivosPorAnio() {
        System.out.println("ingresa el año que desea indagar para ver si estaban vivos los Autor(es)");
        try {
            int year = teclado.nextInt();
            teclado.nextLine();

            List<Author> autoresVivos = authorRepo.findAuthorsAliveInYear(year);
            if (autoresVivos.isEmpty()) {
                System.out.println("""
                             ******************* No hay autores vivos segun tus registros **********
                        """);
            } else {
                System.out.println("********Buscando*********" + "\n");
                System.out.println("segun la fecha ingresada, estos son los autores que seguian vivos en ese tiempo: ");
                for (Author autor : autoresVivos) {
                    // Aquí accedes al AuthorData del autor
                    String infoAutor = autor.toString();
                    System.out.println(infoAutor); // Imprime la información del autor según el toString sobrescrito
                    System.out.println("--------------------------------------------");
                }
            }
        } catch (InputMismatchException e) {
            teclado.nextLine();
            System.out.println("""
                    *********************** debe ingresar el año como numero, intentelo denuevo   **********************
                    """);
        }
    }
    private void buscarLibrosPorIdioma(){

    }
}

    /*private List<Book> libroSegunIdioma(String idioma) {
        Idioma dato = Idioma.fromString(idioma);
        System.out.println("Lenguaje buscado: " + dato);

        return bookRepo.findByIdiomas(dato);
    }

    //METODO PARA BUSCAR LIBROS SEGUN IDIOMA
    private void buscarLibrosPorIdioma() {
        System.out.println("Ingresa el idioma para buscar libros");

        while (true) {
            var menu = """
                    Escribe uno de los siguientes códigos de idioma:
                    - en: Inglés
                    - es: Español
                    - fr: Francés
                    - pt: Portugués
                    
                    Escribe "salir" para volver al menú anterior
                    """;
            System.out.println(menu);

            // Leer entrada del usuario
            String opcion = teclado.nextLine().trim();

            // Salir del bucle si el usuario escribe "salir"
            if (opcion.equalsIgnoreCase("salir")) {
                return;
            }

            try {
                // Intentar buscar los libros por idioma
                List<Book> libros = libroSegunIdioma("[" + opcion + "]");
                if (libros.isEmpty()) {
                    System.out.println("No se encontraron libros en el idioma especificado.");
                } else {
                    libros.forEach(System.out::println);
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Idioma no válido. Intente de nuevo.");
            }
        }
    }
}*/


