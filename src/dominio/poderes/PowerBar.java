package dominio.poderes;

import dominio.personajes.Character;

/**
 * Barra de carga del poder especial de un personaje.
 * Se recarga con el tiempo segun el nivel de poder del personaje; cuando llega
 * al 100 % el jugador puede armarla para disparar su efecto en la proxima patada.
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
     * Crea una barra vacia y desarmada para un poder y nivel dados.
     *
     * @param poder Poder que dispara esta barra.
     * @param nivel Nivel de "Poder Unico" del personaje (1 a 5).
     */
    public PowerBar(Power poder, int nivel) {
        this.poder = poder;
        this.nivel = nivel;
    }

    /**
     * Crea una barra nueva con el mismo poder y nivel, empezando vacia para no
     * compartir estado al copiar un personaje.
     *
     * @return Copia independiente de esta barra de poder.
     */
    public PowerBar nuevaCopia() {
        return new PowerBar(poder, nivel);
    }

    /**
     * Suma carga a la barra segun el tiempo transcurrido y el nivel del personaje,
     * sin pasar del 100 %.
     *
     * @param segundosTranscurridos Segundos transcurridos desde el ultimo paso.
     */
    public void recargar(double segundosTranscurridos) {
        if (segundosTranscurridos <= 0.0 || porcentaje >= LLENA) {
            return;
        }
        porcentaje += RECARGA_POR_NIVEL * nivel * segundosTranscurridos;
        if (porcentaje > LLENA) {
            porcentaje = LLENA;
        }
    }

    /**
     * Arma el poder para la siguiente patada si la barra ya esta llena al 100 %.
     * Si aun no llega al maximo, la orden no tiene efecto.
     */
    public void armar() {
        if (porcentaje >= LLENA) {
            armado = true;
        }
    }

    /**
     * Si el poder esta armado, aplica su efecto sobre el oponente y reinicia la
     * barra a cero.
     *
     * @param oponente Personaje rival que recibe el efecto.
     */
    public void dispararSobre(Character oponente) {
        if (!armado) {
            return;
        }
        poder.aplicarEfecto(oponente);
        reiniciar();
    }

    /** Vacia la barra a 0 % y desarma el poder. */
    public void reiniciar() {
        porcentaje = 0.0;
        armado = false;
    }

    /**
     * Devuelve la proporcion actual de carga de la barra para dibujarla en el HUD.
     *
     * @return Fraccion llena entre 0.0 y 1.0.
     */
    public double obtenerCarga() {
        return porcentaje / LLENA;
    }

    /**
     * Indica si el poder ya fue armado y esta listo para dispararse al patear.
     *
     * @return true si el poder esta armado.
     */
    public boolean estaArmado() {
        return armado;
    }

    /**
     * Devuelve el nivel de la estadistica "Poder Unico" asociado a esta barra.
     *
     * @return Nivel entre 1 y 5.
     */
    public int obtenerNivel() {
        return nivel;
    }

    /**
     * Devuelve el nombre del poder que activa esta barra.
     *
     * @return Nombre del poder.
     */
    public String obtenerNombrePoder() {
        return poder.obtenerNombre();
    }

    /**
     * Devuelve la descripcion breve del poder asociado a esta barra.
     *
     * @return Descripcion del poder.
     */
    public String obtenerDescripcionPoder() {
        return poder.obtenerDescripcion();
    }
}
