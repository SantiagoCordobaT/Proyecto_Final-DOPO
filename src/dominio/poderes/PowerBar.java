package dominio.poderes;

import dominio.personajes.Character;

/**
 * Barra de poder de un personaje
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class PowerBar {

    private static final double LLENA = 100.0;
    private static final double RECARGA_POR_NIVEL = 2.0;
    private final Power poder;
    private final int nivel;
    private double porcentaje;
    private boolean armado;

    /**
     * Crea una barra vacia y desarmada.
     *
     * @param poder Poder que dispara la barra.
     * @param nivel Nivel de "Poder Unico"
     */
    public PowerBar(Power poder, int nivel) {
        this.poder = poder;
        this.nivel = nivel;
    }

    /**
     * @return Nueva barra vacia con el mismo poder y nivel (sin estado compartido).
     */
    public PowerBar nuevaCopia() {
        return new PowerBar(poder, nivel);
    }

    /**
     * Recarga la barra sin superar el 100 %.
     *
     * @param segundosTranscurridos Segundos transcurridos.
     */
    public void recargar(double segundosTranscurridos) {
        porcentaje = Math.min(LLENA, porcentaje + RECARGA_POR_NIVEL * nivel * segundosTranscurridos);
    }

    /**
     * Arma el poder. Con la barra incompleta no tiene efecto: la orden de armar
     * es una intencion continua del jugador.
     */
    public void armar() {
        armado = porcentaje >= LLENA;
    }

    /**
     * Si el poder esta armado, lo aplica al oponente y vacia la barra.
     *
     * @param oponente Rival que recibe el efecto.
     */
    public void dispararSobre(Character oponente) {
        if (armado) {
            poder.aplicarEfecto(oponente);
            reiniciar();
        }
    }

    /** Vacia y desarma la barra. */
    public void reiniciar() {
        porcentaje = 0.0;
        armado = false;
    }

    /** @return Fraccion llena de la barra (0.0 a 1.0). */
    public double obtenerCarga() {
        return porcentaje / LLENA;
    }

    /** @return true si el poder esta armado. */
    public boolean estaArmado() {
        return armado;
    }

    /** @return Nivel de "Poder Unico" (1 a 5). */
    public int obtenerNivel() {
        return nivel;
    }

    /** @return Nombre del poder. */
    public String obtenerNombrePoder() {
        return poder.obtenerNombre();
    }

    /** @return Descripcion del poder. */
    public String obtenerDescripcionPoder() {
        return poder.obtenerDescripcion();
    }
}
