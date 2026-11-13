package dominio;

/**
 * Representa el balón de fútbol 2D en POOBSoccer (Experto en información):
 * maneja su cinemática, la gravedad, la fricción del césped, los rebotes en los
 * límites del campo y su propia respuesta a patadas, empujes y travesaños.
 * Avanza en pasos fijos de física (velocidades en píxeles por paso).
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class Ball {

    static final double RADIUS = 15.0;

    // Aceleración de caída vertical por paso de física
    private static final double GRAVITY = 0.45;
    // Conserva el 75% de la velocidad en cada bote
    private static final double BOUNCE = 0.75;
    // Fricción por rozamiento con el césped
    private static final double GROUND_FRICTION = 0.98;
    // Zona muerta: bajo esta velocidad vertical el balón deja de botar
    private static final double REST_SPEED = 0.5;
    private static final double KICK_SPEED_PER_STRENGTH = 2.0;
    // Velocidad lateral mínima con la que sale el balón al ser empujado por un cuerpo
    private static final double PUSH_SPEED = 3.0;
    // Desde esta rapidez el balón aturde al personaje que golpea
    private static final double HARD_HIT_SPEED = 12.0;
    private static final double HARD_HIT_SLOWDOWN = 0.6;
    private static final double EPSILON = 1e-6;

    // Coordenadas espaciales y velocidades vectoriales
    private double x;
    private double y;
    private double speedX;
    private double speedY;

    /**
     * Ubica el balón en un punto y cancela cualquier inercia previa para que caiga limpiamente.
     *
     * @param x Coordenada horizontal del centro.
     * @param y Coordenada vertical del centro.
     */
    void placeAt(double x, double y) {
        this.x = x;
        this.y = y;
        this.speedX = 0.0;
        this.speedY = 0.0;
    }

    /**
     * Actualiza la posición del balón aplicando gravedad en cada paso
     * y resolviendo colisiones contra el suelo, techo y límites laterales.
     *
     * @param groundY     Coordenada Y de la superficie del suelo.
     * @param leftWallX   Coordenada X del límite lateral izquierdo.
     * @param rightWallX  Coordenada X del límite lateral derecho.
     * @param ceilingY    Coordenada Y del límite superior de la cancha.
     */
    void updatePhysics(double groundY, double leftWallX, double rightWallX, double ceilingY) {
        speedY += GRAVITY;
        x += speedX;
        y += speedY;

        if (y + RADIUS >= groundY) {
            y = groundY - RADIUS;
            speedY = -speedY * BOUNCE;
            speedX = speedX * GROUND_FRICTION;
            if (Math.abs(speedY) < REST_SPEED) {
                speedY = 0.0;
            }
        }
        if (y - RADIUS <= ceilingY) {
            y = ceilingY + RADIUS;
            speedY = -speedY * BOUNCE;
        }
        bounceOffWalls(leftWallX, rightWallX);
    }

    /**
     * Rebota contra las paredes laterales si el balón las toca o las pasó, y lo deja dentro de ellas.
     *
     * @param leftWallX   Coordenada X del límite lateral izquierdo.
     * @param rightWallX  Coordenada X del límite lateral derecho.
     */
    void bounceOffWalls(double leftWallX, double rightWallX) {
        if (x - RADIUS <= leftWallX) {
            x = leftWallX + RADIUS;
            speedX = -speedX * BOUNCE;
        }
        if (x + RADIUS >= rightWallX) {
            x = rightWallX - RADIUS;
            speedX = -speedX * BOUNCE;
        }
    }

    /**
     * Aplica el impacto de una patada: empuja en la dirección de ataque y levanta el balón.
     *
     * @param strength  Estadística de fuerza del personaje (de 1 a 5).
     * @param direction Sentido horizontal: 1 (hacia la derecha) o -1 (hacia la izquierda).
     */
    public void kick(int strength, int direction) {
        double kickSpeed = strength * KICK_SPEED_PER_STRENGTH;
        speedX += direction * kickSpeed;
        speedY -= kickSpeed;
    }

    /**
     * @param left   Borde izquierdo del rectángulo.
     * @param top    Borde superior del rectángulo.
     * @param right  Borde derecho del rectángulo.
     * @param bottom Borde inferior del rectángulo.
     * @return true si el balón toca o se solapa con el rectángulo.
     */
    public boolean overlaps(double left, double top, double right, double bottom) {
        double dx = x - Math.max(left, Math.min(x, right));
        double dy = y - Math.max(top, Math.min(y, bottom));
        return dx * dx + dy * dy <= RADIUS * RADIUS;
    }

    /**
     * @param left   Borde izquierdo del rectángulo.
     * @param top    Borde superior del rectángulo.
     * @param right  Borde derecho del rectángulo.
     * @param bottom Borde inferior del rectángulo.
     * @return true solo si el balón está completamente dentro del rectángulo.
     */
    boolean isInside(double left, double top, double right, double bottom) {
        return x - RADIUS >= left - EPSILON
                && x + RADIUS <= right + EPSILON
                && y - RADIUS >= top - EPSILON
                && y + RADIUS <= bottom + EPSILON;
    }

    /**
     * @param surfaceY Altura de referencia.
     * @return true si el centro del balón está por encima de esa altura.
     */
    public boolean isAbove(double surfaceY) {
        return y < surfaceY;
    }

    /**
     * @param surfaceY Altura de referencia.
     * @return true si el centro del balón está por debajo de esa altura.
     */
    boolean isBelow(double surfaceY) {
        return y > surfaceY;
    }

    /**
     * Rebota hacia arriba sobre una superficie (cabeza de un personaje o travesaño).
     *
     * @param surfaceY Altura de la superficie.
     */
    public void bounceUp(double surfaceY) {
        y = surfaceY - RADIUS;
        speedY = -Math.abs(speedY) * BOUNCE;
    }

    /**
     * Rebota hacia abajo bajo una superficie (parte inferior del travesaño).
     *
     * @param surfaceY Altura de la superficie.
     */
    void bounceDown(double surfaceY) {
        y = surfaceY + RADIUS;
        speedY = Math.abs(speedY) * BOUNCE;
    }

    /**
     * Rebota horizontalmente contra un borde vertical (frente del travesaño).
     *
     * @param edgeX     Coordenada X del borde.
     * @param direction Sentido de salida: 1 derecha, -1 izquierda.
     */
    void bounceSideways(double edgeX, int direction) {
        x = edgeX + direction * RADIUS;
        speedX = direction * Math.abs(speedX) * BOUNCE;
    }

    /**
     * Sale empujado por un cuerpo hacia un lado, con al menos la velocidad de empuje.
     *
     * @param edgeX     Coordenada X del borde del cuerpo que empuja.
     * @param direction Sentido de salida: 1 derecha, -1 izquierda.
     */
    public void pushSideways(double edgeX, int direction) {
        x = edgeX + direction * RADIUS;
        speedX = direction * Math.max(Math.abs(speedX), PUSH_SPEED);
    }

    /**
     * @param referenceX Coordenada X de referencia.
     * @return 1 si el balón está a la derecha de la referencia (o justo sobre ella), -1 si está a la izquierda.
     */
    public int sideOf(double referenceX) {
        return x >= referenceX ? 1 : -1;
    }

    /** @return true si el balón va lo bastante rápido para aturdir a quien golpea. */
    boolean isHardHit() {
        return Math.sqrt(speedX * speedX + speedY * speedY) >= HARD_HIT_SPEED;
    }

    /** Frena el balón tras golpear con fuerza a un personaje. */
    void slowDown() {
        speedX *= HARD_HIT_SLOWDOWN;
        speedY *= HARD_HIT_SLOWDOWN;
    }

    /** @return Coordenada horizontal del centro. */
    public double getX() {
        return x;
    }

    /** @return Coordenada vertical del centro. */
    public double getY() {
        return y;
    }
}
