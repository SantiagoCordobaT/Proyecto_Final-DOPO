package dominio.efectos;

/**
 * Efecto temporal sobre un personaje (congelamiento, aturdimiento, reducción
 * o confusión). Por defecto no cambia acciones, tamaño ni dirección; cada
 * subclase sobrescribe lo que altera.
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
