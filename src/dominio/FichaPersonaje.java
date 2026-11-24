package dominio;

/**
 * Ficha con los datos de un personaje del catálogo para mostrarlo o editarlo en
 * la pantalla de selección.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class FichaPersonaje {

    public final String nombre;
    public final String pais;
    public final int velocidad;
    public final int salto;
    public final int fuerza;
    public final int nivelPoder;
    public final String nombrePoder;
    public final String descripcionPoder;

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
}
