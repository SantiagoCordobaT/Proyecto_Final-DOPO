package dominio.personajes;

import java.util.Arrays;
import java.util.List;

import dominio.Catalog;
import dominio.excepciones.POOBSoccerException;
import dominio.poderes.ConfusionPower;
import dominio.poderes.FreezePower;
import dominio.poderes.ShrinkPower;
import dominio.poderes.StunPower;

/**
 * Catalogo de personajes jugables (Creator): crea los cuatro personajes con sus
 * estadisticas y poder, conoce el orden en que entran los rivales y responde
 * los
 * datos de cada personaje que muestra la seleccion (Information Expert: quien
 * pide
 * un dato no navega hasta el personaje).
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public final class CharacterCatalog extends Catalog<Character> {

    /**
     * Crea el catalogo con Jacob, Kenji, Mateo y Rafael.
     *
     * @throws POOBSoccerException Si algun personaje tuviera datos invalidos.
     */
    public CharacterCatalog() throws POOBSoccerException {
        super(POOBSoccerException.PERSONAJE_INVALIDO);
        agregar(new Character("Jacob", "Canadá", 3, 3, 4, 3, new FreezePower()));
        agregar(new Character("Kenji", "Japón", 5, 4, 2, 2, new StunPower()));
        agregar(new Character("Mateo", "Colombia", 4, 3, 3, 4, new ConfusionPower()));
        agregar(new Character("Rafael", "Brasil", 4, 5, 3, 3, new ShrinkPower()));
    }

    /**
     * Prototipo del rival que sigue al actual en el catalogo (vuelve al primero
     * despues del ultimo).
     * 
     * 
     * @param actual Personaje actual, copia de uno del catalogo.
     * @return Copia nueva del siguiente personaje.
     * @throws POOBSoccerException Si el catalogo no pudiera entregar el personaje.
     */
    public Character siguienteDespuesDe(Character actual) throws POOBSoccerException {
        List<String> nombres = Arrays.asList(obtenerNombres());
        int siguiente = (nombres.indexOf(actual.obtenerNombre()) + 1) % nombres.size();
        return obtener(siguiente).copiar();
    }

    /**
     * @param indice Indice del personaje.
     * @return Pais del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public String obtenerPais(int indice) throws POOBSoccerException {
        return obtener(indice).obtenerPais();
    }

    /**
     * @param indice Indice del personaje.
     * @return Velocidad del personaje (1 a 5).
     * @throws POOBSoccerException Si el indice no existe.
     */
    public int obtenerVelocidad(int indice) throws POOBSoccerException {
        return obtener(indice).obtenerVelocidad();
    }

    /**
     * @param indice Indice del personaje.
     * @return Salto del personaje (1 a 5).
     * @throws POOBSoccerException Si el indice no existe.
     */
    public int obtenerSalto(int indice) throws POOBSoccerException {
        return obtener(indice).obtenerSalto();
    }

    /**
     * @param indice Indice del personaje.
     * @return Fuerza del personaje (1 a 5).
     * @throws POOBSoccerException Si el indice no existe.
     */
    public int obtenerFuerza(int indice) throws POOBSoccerException {
        return obtener(indice).obtenerFuerza();
    }

    /**
     * @param indice Indice del personaje.
     * @return Poder Unico del personaje (1 a 5).
     * @throws POOBSoccerException Si el indice no existe.
     */
    public int obtenerNivelPoder(int indice) throws POOBSoccerException {
        return obtener(indice).obtenerNivelPoder();
    }

    /**
     * @param indice Indice del personaje.
     * @return Nombre del poder del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public String obtenerNombrePoder(int indice) throws POOBSoccerException {
        return obtener(indice).obtenerNombrePoder();
    }

    /**
     * @param indice Indice del personaje.
     * @return Descripcion del poder del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public String obtenerDescripcionPoder(int indice) throws POOBSoccerException {
        return obtener(indice).obtenerDescripcionPoder();
    }
}
