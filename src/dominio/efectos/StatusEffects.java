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

    private final List<Effect> effects = new ArrayList<Effect>();

    /**
     * Agrega un efecto; si ya hay uno del mismo tipo se reemplaza (se reinicia su tiempo).
     *
     * @param effect Efecto a aplicar.
     */
    public void add(Effect effect) {
        effects.removeIf(active -> active.isSameKindAs(effect));
        effects.add(effect);
    }

    /**
     * Descuenta tiempo a todos los efectos y elimina los que terminaron.
     *
     * @param deltaTimeSeconds Segundos transcurridos.
     */
    public void tick(double deltaTimeSeconds) {
        for (Effect e : effects) {
            e.tick(deltaTimeSeconds);
        }
        effects.removeIf(Effect::isExpired);
    }

    /** Elimina todos los efectos. */
    public void clear() {
        effects.clear();
    }

    /** @return true si ningun efecto impide moverse, saltar o patear. */
    public boolean allowActions() {
        for (Effect e : effects) {
            if (!e.allowsActions()) {
                return false;
            }
        }
        return true;
    }

    /** @return Escala del cuerpo (producto de las escalas de los efectos). */
    public double scale() {
        double scale = 1.0;
        for (Effect e : effects) {
            scale *= e.getScale();
        }
        return scale;
    }

    /** @return Signo del movimiento horizontal (producto de los signos de los efectos). */
    public int horizontalSign() {
        int sign = 1;
        for (Effect e : effects) {
            sign *= e.getHorizontalSign();
        }
        return sign;
    }

    /** @return Nombres de los efectos activos. */
    public String[] names() {
        String[] names = new String[effects.size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = effects.get(i).getName();
        }
        return names;
    }
}
