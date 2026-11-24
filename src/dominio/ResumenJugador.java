package dominio;

/**
 * Datos de solo lectura de un jugador en el partido para que la interfaz los
 * dibuje sin pedir cada atributo por separado.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class ResumenJugador {

    private final String nombre;
    private final String pais;
    private final double x;
    private final double y;
    private final double ancho;
    private final double alto;
    private final int goles;
    private final double cargaPoder;
    private final String nombrePoder;
    private final boolean poderArmado;
    private final String[] efectos;
    private final int orientacion;
    private final boolean pateando;

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
        this.efectos = efectos.clone();
        this.orientacion = orientacion;
        this.pateando = pateando;
    }

    public String obtenerNombre() {
        return nombre;
    }

    public String obtenerPais() {
        return pais;
    }

    public double obtenerX() {
        return x;
    }

    public double obtenerY() {
        return y;
    }

    public double obtenerAncho() {
        return ancho;
    }

    public double obtenerAlto() {
        return alto;
    }

    public int obtenerGoles() {
        return goles;
    }

    public double obtenerCargaPoder() {
        return cargaPoder;
    }

    public String obtenerNombrePoder() {
        return nombrePoder;
    }

    public boolean estaPoderArmado() {
        return poderArmado;
    }

    public String[] obtenerEfectos() {
        return efectos.clone();
    }

    public int obtenerOrientacion() {
        return orientacion;
    }

    public boolean estaPateando() {
        return pateando;
    }
}
