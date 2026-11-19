package dominio.efectos;

/**
 * Efecto temporal sobre un personaje (aturdimiento, congelamiento, etc.).
 * Por defecto no altera nada; cada subclase redefine lo que modifica y fija
 * su propia duracion.
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
    void tick(double deltaTimeSeconds) {
        remainingSeconds = Math.max(0.0, remainingSeconds - deltaTimeSeconds);
    }

    /** @return true si ya termino. */
    boolean isExpired() {
        return remainingSeconds <= 0.0;
    }

    /**
     * @param other Otro efecto.
     * @return true si ambos efectos son del mismo tipo (mismo nombre).
     */
    boolean isSameKindAs(Effect other) {
        return name.equals(other.name);
    }

    /** @return Nombre del efecto. */
    String getName() {
        return name;
    }

    /** @return true si el personaje puede moverse, saltar y patear. */
    boolean allowsActions() {
        return true;
    }

    /** @return Factor de escala del cuerpo (1.0 = normal). */
    double getScale() {
        return 1.0;
    }

    /** @return 1 si el movimiento horizontal es normal, -1 si esta invertido. */
    int getHorizontalSign() {
        return 1;
    }
}
