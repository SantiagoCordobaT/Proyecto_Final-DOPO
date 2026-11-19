package dominio.efectos;

/**
 * Efecto temporal sobre un personaje.
 * Por defecto no altera nada; cada subclase redefine lo que modifica y fija
 * su propia duracion.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public abstract class Effect {

    private final String nombre;
    private double segundosRestantes;

    /**
     * @param nombre           Nombre del efecto.
     * @param duracionSegundos Duracion en segundos.
     */
    protected Effect(String nombre, double duracionSegundos) {
        this.nombre = nombre;
        this.segundosRestantes = duracionSegundos;
    }

    /**
     * Descuenta tiempo al efecto.
     *
     * @param segundosTranscurridos Segundos transcurridos.
     */
    void transcurrir(double segundosTranscurridos) {
        segundosRestantes = Math.max(0.0, segundosRestantes - segundosTranscurridos);
    }

    /** @return true si ya termino. */
    boolean haExpirado() {
        return segundosRestantes <= 0.0;
    }

    /**
     * @param otro Otro efecto.
     * @return true si ambos efectos son del mismo tipo (mismo nombre).
     */
    boolean esMismoTipoQue(Effect otro) {
        return nombre.equals(otro.nombre);
    }

    /** @return Nombre del efecto. */
    String obtenerNombre() {
        return nombre;
    }

    /** @return true si el personaje puede moverse, saltar y patear. */
    boolean permiteAcciones() {
        return true;
    }

    /** @return Factor de escala del cuerpo (1.0 = normal). */
    double obtenerEscala() {
        return 1.0;
    }

    /** @return 1 si el movimiento horizontal es normal, -1 si esta invertido. */
    int obtenerSignoHorizontal() {
        return 1;
    }
}
