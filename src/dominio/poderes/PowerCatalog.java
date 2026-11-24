package dominio.poderes;

import dominio.excepciones.POOBSoccerException;

/**
 * Poderes disponibles para asignar al crear o editar un personaje.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class PowerCatalog {

    private final Power[] poderes = {
            new FreezePower(),
            new StunPower(),
            new ConfusionPower(),
            new ShrinkPower()
    };

    public String[] obtenerNombres() {
        String[] nombres = new String[poderes.length];
        for (int i = 0; i < poderes.length; i++) {
            nombres[i] = poderes[i].obtenerNombre();
        }
        return nombres;
    }

    public Power obtener(int indice) throws POOBSoccerException {
        if (indice < 0 || indice >= poderes.length) {
            throw new POOBSoccerException(POOBSoccerException.PODER_INVALIDO);
        }
        return poderes[indice];
    }
}
