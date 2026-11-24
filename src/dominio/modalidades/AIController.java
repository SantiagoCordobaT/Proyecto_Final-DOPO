package dominio.modalidades;

import dominio.Ball;
import dominio.Field;
import dominio.personajes.Character;

/**
 * Controla un personaje manejado por la máquina: se ubica detrás del balón para
 * patearlo hacia el arco contrario y salta solo cuando el balón está alto en el
 * mediocampo.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
class AIController extends Controller {

    private static final double DISTANCIA_DETRAS_BALON = 45.0;
    private static final double ZONA_MUERTA = 8.0;
    private static final double MARGEN_ARCO = 150.0;
    private static final double RANGO_SALTO_X = 70.0;
    private static final double MARGEN_BALON_ALTO = 55.0;
    private static final double RANGO_PATADA = 95.0;

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

        boolean enMediocampo = balon.obtenerX() > MARGEN_ARCO && balon.obtenerX() < Field.ANCHO - MARGEN_ARCO;
        if (enMediocampo && balon.obtenerY() < personaje.obtenerBordeSuperior() - MARGEN_BALON_ALTO
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
