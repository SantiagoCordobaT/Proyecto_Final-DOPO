package dominio.modalidades;

import dominio.Ball;
import dominio.Field;
import dominio.excepciones.POOBSoccerException;
import dominio.personajes.Character;

/**
 * IA autonoma: persigue el balon en X, salta si viene alto, patea en rango
 * y activa el poder cuando la barra esta llena.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class AIController implements Controller {

    public static final double BEHIND_BALL_OFFSET = 30.0;
    public static final double DEAD_ZONE = 8.0;
    public static final double JUMP_RANGE_X = 120.0;
    public static final double HIGH_BALL_MARGIN = 20.0;
    public static final double KICK_RANGE = 100.0;

    @Override
    public void control(Character self, Character opponent, Ball ball, Field field) throws POOBSoccerException {
        double targetX = ball.getX() - self.getFacing() * BEHIND_BALL_OFFSET;
        double distanceX = targetX - self.getX();
        if (distanceX > DEAD_ZONE) {
            self.setMoveDirection(1);
        } else if (distanceX < -DEAD_ZONE) {
            self.setMoveDirection(-1);
        } else {
            self.setMoveDirection(0);
        }

        double ballDx = Math.abs(ball.getX() - self.getX());
        double ballDy = Math.abs(ball.getY() - (self.getTop() + self.getHeight() / 2.0));

        if (ball.getY() < self.getTop() - HIGH_BALL_MARGIN && ballDx < JUMP_RANGE_X) {
            self.jump(field.getGroundY());
        }
        if (ballDx < KICK_RANGE && ballDy < KICK_RANGE) {
            self.requestKick();
        }
        if (self.isPowerReady() && !self.isPowerActive()) {
            self.activatePower();
        }
    }
}
