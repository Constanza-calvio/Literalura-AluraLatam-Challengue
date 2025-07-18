<h1 align="center">Desafio Literalura App </h1> 


<img width="5376" height="3584" alt="mujer en bilioteca rodeada de plantas trabajando en un pc" src="https://github.com/user-attachments/assets/46d2efb7-0196-4c87-b42e-406c3802973b" />

<h2>¿De que trata este Desafio?</h2>
<p> Este Desafio, llamado "Desafío Literalura", fue realizado como parte de la formación en el programa Oracle Next Education (Oracle ONE),
consiste en crear una aplicación de consola en Java que consume la <a href="https://gutendex.com"> Api Gutendex  </a> que nos permitira realizar consultas como <i>buscar libros por título, listar libros registrados y autores registrados, 
listar autores vivos en un determinado año, listar libros por idioma y buscar libros segun el nombre del autor.</i> </p>

<h2>¿Que herramientas utilizamos para este proyecto?</h2>
<a href ="https://www.jetbrains.com/idea/"> <img width="70" height="70"  alt="intellij" src="https://github.com/user-attachments/assets/79fb377a-841b-4e70-88eb-2eba53540383"/></a> <a href ="https://dev.java"><img width="70" height="70" alt="5968282" src="https://github.com/user-attachments/assets/3516bee8-dcd8-4042-b7f3-cfd6fc36420a" /></a>  <a href ="https://www.pgadmin.org"><img width="70" height="70" alt="pgadmin4" src="https://github.com/user-attachments/assets/f7de51b6-cda2-4e34-8c83-e7dbaa8293bf"/></a> 

<h2>Caracteristicas de nuestro proyecto </h2>
<p> Una vez ejecutado nuestro codigo, nos saldra un menu con las siguientes opciones:</p>
<img width="250" height="250" alt="image_menu_console_code" src="https://github.com/user-attachments/assets/0316516b-8025-43bf-a909-c0f8f25b6053"/>

<ul> <br>
  <li> <b>Buscar Libro</b>: buscamos el nombre de neustro libro con la conexion establecida con la API GUTENDEX (en ingles), </li>
  <li> <b>Buscar Libros con el nombre del autor</b>: buscamos segun el nombre del autor, si el autor esta registrado en la API, entonces nos entregarauna lista con los id de todos los libros del autor, luego tenemos la opcion de agregar el libro que estabamos buscando al ingresar el id.</li>
  <li> <b>Listar Libros: </b> Imprime por consola todos los libros guardados en nuestra base de datos, </li>
  <li> <b>Listar autores: </b> Imprime por consola todos los autores guardados en nuestra base de datos.</li>
  <li> <b> Listar Autores vivos: </b> Imprime por consola los autores que estan vivos segun el año ingresado por la consola.</li>
  <li> <b>Listar Libros por Idioma: </b> Filtra los libros según el idioma especificado que tengamos en uestra base de datos y los muestra por consola.</li>
</ul>

<h2>Caracteristicas que deseo agregar en un futuro al proyecto </h2>
<img  align= "center" width="300" height="300" alt="updatebook" src="https://github.com/user-attachments/assets/edb8c9e7-2e07-42d1-bc57-362ac43126ec" />

<ul> <br>
  <li> <b> Mejorar metodo buscar libro</b>: Actualmente el libro solo se puede buscar al ingresar el titulo en ingles, deseo incorporar que se pueda buscar los titulos tanto en español como en ingles. </li>
  <li> <b> Buscar libros segun el idioma que deseo</b>: Actualmente el libro solo se puede buscar al ingresar el titulo o si ponemos el nombre del autor, debe ser un metodo donde yo busque el titulo y luego tenga la posibilidad de poner el idioma en el que deseo registrar mi libro. </li>
  <li> <b> Metodo que entrege una lista de los 10 libros mas descargados</b>: Se debera crear un nuevo metodo con una nueva opcion en el menu para ver esta informacion. </li>
  <li> <b> Buscar los 5 libros mas descargados de un autor en especifico </b>: Se debera crear otro metodo en donde yo ingrese el nombre del autor para que me entregue de igual manera (aun si esta registrado en la base de datos) los 5 libros mas descargados con su informacion.</li>
</ul>

