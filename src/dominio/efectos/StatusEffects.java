package dominio.efectos;

import java.util.ArrayList;
import java.util.List;

/**
 * Coleccion de efectos activos de un personaje (Experto en informacion): los
 * combina multiplicando sus aportes y descarta los vencidos.
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
    public void add(Effect efecto) {
        efectos.removeIf(activo -> activo.isSameKindAs(efecto));
        efectos.add(efecto);
    }

    /**
     * Descuenta tiempo a todos los efectos y elimina los que terminaron.
     *
     * @param segundosTranscurridos Segundos transcurridos.
     */
    public void tick(double segundosTranscurridos) {
        for (Effect efecto : efectos) {
            efecto.tick(segundosTranscurridos);
        }
        efectos.removeIf(Effect::isExpired);
    }

    /** Elimina todos los efectos. */
    public void clear() {
        efectos.clear();
    }

    /** @return true si ningun efecto impide moverse, saltar o patear. */
    public boolean allowActions() {
        for (Effect efecto : efectos) {
            if (!efecto.allowsActions()) {
                return false;
            }
        }
        return true;
    }

    /** @return Escala del cuerpo (producto de las escalas de los efectos). */
    public double scale() {
        double escala = 1.0;
        for (Effect efecto : efectos) {
            escala *= efecto.getScale();
        }
        return escala;
    }

    /**
     * @return Signo del movimiento horizontal (producto de los signos de los
     *         efectos).
     */
    public int horizontalSign() {
        int signo = 1;
        for (Effect efecto : efectos) {
            signo *= efecto.getHorizontalSign();
        }
        return signo;
    }

    /** @return Nombres de los efectos activos. */
    public String[] names() {
        String[] nombres = new String[efectos.size()];
        for (int i = 0; i < nombres.length; i++) {
            nombres[i] = efectos.get(i).getName();
        }
        return nombres;
    }
}
