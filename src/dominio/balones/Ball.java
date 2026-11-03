package balones;

public class Ball {
    protected String name;
    protected double x, y;
    protected double speedX, speedY;
    protected double radius;
    protected double gravity;
    protected double bounceFactor;
    protected double weightFactor;

    public Ball(String name, double x, double y, double radius, double gravity, double bounceFactor, double weightFactor) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.gravity = gravity;
        this.bounceFactor = bounceFactor;
        this.weightFactor = weightFactor;
        this.speedX = 0;
        this.speedY = 0;
    }

    public void resetPosition(double xCenter, double yCenter) {
        this.x = xCenter;
        this.y = yCenter;
        this.speedX = 0;
        this.speedY = 0;
    }

    // Actualiza la posición del balón (Rebote en el suelo, Rebote en el techo y Rebote en bordes laterales)


    // Recibe una patada según la estadística de fuerza (1 a 5) del personaje

    // Método abstracto para clonar el tipo de balón

    // Métodos de consulta (Getters)
}
