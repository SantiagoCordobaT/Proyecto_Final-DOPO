package dominio.modalidades;

import dominio.Ball;
import dominio.personajes.Character;

/**
 * Controlador automático de la máquina: persigue el balón, salta si está
 * alto, patea cuando entra en rango y arma el poder en cuanto se llena.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
class AIController extends Controller {

    private static final double DISTANCIA_DETRAS_BALON = 30.0;
    private static final double ZONA_MUERTA = 8.0;
    private static final double RANGO_SALTO_X = 120.0;
    private static final double MARGEN_BALON_ALTO = 20.0;
    private static final double RANGO_PATADA = 100.0;

    @Override
    public void controlar(Character personaje, Ball balon) {
        double objetivoX = balon.obtenerX() - personaje.obtenerOrientacion() * DISTANCIA_DETRAS_BALON;
        double distanciaX = objetivoX - personaje.obtenerX();
        if (distanciaX > ZONA_MUERTA) {
            personaje.establecerDireccionMovimiento(1);
        } else if (distanciaX < -ZONA_MUERTA) {
            personaje.establecerDireccionMovimiento(-1);
        } else {
            personaje.establecerDireccionMovimiento(0);
        }

        if (balon.obtenerY() < personaje.obtenerBordeSuperior() - MARGEN_BALON_ALTO
                && Math.abs(balon.obtenerX() - personaje.obtenerX()) < RANGO_SALTO_X) {
            personaje.saltar();
        }

        if (Math.abs(balon.obtenerX() - personaje.obtenerX()) < RANGO_PATADA
                && Math.abs(balon.obtenerY() - personaje.obtenerCentroY()) < RANGO_PATADA) {
            personaje.solicitarPatada();
        }

        personaje.armarPoder();
    }
}
