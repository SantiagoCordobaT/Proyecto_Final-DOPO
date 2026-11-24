package dominio.personajes;

import java.util.ArrayList;

import dominio.excepciones.POOBSoccerException;
import dominio.poderes.ConfusionPower;
import dominio.poderes.FreezePower;
import dominio.poderes.Power;
import dominio.poderes.ShrinkPower;
import dominio.poderes.StunPower;

/**
 * Catálogo de personajes jugables: inicia con cuatro personajes base y permite
 * crear, editar, eliminar y rotar rivales en Supervivencia.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class CharacterCatalog {

    private static final int MINIMO_PERSONAJES = 2;
    private final ArrayList<Character> personajes = new ArrayList<>();

    public CharacterCatalog() throws POOBSoccerException {
        personajes.add(new Character("Jacob", "Canadá", 3, 3, 4, 3, new FreezePower()));
        personajes.add(new Character("Kenji", "Japón", 5, 4, 2, 2, new StunPower()));
        personajes.add(new Character("Mateo", "Colombia", 4, 3, 3, 4, new ConfusionPower()));
        personajes.add(new Character("Rafael", "Brasil", 4, 5, 3, 3, new ShrinkPower()));
    }

    public int cantidad() {
        return personajes.size();
    }

    public String[] obtenerNombres() {
        String[] nombres = new String[personajes.size()];
        for (int i = 0; i < nombres.length; i++) {
            nombres[i] = personajes.get(i).obtenerNombre();
        }
        return nombres;
    }

    public Character obtener(int indice) throws POOBSoccerException {
        validarIndice(indice);
        return personajes.get(indice);
    }

    /** Crea un personaje nuevo verificando estadísticas válidas y nombre único. */
    public void crear(String nombre, String pais, int velocidad, int salto, int fuerza, int nivelPoder, Power poder)
            throws POOBSoccerException {
        personajes.add(construirPersonaje(nombre, pais, velocidad, salto, fuerza, nivelPoder, poder, -1));
    }

    /** Reemplaza un personaje existente validando índice, datos y nombre único. */
    public void editar(int indice, String nombre, String pais, int velocidad, int salto, int fuerza, int nivelPoder,
            Power poder) throws POOBSoccerException {
        validarIndice(indice);
        personajes.set(indice, construirPersonaje(nombre, pais, velocidad, salto, fuerza, nivelPoder, poder, indice));
    }

    /** Elimina un personaje dejando al menos dos disponibles para jugar. */
    public void eliminarPersonaje(int indice) throws POOBSoccerException {
        if (personajes.size() <= MINIMO_PERSONAJES) {
            throw new POOBSoccerException(POOBSoccerException.MINIMO_PERSONAJES);
        }
        validarIndice(indice);
        personajes.remove(indice);
    }

    private Character construirPersonaje(String nombre, String pais, int velocidad, int salto, int fuerza,
            int nivelPoder, Power poder, int indiceExcluido) throws POOBSoccerException {
        String nombreLimpio = nombre == null ? null : nombre.trim();
        String paisLimpio = pais == null ? null : pais.trim();
        Character nuevo = new Character(nombreLimpio, paisLimpio, velocidad, salto, fuerza, nivelPoder, poder);
        for (int i = 0; i < personajes.size(); i++) {
            if (i != indiceExcluido && personajes.get(i).obtenerNombre().equalsIgnoreCase(nuevo.obtenerNombre())) {
                throw new POOBSoccerException(POOBSoccerException.PERSONAJE_REPETIDO);
            }
        }
        return nuevo;
    }

    private void validarIndice(int indice) throws POOBSoccerException {
        if (indice < 0 || indice >= personajes.size()) {
            throw new POOBSoccerException(POOBSoccerException.PERSONAJE_INVALIDO);
        }
    }

    /**
     * Devuelve una copia del siguiente personaje en la lista (circular al final).
     */
    public Character siguienteDespuesDe(Character actual) throws POOBSoccerException {
        int indiceActual = 0;
        for (int i = 0; i < personajes.size(); i++) {
            if (personajes.get(i).obtenerNombre().equals(actual.obtenerNombre())) {
                indiceActual = i;
                break;
            }
        }
        int siguiente = (indiceActual + 1) % personajes.size();
        return personajes.get(siguiente).copiar();
    }

    public String obtenerPais(int indice) throws POOBSoccerException {
        return obtener(indice).obtenerPais();
    }

    public int obtenerVelocidad(int indice) throws POOBSoccerException {
        return obtener(indice).obtenerVelocidad();
    }

    public int obtenerSalto(int indice) throws POOBSoccerException {
        return obtener(indice).obtenerSalto();
    }

    public int obtenerFuerza(int indice) throws POOBSoccerException {
        return obtener(indice).obtenerFuerza();
    }

    public int obtenerNivelPoder(int indice) throws POOBSoccerException {
        return obtener(indice).obtenerNivelPoder();
    }

    public String obtenerNombrePoder(int indice) throws POOBSoccerException {
        return obtener(indice).obtenerNombrePoder();
    }

    public String obtenerDescripcionPoder(int indice) throws POOBSoccerException {
        return obtener(indice).obtenerDescripcionPoder();
    }
}
