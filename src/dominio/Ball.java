package dominio;

/**
 * Representa el balón de fútbol 2D en POOBSoccer.
 * Maneja la cinemática, la gravedad, la fricción del césped,
 * los rebotes en los límites del campo y la respuesta al ser pateado.
 * 
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class Ball {
    // Coordenadas espaciales y velocidades vectoriales
    private double x;
    private double y;
    private double speedX;
    private double speedY;

    // Propiedades físicas y geométricas
    private double radius;
    private double gravity;
    private double bounceFactor;
    private double weightFactor;

    /**
     * Constructor con valores físicos predeterminados y equilibrados.
     * 
     * @param startX Posición horizontal inicial (centro del campo).
     * @param startY Posición vertical inicial (altura de caída).
     * @param radius Radio en píxeles del balón.
     */
    public Ball(double startX, double startY, double radius) {
        this.x = startX;
        this.y = startY;
        this.radius = radius;
        this.speedX = 0.0;
        this.speedY = 0.0;
        this.gravity = 0.45;       // Aceleración de caída vertical
        this.bounceFactor = 0.75;  // Conserva el 75% de la velocidad en cada bote
        this.weightFactor = 1.0;   // Resistencia base al impacto
    }

    /**
     * Constructor completo por si se requiere calibrar manualmente los coeficientes físicos.
     */
    public Ball(double startX, double startY, double radius, double gravity, double bounceFactor, double weightFactor) {
        this.x = startX;
        this.y = startY;
        this.radius = radius;
        this.gravity = gravity;
        this.bounceFactor = bounceFactor;
        this.weightFactor = weightFactor;
        this.speedX = 0.0;
        this.speedY = 0.0;
    }

    /**
     * Reubica el balón en el centro superior de la cancha tras anotarse un gol
     * y cancela cualquier inercia previa para que caiga limpiamente.
     * 
     * @param centerX Coordenada horizontal del centro del campo.
     * @param dropY   Altura desde la cual se dejará caer el esférico.
     */
    public void resetPosition(double centerX, double dropY) {
        this.x = centerX;
        this.y = dropY;
        this.speedX = 0.0;
        this.speedY = 0.0;
    }

    /**
     * Actualiza la posición del balón aplicando gravedad en cada tick
     * y resolviendo colisiones contra el suelo, techo y límites laterales.
     * 
     * @param groundY     Coordenada Y de la superficie del suelo.
     * @param leftWallX   Coordenada X del límite lateral izquierdo.
     * @param rightWallX  Coordenada X del límite lateral derecho.
     * @param ceilingY    Coordenada Y del límite superior de la cancha.
     */
    public void updatePhysics(double groundY, double leftWallX, double rightWallX, double ceilingY) {
        // Aplicar aceleración gravitacional y traslación
        speedY += gravity;
        x += speedX;
        y += speedY;

        // Colisión con el suelo
        if (y + radius >= groundY) {
            y = groundY - radius;                  // Corrección para no atravesar el piso
            speedY = -speedY * bounceFactor;       // Inversión vertical con amortiguación
            speedX = speedX * 0.98;                // Fricción por rozamiento con el césped

            // Zona muerta: evita temblor continuo cuando el bote es leve
            if (Math.abs(speedY) < 0.5) {
                speedY = 0.0;
            }
        }

        // Colisión con el techo
        if (y - radius <= ceilingY) {
            y = ceilingY + radius;
            speedY = -speedY * bounceFactor;
        }

        // Colisión con el borde lateral izquierdo
        if (x - radius <= leftWallX) {
            x = leftWallX + radius;
            speedX = -speedX * bounceFactor;
        }

        // Colisión con el borde lateral derecho
        if (x + radius >= rightWallX) {
            x = rightWallX - radius;
            speedX = -speedX * bounceFactor;
        }
    }

    /**
     * Aplica el impacto de una patada al balón considerando la fuerza del jugador,
     * la dirección hacia donde ataca y el peso del esférico.
     * 
     * @param characterStrength Estadística de fuerza del personaje (de 1 a 5).
     * @param directionX        Sentido horizontal: 1 (hacia la derecha) o -1 (hacia la izquierda).
     */
    public void kick(int characterStrength, int directionX) {
        // Validación de la fuerza del personaje
        if (characterStrength < 1) characterStrength = 1;
        if (characterStrength > 5) characterStrength = 5;

        // Cálculo de la velocidad por la patada
        double kickSpeed = characterStrength * 2.0; // Escala lineal de fuerza

        // Aplicación de la dirección y ajuste por peso del balón
        speedX += directionX * kickSpeed / weightFactor;
        speedY -= kickSpeed / weightFactor; // La patada también levanta el balón
    }

    // Métodos de Consulta (Getters)
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRadius() {
        return radius;
    }

    public double getSpeedX() {
        return speedX;
    }

    public double getSpeedY() {
        return speedY;
    }

    public double getGravity() {
        return gravity;
    }

    public double getBounceFactor() {
        return bounceFactor;
    }

    public double getWeightFactor() {
        return weightFactor;
    }

    // Metodos de modificacion usados por la cancha, los arcos y la fachada del dominio
    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setSpeedX(double speedX) {
        this.speedX = speedX;
    }

    public void setSpeedY(double speedY) {
        this.speedY = speedY;
    }
}
