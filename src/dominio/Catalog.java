package dominio;

import java.util.ArrayList;
import java.util.List;
import dominio.excepciones.POOBSoccerException;

/**
 * Lista ordenada de opciones elegibles por indice. Cada catalogo concreto crea
 * sus elementos (Creator).
 *
 * @param <T> Tipo de elemento ofrecido.
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public abstract class Catalog<T extends Named> {

    private final List<T> elementos = new ArrayList<T>();
    private final String mensajeIndiceInvalido;

    /**
     * @param mensajeIndiceInvalido Mensaje cuando se pide un indice inexistente.
     */
    protected Catalog(String mensajeIndiceInvalido) {
        this.mensajeIndiceInvalido = mensajeIndiceInvalido;
    }

    /**
     * Agrega una opcion al final del catalogo.
     *
     * @param elemento Opcion a agregar.
     */
    protected final void add(T elemento) {
        elementos.add(elemento);
    }

    /** @return Nombres de las opciones ordenadas. */
    public final String[] names() {
        String[] nombres = new String[elementos.size()];
        for (int i = 0; i < nombres.length; i++) {
            nombres[i] = elementos.get(i).getName();
        }
        return nombres;
    }

    /**
     * @param indice Indice de la opcion.
     * @return Opcion en ese indice.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public final T get(int indice) throws POOBSoccerException {
        if (indice < 0 || indice >= elementos.size()) {
            throw new POOBSoccerException(mensajeIndiceInvalido);
        }
        return elementos.get(indice);
    }
}
