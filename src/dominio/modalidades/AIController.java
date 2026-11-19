package dominio.modalidades;

import dominio.Ball;
import dominio.personajes.Character;

/**
 * IA autonoma: se ubica detras del balon segun hacia donde ataca, salta si
 * viene alto, patea en rango y arma el poder apenas la barra se llena. Ignora
 * las acciones mantenidas (el teclado no la controla).
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

    /** Se acerca al punto detras del balon, con una zona muerta para no temblar. */
    @Override
    protected int direccion(Character personaje, Ball balon) {
        double distanciaX = balon.obtenerX() - personaje.obtenerOrientacion() * DISTANCIA_DETRAS_BALON
                - personaje.obtenerX();
        return Math.abs(distanciaX) > ZONA_MUERTA ? (int) Math.signum(distanciaX) : 0;
    }

    /** Salta si el balon pasa por encima de su cabeza y esta cerca en X. */
    @Override
    protected boolean quiereSaltar(Character personaje, Ball balon) {
        return balon.obtenerY() < personaje.obtenerBordeSuperior() - MARGEN_BALON_ALTO
                && Math.abs(balon.obtenerX() - personaje.obtenerX()) < RANGO_SALTO_X;
    }

    protected boolean quierePatear(Character personaje, Ball balon) {
        return Math.abs(balon.obtenerX() - personaje.obtenerX()) < RANGO_PATADA
                && Math.abs(balon.obtenerY() - personaje.obtenerCentroY()) < RANGO_PATADA;
    }

    /** Siempre intenta armar el poder: solo se arma con la barra llena. */
    @Override
    protected boolean quierePoder() {
        return true;
    }
}
