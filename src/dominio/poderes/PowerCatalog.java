package dominio.poderes;

import dominio.Catalog;
import dominio.excepciones.POOBSoccerException;

/**
 * Catalogo de poderes disponibles (Creator): expone los poderes creables para
 * asignarlos al crear o editar un personaje.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public final class PowerCatalog extends Catalog<Power> {

    /** Crea el catalogo con los poderes del juego. */
    public PowerCatalog() {
        super(POOBSoccerException.PODER_INVALIDO);
        agregar(new FreezePower());
        agregar(new StunPower());
        agregar(new ConfusionPower());
        agregar(new ShrinkPower());
    }
}
