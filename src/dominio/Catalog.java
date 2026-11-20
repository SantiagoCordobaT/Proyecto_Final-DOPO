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
    protected final void agregar(T elemento) {
        elementos.add(elemento);
    }

    /**
     * Reemplaza la opcion ubicada en el indice indicado.
     *
     * @param indice   Posicion existente.
     * @param elemento Nueva opcion.
     * @throws POOBSoccerException Si el indice no existe.
     */
    protected final void reemplazar(int indice, T elemento) throws POOBSoccerException {
        validarIndice(indice);
        elementos.set(indice, elemento);
    }

    /**
     * Elimina la opcion ubicada en el indice indicado.
     *
     * @param indice Posicion existente.
     * @throws POOBSoccerException Si el indice no existe.
     */
    protected final void eliminar(int indice) throws POOBSoccerException {
        validarIndice(indice);
        elementos.remove(indice);
    }

    /** @return Cantidad de opciones en el catalogo. */
    public final int cantidad() {
        return elementos.size();
    }

    /** @return Nombres de las opciones ordenadas. */
    public final String[] obtenerNombres() {
        String[] nombres = new String[elementos.size()];
        for (int i = 0; i < nombres.length; i++) {
            nombres[i] = elementos.get(i).obtenerNombre();
        }
        return nombres;
    }

    /**
     * @param indice Indice de la opcion.
     * @return Opcion en ese indice.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public final T obtener(int indice) throws POOBSoccerException {
        validarIndice(indice);
        return elementos.get(indice);
    }

    private void validarIndice(int indice) throws POOBSoccerException {
        if (indice < 0 || indice >= elementos.size()) {
            throw new POOBSoccerException(mensajeIndiceInvalido);
        }
    }
}
