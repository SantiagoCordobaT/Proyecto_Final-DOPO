package dominio.partidos;

import dominio.modalidades.GameMode;

/**
 * Clase base para los tipos de partido. Lleva el tiempo transcurrido, el
 * ganador y si la partida ya terminó.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public abstract class MatchType {

    protected static final int JUGADOR_DOS = 2;
    protected static final double TOLERANCIA = 0.0001;

    private final String nombre;
    private boolean terminado;
    private int ganador;
    private double segundosTranscurridos;

    protected MatchType(String nombre) {
        this.nombre = nombre;
    }

    public boolean admite(GameMode modo) {
        return true;
    }

    public void iniciar() {
        this.terminado = false;
        this.ganador = 0;
        this.segundosTranscurridos = 0.0;
    }

    public void avanzar(double segundos) {
        segundosTranscurridos += segundos;
    }

    public abstract void golAnotado(int anotador, int golesAnotador);

    public boolean consumirCambioRival() {
        return false;
    }

    public abstract String obtenerTextoEstado();

    protected void finalizar(int ganador) {
        if (terminado) {
            return;
        }
        this.ganador = ganador;
        this.terminado = true;
    }

    protected double obtenerSegundosTranscurridos() {
        return segundosTranscurridos;
    }

    protected static String formatoReloj(int segundos) {
        if (segundos < 0) {
            segundos = 0;
        }
        int minutos = segundos / 60;
        int resto = segundos % 60;
        String textoMinutos = String.valueOf(minutos);
        if (minutos < 10) {
            textoMinutos = "0" + textoMinutos;
        }
        String textoSegundos = String.valueOf(resto);
        if (resto < 10) {
            textoSegundos = "0" + textoSegundos;
        }
        return textoMinutos + ":" + textoSegundos;
    }

    public boolean estaTerminado() {
        return terminado;
    }

    public int obtenerGanador() {
        return ganador;
    }

    public String obtenerNombre() {
        return nombre;
    }
}
