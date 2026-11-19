package dominio.poderes;

import dominio.efectos.Effect;
import dominio.personajes.Character;

/**
 * Poder especial de un personaje (depende de su pais). Metodo plantilla:
 * aplicar el poder siempre agrega un efecto al oponente, y cada subclase solo
 * decide que efecto crea (metodo fabrica).
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public abstract class Power {

    private final String name;
    private final String description;

    /**
     * @param name Nombre del poder.
     * @param description Descripcion del poder.
     */
    protected Power(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Aplica el efecto del poder sobre el oponente.
     *
     * @param opponent Personaje rival que recibe el efecto.
     */
    public final void applyEffect(Character opponent) {
        opponent.addEffect(createEffect());
    }

    /** @return Nuevo efecto que este poder produce en el oponente. */
    protected abstract Effect createEffect();

    /** @return Nombre del poder. */
    String getName() {
        return name;
    }

    /** @return Descripcion del poder. */
    String getDescription() {
        return description;
    }
}
