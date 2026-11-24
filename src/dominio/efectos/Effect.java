package dominio.efectos;

/**
 * Base para los efectos temporales que afectan a un personaje durante un
 * tiempo limitado. Cada subclase aplica su propio cambio sobre el estado.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public abstract class Effect {

    private final String nombre;
    private double segundosRestantes;

    protected Effect(String nombre, double duracionSegundos) {
        this.nombre = nombre;
        this.segundosRestantes = duracionSegundos;
    }

    abstract void aplicarSobre(StatusEffects estado);

    void transcurrir(double segundos) {
        if (segundos <= 0.0) {
            return;
        }
        segundosRestantes -= segundos;
        if (segundosRestantes < 0.0) {
            segundosRestantes = 0.0;
        }
    }

    boolean haExpirado() {
        return segundosRestantes <= 0.0;
    }

    boolean esMismoTipoQue(Effect otro) {
        return otro != null && nombre.equals(otro.nombre);
    }

    String obtenerNombre() {
        return nombre;
    }
}
