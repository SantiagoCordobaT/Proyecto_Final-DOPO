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
     * @param current Personaje actual, copia de uno del catalogo.
     * @return Copia nueva del siguiente personaje.
     * @throws POOBSoccerException Si el catalogo no pudiera entregar el personaje.
     */
    public Character nextAfter(Character current) throws POOBSoccerException {
        List<String> names = Arrays.asList(names());
        int next = (names.indexOf(current.getName()) + 1) % names.size();
        return get(next).copy();
    }

    /**
     * @param index Indice del personaje.
     * @return Pais del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public String getCountry(int index) throws POOBSoccerException {
        return get(index).getCountry();
    }

    /**
     * @param index Indice del personaje.
     * @return Velocidad del personaje (1 a 5).
     * @throws POOBSoccerException Si el indice no existe.
     */
    public int getSpeed(int index) throws POOBSoccerException {
        return get(index).getSpeed();
    }

    /**
     * @param index Indice del personaje.
     * @return Salto del personaje (1 a 5).
     * @throws POOBSoccerException Si el indice no existe.
     */
    public int getJump(int index) throws POOBSoccerException {
        return get(index).getJump();
    }

    /**
     * @param index Indice del personaje.
     * @return Fuerza del personaje (1 a 5).
     * @throws POOBSoccerException Si el indice no existe.
     */
    public int getStrength(int index) throws POOBSoccerException {
        return get(index).getStrength();
    }

    /**
     * @param index Indice del personaje.
     * @return Poder Unico del personaje (1 a 5).
     * @throws POOBSoccerException Si el indice no existe.
     */
    public int getPowerLevel(int index) throws POOBSoccerException {
        return get(index).getPowerLevel();
    }

    /**
     * @param index Indice del personaje.
     * @return Nombre del poder del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public String getPowerName(int index) throws POOBSoccerException {
        return get(index).getPowerName();
    }

    /**
     * @param index Indice del personaje.
     * @return Descripcion del poder del personaje.
     * @throws POOBSoccerException Si el indice no existe.
     */
    public String getPowerDescription(int index) throws POOBSoccerException {
        return get(index).getPowerDescription();
    }
}
