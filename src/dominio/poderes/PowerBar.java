package dominio.poderes;

import dominio.personajes.Character;

/**
 * Barra de poder de un personaje: va de 0.0 a 1.0, carga con el tiempo según
 * el nivel y se descarga cuando dispara el poder en una patada.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class PowerBar {

    private static final double CARGA_POR_NIVEL = 0.02;
    private final Power poder;
    private final int nivel;
    private double carga;
    private boolean armado;

    public PowerBar(Power poder, int nivel) {
        this.poder = poder;
        this.nivel = nivel;
    }

    /** Suma carga según el tiempo y el nivel sin pasar de 1.0. */
    public void recargar(double segundosTranscurridos) {
        if (segundosTranscurridos <= 0.0 || carga >= 1.0) {
            return;
        }
        carga += CARGA_POR_NIVEL * nivel * segundosTranscurridos;
        if (carga > 1.0) {
            carga = 1.0;
        }
    }

    /** Arma el poder solo si la barra ya está completa (1.0). */
    public void armar() {
        if (carga >= 1.0) {
            armado = true;
        }
    }

    /** Aplica el poder al oponente si estaba armado y vacía la barra. */
    public void dispararSobre(Character oponente) {
        if (!armado) {
            return;
        }
        poder.aplicarEfecto(oponente);
        reiniciar();
    }

    public void reiniciar() {
        carga = 0.0;
        armado = false;
    }

    public double obtenerCarga() {
        return carga;
    }

    public boolean estaArmado() {
        return armado;
    }

    public int obtenerNivel() {
        return nivel;
    }

    public Power obtenerPoder() {
        return poder;
    }

    public String obtenerNombrePoder() {
        return poder.obtenerNombre();
    }

    public String obtenerDescripcionPoder() {
        return poder.obtenerDescripcion();
    }
}
