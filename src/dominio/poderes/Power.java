package dominio.poderes;

import dominio.Ball;
import dominio.personajes.Character;

/**
 * Poder especial de un personaje (depende de su pais). Cada subclase define
 * como afecta al oponente (polimorfismo, OCP).
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public abstract class Power {

    private final String name;
    private final String description;
    private final double durationSeconds;

    /**
     * @param name Nombre del poder.
     * @param description Descripcion del poder.
     * @param durationSeconds Duracion del efecto en segundos.
     */
    protected Power(String name, String description, double durationSeconds) {
        this.name = name;
        this.description = description;
        this.durationSeconds = durationSeconds;
    }

    /**
     * Aplica el efecto del poder sobre el oponente.
     *
     * @param opponent Personaje rival que recibe el efecto.
     * @param ball Balon del partido.
     */
    public abstract void applyEffect(Character opponent, Ball ball);

    /** @return Nombre del poder. */
    public String getName() {
        return name;
    }

    /** @return Descripcion del poder. */
    public String getDescription() {
        return description;
    }

    /** @return Duracion del efecto en segundos. */
    public double getDurationSeconds() {
        return durationSeconds;
    }
}
