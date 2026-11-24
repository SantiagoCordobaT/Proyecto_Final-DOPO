package dominio.modalidades;

/**
 * Define los controladores de cada jugador y si la modalidad admite el modo
 * Supervivencia.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public abstract class GameMode {

    private final String nombre;

    protected GameMode(String nombre) {
        this.nombre = nombre;
    }

    public abstract Controller crearControladorJugador1();

    public abstract Controller crearControladorJugador2();

    public boolean admiteSupervivencia() {
        return false;
    }

    public String obtenerNombre() {
        return nombre;
    }
}
