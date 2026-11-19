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
 * estadisticas y poder, conoce el orden en que entran los rivales y responde los
 * datos de cada personaje que muestra la seleccion (Information Expert: quien pide
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
        add(new Character("Jacob", "Canadá", 3, 3, 4, 3, new FreezePower()));
        add(new Character("Kenji", "Japón", 5, 4, 2, 2, new StunPower()));
        add(new Character("Mateo", "Colombia", 4, 3, 3, 4, new ConfusionPower()));
        add(new Character("Rafael", "Brasil", 4, 5, 3, 3, new ShrinkPower()));
    }

    /**
     * Prototipo del rival que sigue al actual en el catalogo (vuelve al primero despues del ultimo).
     *
     * @param actual Personaje actual, copia de uno del catalogo.
     * @return Copia nueva del siguiente personaje.
     * @throws POOBSoccerException Si el catalogo no pudiera entregar el personaje.
     */
    public Character nextAfter(Character actual) throws POOBSoccerException {
        List<String> nombres = Arrays.asList(names());
        int siguiente = (nombres.indexOf(actual.getName()) + 1) % nombres.size();
        return get(siguiente).copy();
    }

    /**
     * @param indice Indice del personaje.
     * @return Pais del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public String getCountry(int indice) throws POOBSoccerException {
        return get(indice).getCountry();
    }

    /**
     * @param indice Indice del personaje.
     * @return Velocidad del personaje (1 a 5).
     * @throws POOBSoccerException Si el indice no existe.
     */
    public int getSpeed(int indice) throws POOBSoccerException {
        return get(indice).getSpeed();
    }

    /**
     * @param indice Indice del personaje.
     * @return Salto del personaje (1 a 5).
     * @throws POOBSoccerException Si el indice no existe.
     */
    public int getJump(int indice) throws POOBSoccerException {
        return get(indice).getJump();
    }

    /**
     * @param indice Indice del personaje.
     * @return Fuerza del personaje (1 a 5).
     * @throws POOBSoccerException Si el indice no existe.
     */
    public int getStrength(int indice) throws POOBSoccerException {
        return get(indice).getStrength();
    }

    /**
     * @param indice Indice del personaje.
     * @return Poder Unico del personaje (1 a 5).
     * @throws POOBSoccerException Si el indice no existe.
     */
    public int getPowerLevel(int indice) throws POOBSoccerException {
        return get(indice).getPowerLevel();
    }

    /**
     * @param indice Indice del personaje.
     * @return Nombre del poder del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public String getPowerName(int indice) throws POOBSoccerException {
        return get(indice).getPowerName();
    }

    /**
     * @param indice Indice del personaje.
     * @return Descripcion del poder del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public String getPowerDescription(int indice) throws POOBSoccerException {
        return get(indice).getPowerDescription();
    }
}
