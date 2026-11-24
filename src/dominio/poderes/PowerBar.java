package dominio.poderes;

import dominio.personajes.Character;

/**
 * Lleva la carga del poder de un personaje (de 0.0 a 1.0) y lo dispara al
 * patear cuando está armado.
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

    public void recargar(double segundosTranscurridos) {
        if (segundosTranscurridos <= 0.0 || carga >= 1.0) {
            return;
        }
        carga += CARGA_POR_NIVEL * nivel * segundosTranscurridos;
        if (carga > 1.0) {
            carga = 1.0;
        }
    }

    public void armar() {
        if (carga >= 1.0) {
            armado = true;
        }
    }

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
