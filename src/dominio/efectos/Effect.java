package dominio.efectos;

/**
 * Efecto temporal sobre un personaje (aturdimiento, congelamiento, etc.).
 * Por defecto no altera nada; cada subclase redefine lo que modifica.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public abstract class Effect {

    private final String name;
    private double remainingSeconds;

    /**
     * @param name Nombre del efecto.
     * @param durationSeconds Duracion en segundos.
     */
    protected Effect(String name, double durationSeconds) {
        this.name = name;
        this.remainingSeconds = durationSeconds;
    }

    /**
     * Descuenta tiempo al efecto.
     *
     * @param deltaTimeSeconds Segundos transcurridos.
     */
    public void tick(double deltaTimeSeconds) {
        remainingSeconds = Math.max(0.0, remainingSeconds - deltaTimeSeconds);
    }

    /** @return true si ya termino. */
    public boolean isExpired() {
        return remainingSeconds <= 0.0;
    }

    /** @return Nombre del efecto. */
    public String getName() {
        return name;
    }

    /** @return Segundos restantes. */
    public double getRemainingSeconds() {
        return remainingSeconds;
    }

    /** @return true si el personaje puede moverse, saltar y patear. */
    public boolean allowsActions() {
        return true;
    }

    /** @return Factor de escala del cuerpo (1.0 = normal). */
    public double getScale() {
        return 1.0;
    }

    /** @return 1 si el movimiento horizontal es normal, -1 si esta invertido. */
    public int getHorizontalSign() {
        return 1;
    }
}
