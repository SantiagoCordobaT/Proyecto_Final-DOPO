package dominio;

/**
 * Elemento como opcion en un catalogo (modalidades, tipos de partido y
 * personajes), el catalogo no requiere
 * saber la clase de la implementacion. Named evita repetir logica de obtener el
 * nombre visible de cada elemento y permite que el catalogo sea generico.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public interface Named {

    /** @return Nombre visible del elemento. */
    String getName();
}
