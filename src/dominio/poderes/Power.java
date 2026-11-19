package dominio.poderes;

import dominio.efectos.Effect;
import dominio.personajes.Character;

/**
 * Poder especial de un personaje (depende de su pais). (plantilla cada subclase
 * solo decide que efecto crea (factory)).
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public abstract class Power {

    private final String nombre;
    private final String descripcion;

    /**
     * @param nombre      Nombre del poder.
     * @param descripcion Descripcion del poder.
     */
    protected Power(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    /**
     * Aplica el efecto del poder sobre el oponente.
     *
     * @param oponente Personaje rival que recibe el efecto.
     */
    public final void aplicarEfecto(Character oponente) {
        oponente.agregarEfecto(crearEfecto());
    }

    /** @return Nuevo efecto que este poder produce en el oponente. */
    protected abstract Effect crearEfecto();

    /** @return Nombre del poder. */
    String obtenerNombre() {
        return nombre;
    }

    /** @return Descripcion del poder. */
    String obtenerDescripcion() {
        return descripcion;
    }
}
