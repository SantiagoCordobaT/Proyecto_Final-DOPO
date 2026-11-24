package dominio.poderes;

import dominio.personajes.Character;

/**
 * Poder especial de un personaje según su país: cada poder aplica un efecto
 * sobre el oponente.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public abstract class Power {

    private final String nombre;
    private final String descripcion;

    protected Power(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public abstract void aplicarEfecto(Character oponente);

    public String obtenerNombre() {
        return nombre;
    }

    public String obtenerDescripcion() {
        return descripcion;
    }
}
