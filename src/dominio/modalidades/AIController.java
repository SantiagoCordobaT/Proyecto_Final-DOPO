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

    private static final double BEHIND_BALL_OFFSET = 30.0;
    private static final double DEAD_ZONE = 8.0;
    private static final double JUMP_RANGE_X = 120.0;
    private static final double HIGH_BALL_MARGIN = 20.0;
    private static final double KICK_RANGE = 100.0;

    /** Se acerca al punto detras del balon, con una zona muerta para no temblar. */
    @Override
    protected int direction(Character self, Ball ball) {
        double distanceX = ball.getX() - self.getFacing() * BEHIND_BALL_OFFSET - self.getX();
        return Math.abs(distanceX) > DEAD_ZONE ? (int) Math.signum(distanceX) : 0;
    }

    /** Salta si el balon pasa por encima de su cabeza y esta cerca en X. */
    @Override
    protected boolean wantsJump(Character self, Ball ball) {
        return ball.getY() < self.getTop() - HIGH_BALL_MARGIN && Math.abs(ball.getX() - self.getX()) < JUMP_RANGE_X;
    }

    /** Pide patada si el balon esta cerca en X y en Y. */
    @Override
    protected boolean wantsKick(Character self, Ball ball) {
        return Math.abs(ball.getX() - self.getX()) < KICK_RANGE
                && Math.abs(ball.getY() - self.getCenterY()) < KICK_RANGE;
    }

    /** Siempre intenta armar el poder: solo se arma con la barra llena. */
    @Override
    protected boolean wantsPower() {
        return true;
    }
}
