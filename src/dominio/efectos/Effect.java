package dominio.efectos;

/**
 * Clase base para los efectos temporales que alteran acciones, tamaño o
 * dirección de un personaje.
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

    void transcurrir(double segundosTranscurridos) {
        if (segundosTranscurridos <= 0.0) {
            return;
        }
        segundosRestantes -= segundosTranscurridos;
        if (segundosRestantes < 0.0) {
            segundosRestantes = 0.0;
        }
    }

    boolean haExpirado() {
        return segundosRestantes <= 0.0;
    }

    boolean esMismoTipoQue(Effect otro) {
        if (otro == null) {
            return false;
        }
        return nombre.equals(otro.nombre);
    }

    String obtenerNombre() {
        return nombre;
    }

    boolean permiteAcciones() {
        return true;
    }

    double obtenerEscala() {
        return 1.0;
    }

    int obtenerSignoHorizontal() {
        return 1;
    }
}
