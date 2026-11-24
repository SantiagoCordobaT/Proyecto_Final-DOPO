package dominio;

/**
 * Datos de solo lectura de un jugador en el partido para que la interfaz los
 * dibuje sin pedir cada atributo por separado.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class ResumenJugador {

    public final String nombre;
    public final String pais;
    public final double x;
    public final double y;
    public final double ancho;
    public final double alto;
    public final int goles;
    public final double cargaPoder;
    public final String nombrePoder;
    public final boolean poderArmado;
    public final String[] efectos;
    public final int orientacion;
    public final boolean pateando;

    ResumenJugador(String nombre, String pais, double[] limites, int goles, double cargaPoder, String nombrePoder,
            boolean poderArmado, String[] efectos, int orientacion, boolean pateando) {
        this.nombre = nombre;
        this.pais = pais;
        this.x = limites[0];
        this.y = limites[1];
        this.ancho = limites[2];
        this.alto = limites[3];
        this.goles = goles;
        this.cargaPoder = cargaPoder;
        this.nombrePoder = nombrePoder;
        this.poderArmado = poderArmado;
        this.efectos = efectos;
        this.orientacion = orientacion;
        this.pateando = pateando;
    }
}
