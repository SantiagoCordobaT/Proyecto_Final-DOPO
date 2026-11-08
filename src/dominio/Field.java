package dominio;

/**
 * Cancha 2D en vista lateral con sus limites y dos arcos.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class Field {

    public static final double WIDTH = 900.0;
    public static final double GROUND_Y = 450.0;
    public static final double CEILING_Y = 0.0;
    public static final double GOAL_WIDTH = 90.0;
    public static final double GOAL_HEIGHT = 140.0;
    public static final double CROSSBAR_THICKNESS = 10.0;
    public static final double BALL_RADIUS = 15.0;
    public static final double BALL_DROP_Y = 60.0;
    public static final double PLAYER1_START_X = 200.0;
    public static final double PLAYER2_START_X = 700.0;

    private final double groundY;
    private final double ceilingY;
    private final double leftWallX;
    private final double rightWallX;
    private final Goal leftGoal;
    private final Goal rightGoal;

    /** Crea la cancha con las medidas estandar. */
    public Field() {
        this.groundY = GROUND_Y;
        this.ceilingY = CEILING_Y;
        this.leftWallX = 0.0;
        this.rightWallX = WIDTH;
        double crossbarTop = groundY - GOAL_HEIGHT;
        this.leftGoal = new Goal(leftWallX, leftWallX + GOAL_WIDTH, crossbarTop, CROSSBAR_THICKNESS, groundY, 1);
        this.rightGoal = new Goal(rightWallX - GOAL_WIDTH, rightWallX, crossbarTop, CROSSBAR_THICKNESS, groundY, -1);
    }

    /**
     * Avanza el balon un paso de fisica incluyendo el rebote en los travesaños.
     *
     * @param ball Balon.
     */
    public void updateBall(Ball ball) {
        ball.updatePhysics(groundY, leftWallX, rightWallX, ceilingY);
        leftGoal.resolveCrossbar(ball);
        rightGoal.resolveCrossbar(ball);
    }

    /** @return Coordenada horizontal del centro de la cancha. */
    public double getCenterX() {
        return (leftWallX + rightWallX) / 2.0;
    }

    /**
     * @return Coordenada Y del suelo.
     */
    public double getGroundY() {
        return groundY;
    }

    /**
     * @return Coordenada Y del techo.
     */
    public double getCeilingY() {
        return ceilingY;
    }

    /**
     * @return Coordenada X del borde izquierdo.
     */
    public double getLeftWallX() {
        return leftWallX;
    }

    /**
     * @return Coordenada X del borde derecho.
     */
    public double getRightWallX() {
        return rightWallX;
    }

    /**
     * @return Arco izquierdo (defendido por el jugador 1).
     */
    public Goal getLeftGoal() {
        return leftGoal;
    }

    /**
     * @return Arco derecho (defendido por el jugador 2).
     */
    public Goal getRightGoal() {
        return rightGoal;
    }
}
