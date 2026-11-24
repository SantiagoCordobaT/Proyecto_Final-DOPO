package dominio;

/**
 * Ficha con los datos de un personaje del catálogo para mostrarlo o editarlo en
 * la pantalla de selección.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class FichaPersonaje {

    private final String nombre;
    private final String pais;
    private final int velocidad;
    private final int salto;
    private final int fuerza;
    private final int nivelPoder;
    private final String nombrePoder;
    private final String descripcionPoder;

    FichaPersonaje(String nombre, String pais, int velocidad, int salto, int fuerza, int nivelPoder,
            String nombrePoder, String descripcionPoder) {
        this.nombre = nombre;
        this.pais = pais;
        this.velocidad = velocidad;
        this.salto = salto;
        this.fuerza = fuerza;
        this.nivelPoder = nivelPoder;
        this.nombrePoder = nombrePoder;
        this.descripcionPoder = descripcionPoder;
    }

    public String obtenerNombre() {
        return nombre;
    }

    public String obtenerPais() {
        return pais;
    }

    public int obtenerVelocidad() {
        return velocidad;
    }

    public int obtenerSalto() {
        return salto;
    }

    public int obtenerFuerza() {
        return fuerza;
    }

    public int obtenerNivelPoder() {
        return nivelPoder;
    }

    public String obtenerNombrePoder() {
        return nombrePoder;
    }

    public String obtenerDescripcionPoder() {
        return descripcionPoder;
    }
}
