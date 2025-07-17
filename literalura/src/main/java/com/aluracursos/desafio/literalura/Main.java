package com.aluracursos.desafio.literalura;

import com.aluracursos.desafio.literalura.Dto.AuthorData;
import com.aluracursos.desafio.literalura.Dto.BooksData;
import com.aluracursos.desafio.literalura.Dto.BooksDataContainer;
import com.aluracursos.desafio.literalura.Dto.Idioma;
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
// NO LOGRA BUSCAR LOS LIBROS POR TITULO ===== LOGRADO Y NO TENEMOS PROBLEMAS CON REPETICION DE AUTOR :D
// TAMPOCO ME MUESTRA LA LISTA DE LIBROS REGISTRADOS  ====  logrado  y agrergamos otro meodo para buscar liubros segun el autor
// ORDENAR VIZUALIZACION DE DATOS AL MOSTRAR AUTORES REGISTRADOS === LISTOOO
// FALTA METODO 5 == METODO FUNCIUONANDO SIN NIGNUN PROBLEMA
// FALTA METODO 6 == METODO FUNCIONA SIN NINGUN PROBLEMA

//ADICIONES Al CODIGOOO
// INCORPORAR QUE AL BUSCAR LIBROS ME ENTIENDA EL TITULO EN INGLES O EN ESPAÑOL
// INCORPORAR METODO QUE ME BUSQUE LIBROS SEWGUN EL IDIMA QUE YO QUIERO , LUEGO QUE ME CONSULTE DE AUTOR O TITULO ESTOY BUSCANDOI Y HAGA LA CONSULTA EN LA API
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
                    2 - Buscar libros por Autor 
                    3 - Mostrar libros registrados
                    4 - Mostrar autores registrados
                    5 - Autores vivos en determinado año
                    6 - Buscar libros por idioma
                    7 - Salir
                    
                    
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
                    buscarLibrosPorAutor();
                    break;
                case 3:
                    mostrarLibrosHistorial();
                    break;
                case 4:
                    mostrarAutoresRegistrados();
                    break;
                case 5:
                    autoresVivosPorAnio();
                    break;
                case 6:
                    buscarLibrosPorIdioma();
                    break;
                case 7:
                    salir();
                case 0:
                    System.out.println("¡Gracias por usar nuestra App, Nos vemos pronto!");
                    break;
                default:
                    System.out.print("Opción inválida\n");
            }
        }

    }

    private void salir() {
        System.out.println("¡Gracias por usar nuestra App, Nos vemos pronto!");
    }

    private BooksData booksData() {
        System.out.println("¿Cual es el nombre del libro que estas buscando?");
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
    private void buscarLibroPorTitulo() {
        System.out.println("Por favor ingrese el nombre del libro que desea buscar: ");
        var tituloBuscado = teclado.nextLine();

        Optional<Book> libroEnBD = bookRepo.findByTitulo(tituloBuscado);

        if (libroEnBD.isPresent()) {
            System.out.println("Parece que ya has consultado por este título. ¡Aquí te muestro los datos!");
            System.out.println(libroEnBD.get());
            return;
        }

        try {
            System.out.println("Buscando el libro en la API...");

            String jsonResponse = consumeApi.obtainData(URL_BASE + "?search=" + tituloBuscado.replace(" ", "%20"));
            ObjectMapper mapper = new ObjectMapper();
            BooksDataContainer contenedor = mapper.readValue(jsonResponse, BooksDataContainer.class);

            Optional<BooksData> libroEncontrado = contenedor.results().stream()
                    .filter(libro -> libro.titulo().equalsIgnoreCase(tituloBuscado))
                    .findFirst();

            if (libroEncontrado.isPresent()) {
                BooksData data = libroEncontrado.get();

                // Normaliza el nombre del autor desde la API
                String nombreAutorApi = data.autor().isEmpty() ? "Autor Desconocido" : normalizarNombreAutor(data.autor().get(0).nombre());

                // Trae todos los autores y busca uno que coincida con nombre normalizado
                List<Author> autoresRegistrados = authorRepo.findAll();
                Optional<Author> autorExistente = autoresRegistrados.stream()
                        .filter(a -> normalizarNombreAutor(a.getNombre()).equalsIgnoreCase(nombreAutorApi))
                        .findFirst();

                Author autor;
                if (autorExistente.isPresent()) {
                    autor = autorExistente.get();
                } else {
                    autor = new Author();
                    autor.setNombre(nombreAutorApi);
                    autor.setFechaNacimiento(data.autor().isEmpty() ? null : data.autor().get(0).fechaNacimiento());
                    autor.setFechaDefuncion(data.autor().isEmpty() ? null : data.autor().get(0).fechaDefuncion());
                    authorRepo.save(autor);
                }

                Book nuevoLibro = new Book(data);
                nuevoLibro.setAutor(autor);
                bookRepo.save(nuevoLibro);

                System.out.println("Libro encontrado en la API y guardado en la base de datos:");
                System.out.println(nuevoLibro);

            } else {
                System.out.println("No se encontró ningún libro con el título: " + tituloBuscado + " en la API.");
            }

        } catch (Exception e) {
            System.err.println("Error al buscar el libro en la API: " + e.getMessage());
        }
    }



    //METODO PARA BUSCAR AUTOR NOMBRE SIN PROBLEMAS PARA APLICARE AL OTRO METODO DE ABAJO
    private boolean autorCoincide(String nombreDesdeApi, String nombreUsuario) {
        String apiNombre = nombreDesdeApi.toLowerCase().replace(",", "").trim(); // austen jane
        String usuarioNombre = nombreUsuario.toLowerCase().trim(); // jane austen

        // Separar por espacios
        String[] partes = usuarioNombre.split(" ");
        if (partes.length == 2) {
            String nombre = partes[0]; // jane
            String apellido = partes[1]; // austen
            String invertido = apellido + " " + nombre; // austen jane

            return apiNombre.contains(usuarioNombre) || apiNombre.contains(invertido);
        }
        return apiNombre.contains(usuarioNombre);
    }

    private String normalizarNombreAutor(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            return "Autor Desconocido";
        }
        nombre = nombre.trim();
        if (nombre.contains(",")) {
            String[] partes = nombre.split(",");
            if (partes.length == 2) {
                // Invierte "Apellido, Nombre" a "Nombre Apellido"
                return partes[1].trim() + " " + partes[0].trim();
            }
        }
        return nombre;
    }

    private String normalizarNombreUsuario(String nombreUsuario) {
        return nombreUsuario.trim().toLowerCase();
    }

    private void buscarLibrosPorAutor() {
        System.out.println("Por favor ingrese el nombre del autor que desea buscar:");
        String nombreAutorIngresado = teclado.nextLine().trim();

        // Normaliza la entrada del usuario para comparación
        String nombreUsuarioNormalizado = normalizarNombreUsuario(nombreAutorIngresado);

        // Buscar autor en base de datos comparando nombre normalizado
        Optional<Author> autorExistente = authorRepo.findAll().stream()
                .filter(a -> {
                    // Normaliza el nombre del autor de la BD para comparar
                    String nombreAutorBD = normalizarNombreUsuario(normalizarNombreAutor(a.getNombre()));
                    return nombreAutorBD.equals(nombreUsuarioNormalizado);
                })
                .findFirst();

        if (autorExistente.isPresent()) {
            List<Book> libros = bookRepo.findByAutor(autorExistente.get());
            if (!libros.isEmpty()) {
                System.out.println("Estos son los libros que ya has consultado de este autor:");
                libros.forEach(System.out::println);
                return;
            }
        }

        // Buscar en API si no está en BD
        try {
            System.out.println("Buscando libros del autor en la API...");
            String jsonResponse = consumeApi.obtainData(URL_BASE + "?search=" + nombreAutorIngresado.replace(" ", "%20"));
            ObjectMapper mapper = new ObjectMapper();
            BooksDataContainer contenedor = mapper.readValue(jsonResponse, BooksDataContainer.class);

            // Filtrar resultados por autor coincidente usando tu función autorCoincide
            List<BooksData> librosAutor = contenedor.results().stream()
                    .filter(libro -> libro.autor().stream()
                            .anyMatch(a -> autorCoincide(a.nombre(), nombreAutorIngresado)))
                    .toList();

            if (librosAutor.isEmpty()) {
                System.out.println("No se encontraron libros de " + nombreAutorIngresado + " en la API.");
                return;
            }

            System.out.println("Libros encontrados del autor \"" + nombreAutorIngresado + "\":");
            for (BooksData libro : librosAutor) {
                String idioma = libro.idiomas().isEmpty() ? "Idioma desconocido" : libro.idiomas().get(0);
                System.out.println("- " + libro.titulo() + " [Idioma: " + idioma + ", ID: " + libro.id() + "]");
            }

            System.out.println("\nPor favor ingresa el ID del libro que deseas consultar:");
            int idSeleccionado = Integer.parseInt(teclado.nextLine().trim());

            Optional<BooksData> libroSeleccionado = librosAutor.stream()
                    .filter(libro -> libro.id() == idSeleccionado)
                    .findFirst();

            if (libroSeleccionado.isEmpty()) {
                System.out.println("No se encontró un libro con ese ID entre los resultados.");
                return;
            }

            BooksData data = libroSeleccionado.get();

            // Normalizar nombre autor API para búsqueda en BD
            String nombreAutorApi = data.autor().isEmpty() ? "Autor Desconocido" : normalizarNombreAutor(data.autor().get(0).nombre());

            // Buscar o crear autor con nombre normalizado
            Author autor = authorRepo.findAll().stream()
                    .filter(a -> {
                        String nombreBD = normalizarNombreUsuario(normalizarNombreAutor(a.getNombre()));
                        String nombreApi = normalizarNombreUsuario(nombreAutorApi);
                        return nombreBD.equals(nombreApi);
                    })
                    .findFirst()
                    .orElseGet(() -> {
                        Author nuevo = new Author();
                        nuevo.setNombre(nombreAutorApi);
                        nuevo.setFechaNacimiento(data.autor().isEmpty() ? null : data.autor().get(0).fechaNacimiento());
                        nuevo.setFechaDefuncion(data.autor().isEmpty() ? null : data.autor().get(0).fechaDefuncion());
                        return authorRepo.save(nuevo);
                    });

            // Crear y guardar el libro con el autor ya normalizado
            Book nuevoLibro = new Book(data);
            nuevoLibro.setAutor(autor);
            bookRepo.save(nuevoLibro);

            System.out.println("\nInformación detallada del libro seleccionado:");
            System.out.println(nuevoLibro);

        } catch (Exception e) {
            System.err.println("Error al buscar libros del autor: " + e.getMessage());
        }
    }





    //METODO PARA MOSTRAR LIBROS REGISTRADOS
    private void mostrarLibrosHistorial() {
        System.out.println("Encontramos tu historial de consulta! ↓↓↓↓");
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
                .sorted(Comparator.comparing(Author::getNombre))
                .forEach(author -> {
                    String salida = String.format("Autor: %s | Nacido: %s | Fallecido: %s",
                            author.getNombre(),
                            author.getFechaNacimiento() != null ? author.getFechaNacimiento() : "Desconocido",
                            author.getFechaDefuncion() != null ? author.getFechaDefuncion() : "Desconocido");
                    System.out.println(salida);

                    System.out.println("Libros registrados de " + author.getNombre() + ":");
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

    private List<Book> libroSegunIdioma(String idioma) {
        Idioma dato = Idioma.fromString(idioma);
        System.out.println("Lenguaje buscado: " + dato);
        return bookRepo.findByIdiomas(dato);
    }

    //METODO PARA BUSCAR LIBROS SEGUN IDIOMA
    private void buscarLibrosPorIdioma() {
        System.out.println("Ingresa el idioma para buscar en suus registros que libros coinciden");

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
                    System.out.println("**************No se encontraron libros en el idioma especificado dentro de tus registros.**************");
                } else {
                    System.out.println("**************Estos son los libros en tus registros que coinciden con el idioma *************");
                    libros.forEach(System.out::println);
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Idioma no válido. Intente de nuevo poniendo el codigo de idioma (en,es,fr,pt o salir).");
            }
        }
    }
}


