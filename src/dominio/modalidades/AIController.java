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
    protected int direction(Character personaje, Ball balon) {
        double distanciaX = balon.getX() - personaje.getFacing() * DISTANCIA_DETRAS_BALON - personaje.getX();
        return Math.abs(distanciaX) > ZONA_MUERTA ? (int) Math.signum(distanciaX) : 0;
    }

    /** Salta si el balon pasa por encima de su cabeza y esta cerca en X. */
    @Override
    protected boolean wantsJump(Character personaje, Ball balon) {
        return balon.getY() < personaje.getTop() - MARGEN_BALON_ALTO
                && Math.abs(balon.getX() - personaje.getX()) < RANGO_SALTO_X;
    }

    

    /** Pide patada si el balon esta cerca en X y en Y. */
    @Override
    protected boolean wantsKick(Character personaje, Ball balon) {
        return Math.abs(balon.getX() - personaje.getX()) < RANGO_PATADA
                && Math.abs(balon.getY() - personaje.getCenterY()) < RANGO_PATADA;
    }

    /** Siempre intenta armar el poder: solo se arma con la barra llena. */
    @Override
    protected boolean wantsPower() {
        return true;
    }
}
