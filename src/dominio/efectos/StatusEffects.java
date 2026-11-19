package dominio.efectos;

import java.util.ArrayList;
import java.util.List;

/**
 * Coleccion de efectos activos de un personaje
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class StatusEffects {

    private final List<Effect> efectos = new ArrayList<Effect>();

    /**
     * Agrega un efecto; si ya hay uno del mismo tipo se reemplaza (se reinicia su
     * tiempo).
     *
     * @param efecto Efecto a aplicar.
     */
    public void agregar(Effect efecto) {
        efectos.removeIf(activo -> activo.esMismoTipoQue(efecto));
        efectos.add(efecto);
    }

    /**
     * Descuenta tiempo a todos los efectos y elimina los que terminaron.
     *
     * @param segundosTranscurridos Segundos transcurridos.
     */
    public void transcurrir(double segundosTranscurridos) {
        for (Effect efecto : efectos) {
            efecto.transcurrir(segundosTranscurridos);
        }
        efectos.removeIf(Effect::haExpirado);
    }

    /** Elimina todos los efectos. */
    public void limpiar() {
        efectos.clear();
    }

    /** @return true si ningun efecto impide moverse, saltar o patear. */
    public boolean permitenAcciones() {
        for (Effect efecto : efectos) {
            if (!efecto.permiteAcciones()) {
                return false;
            }
        }
        return true;
    }

    /** @return Escala del cuerpo (producto de las escalas de los efectos). */
    public double escala() {
        double escala = 1.0;
        for (Effect efecto : efectos) {
            escala *= efecto.obtenerEscala();
        }
        return escala;
    }

    /**
     * @return Signo del movimiento horizontal (producto de los signos de los
     *         efectos).
     */
    public int signoHorizontal() {
        int signo = 1;
        for (Effect efecto : efectos) {
            signo *= efecto.obtenerSignoHorizontal();
        }
        return signo;
    }

    /** @return Nombres de los efectos activos. */
    public String[] obtenerNombres() {
        String[] nombres = new String[efectos.size()];
        for (int i = 0; i < nombres.length; i++) {
            nombres[i] = efectos.get(i).obtenerNombre();
        }
        return nombres;
    }
}
