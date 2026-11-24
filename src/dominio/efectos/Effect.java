package dominio.efectos;

/**
 * Base para los efectos temporales sobre un personaje (congelamiento,
 * aturdimiento, reduccion o confusion). Lleva el tiempo restante y ofrece
 * valores neutros por defecto para que cada subclase cambie solo lo que afecta.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public abstract class Effect {

    private final String nombre;
    private double segundosRestantes;

    /**
     * Crea un efecto con nombre visible y duracion inicial en segundos.
     *
     * @param nombre           Nombre del efecto mostrado al jugador.
     * @param duracionSegundos Duracion total en segundos.
     */
    protected Effect(String nombre, double duracionSegundos) {
        this.nombre = nombre;
        this.segundosRestantes = duracionSegundos;
    }

    /**
     * Descuenta tiempo restante al efecto sin bajarlo de cero.
     *
     * @param segundosTranscurridos Segundos transcurridos desde el ultimo paso.
     */
    void transcurrir(double segundosTranscurridos) {
        if (segundosTranscurridos <= 0.0) {
            return;
        }
        segundosRestantes -= segundosTranscurridos;
        if (segundosRestantes < 0.0) {
            segundosRestantes = 0.0;
        }
    }

    /**
     * Indica si el tiempo del efecto ya llego a cero.
     *
     * @return true si el efecto ya termino.
     */
    boolean haExpirado() {
        return segundosRestantes <= 0.0;
    }

    /**
     * Compara si otro efecto representa la misma alteracion usando su nombre.
     *
     * @param otro Efecto con el que se compara.
     * @return true si ambos efectos son del mismo tipo.
     */
    boolean esMismoTipoQue(Effect otro) {
        if (otro == null) {
            return false;
        }
        return nombre.equals(otro.nombre);
    }

    /**
     * Devuelve el nombre visible del efecto.
     *
     * @return Nombre del efecto.
     */
    String obtenerNombre() {
        return nombre;
    }

    /**
     * Indica si este efecto deja moverse, saltar y patear al personaje.
     *
     * @return true por defecto; las subclases bloqueantes devuelven false.
     */
    boolean permiteAcciones() {
        return true;
    }

    /**
     * Devuelve el factor de tamano aplicado al cuerpo del personaje.
     *
     * @return Factor de escala (1.0 significa tamano normal).
     */
    double obtenerEscala() {
        return 1.0;
    }

    /**
     * Indica el sentido de desplazamiento horizontal bajo este efecto.
     *
     * @return 1 si el movimiento es normal, -1 si esta invertido.
     */
    int obtenerSignoHorizontal() {
        return 1;
    }
}
