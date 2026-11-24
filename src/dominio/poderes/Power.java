package dominio.poderes;

import dominio.Named;
import dominio.efectos.Effect;
import dominio.personajes.Character;

/**
 * Poder especial de un personaje segun su pais. Aplica un efecto al oponente
 * delegando a cada subclase cual efecto concreto debe crear.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public abstract class Power implements Named {

    private final String nombre;
    private final String descripcion;

    /**
     * Crea un poder con su nombre y descripcion corta.
     *
     * @param nombre      Nombre visible del poder.
     * @param descripcion Explicacion breve de lo que produce.
     */
    protected Power(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    /**
     * Aplica el efecto de este poder sobre el personaje rival.
     *
     * @param oponente Personaje rival que recibe el efecto.
     */
    public final void aplicarEfecto(Character oponente) {
        oponente.agregarEfecto(crearEfecto());
    }

    /**
     * Crea una instancia nueva del efecto asociado a este poder.
     *
     * @return Efecto nuevo listo para aplicar.
     */
    protected abstract Effect crearEfecto();

    /**
     * Devuelve el nombre del poder.
     *
     * @return Nombre del poder.
     */
    @Override
    public String obtenerNombre() {
        return nombre;
    }

    /**
     * Devuelve la descripcion corta del poder.
     *
     * @return Descripcion del poder.
     */
    public String obtenerDescripcion() {
        return descripcion;
    }
}
