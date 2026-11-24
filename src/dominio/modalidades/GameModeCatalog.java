package dominio.modalidades;

import dominio.excepciones.POOBSoccerException;

/**
 * Lista con las tres modalidades de juego en el orden del menú.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class GameModeCatalog {

    private final GameMode[] modos = {
            new PvsP(),
            new PvsM(),
            new MvsM()
    };

    public String[] obtenerNombres() {
        String[] nombres = new String[modos.length];
        for (int i = 0; i < modos.length; i++) {
            nombres[i] = modos[i].obtenerNombre();
        }
        return nombres;
    }

    public GameMode obtener(int indice) throws POOBSoccerException {
        if (indice < 0 || indice >= modos.length) {
            throw new POOBSoccerException(POOBSoccerException.MODO_INVALIDO);
        }
        return modos[indice];
    }
}
