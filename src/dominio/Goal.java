package dominio;

import dominio.personajes.Character;

/**
 * Arco de la cancha. El area interior esta bajo el travesaño; el balon solo es
 * gol cuando esta completamente dentro de esa area.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class Goal {

    private static final double EPSILON = 1e-6;

    private final double leftX;
    private final double rightX;
    private final double crossbarTop;
    private final double crossbarThickness;
    private final double groundY;
    private final int mouthDirection;
    private Character defender;
    private Character attacker;

    /**
     * @param leftX Borde izquierdo del arco.
     * @param rightX Borde derecho del arco.
     * @param crossbarTop Altura (y) de la parte superior del travesaño.
     * @param crossbarThickness Grosor del travesaño.
     * @param groundY Altura del suelo.
     * @param mouthDirection 1 si la boca del arco mira a la derecha, -1 si mira a la izquierda.
     */
    public Goal(double leftX, double rightX, double crossbarTop, double crossbarThickness, double groundY,
            int mouthDirection) {
        this.leftX = leftX;
        this.rightX = rightX;
        this.crossbarTop = crossbarTop;
        this.crossbarThickness = crossbarThickness;
        this.groundY = groundY;
        this.mouthDirection = mouthDirection;
    }

    /**
     * @param ball Balon.
     * @return true solo si el balon esta completamente dentro del area del arco.
     */
    public boolean isBallInside(Ball ball) {
        double r = ball.getRadius();
        return ball.getX() - r >= leftX - EPSILON
                && ball.getX() + r <= rightX + EPSILON
                && ball.getY() - r >= crossbarTop + crossbarThickness
                && ball.getY() + r <= groundY + EPSILON;
    }

    /**
     * Rebota el balon contra el travesaño (por arriba, por debajo o de frente).
     *
     * @param ball Balon a corregir.
     */
    public void resolveCrossbar(Ball ball) {
        double r = ball.getRadius();
        double bottom = crossbarTop + crossbarThickness;
        double closestX = Math.max(leftX, Math.min(ball.getX(), rightX));
        double closestY = Math.max(crossbarTop, Math.min(ball.getY(), bottom));
        double dx = ball.getX() - closestX;
        double dy = ball.getY() - closestY;
        if (dx * dx + dy * dy >= r * r) {
            return;
        }
        double bounce = ball.getBounceFactor();
        if (ball.getY() < crossbarTop) {
            ball.setY(crossbarTop - r);
            ball.setSpeedY(-Math.abs(ball.getSpeedY()) * bounce);
        } else if (ball.getY() > bottom) {
            ball.setY(bottom + r);
            ball.setSpeedY(Math.abs(ball.getSpeedY()) * bounce);
        } else {
            double mouthEdge = mouthDirection > 0 ? rightX : leftX;
            ball.setX(mouthEdge + mouthDirection * r);
            ball.setSpeedX(mouthDirection * Math.abs(ball.getSpeedX()) * bounce);
        }
    }

    /**
     * Asigna quien defiende y quien ataca este arco.
     *
     * @param defender Dueño del arco.
     * @param attacker Rival que anota en el.
     */
    public void setPlayers(Character defender, Character attacker) {
        this.defender = defender;
        this.attacker = attacker;
    }

    /**
     * @return Personaje que defiende el arco.
     */
    public Character getDefender() {
        return defender;
    }

    /**
     * @return Personaje que anota en el arco.
     */
    public Character getAttacker() {
        return attacker;
    }

    /**
     * @return Coordenada Y de la parte superior del travesano.
     */
    public double getCrossbarTop() {
        return crossbarTop;
    }
}
