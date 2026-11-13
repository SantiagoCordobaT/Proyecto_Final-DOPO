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
 * estadisticas y poder, y conoce el orden en que entran los rivales.
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
}
