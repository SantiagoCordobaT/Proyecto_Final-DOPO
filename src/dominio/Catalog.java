package dominio;

import java.util.ArrayList;
import java.util.List;

import dominio.excepciones.POOBSoccerException;

/**
 * Lista ordenada de opciones elegibles por indice. Cada catalogo concreto crea
 * sus elementos (Creator) y define el mensaje para un indice inexistente.
 *
 * @param <T> Tipo de elemento ofrecido.
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public abstract class Catalog<T extends Named> {

    private final List<T> items = new ArrayList<T>();
    private final String invalidIndexMessage;

    /** @param invalidIndexMessage Mensaje de la excepcion cuando se pide un indice inexistente. */
    protected Catalog(String invalidIndexMessage) {
        this.invalidIndexMessage = invalidIndexMessage;
    }

    /**
     * Agrega una opcion al final del catalogo.
     *
     * @param item Opcion a agregar.
     */
    protected final void add(T item) {
        items.add(item);
    }

    /** @return Nombres de las opciones, en orden. */
    public final String[] names() {
        String[] names = new String[items.size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = items.get(i).getName();
        }
        return names;
    }

    /**
     * @param index Indice de la opcion.
     * @return Opcion en ese indice.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public final T get(int index) throws POOBSoccerException {
        if (index < 0 || index >= items.size()) {
            throw new POOBSoccerException(invalidIndexMessage);
        }
        return items.get(index);
    }
}
