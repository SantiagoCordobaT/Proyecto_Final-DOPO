package dominio.partidos;

import dominio.excepciones.POOBSoccerException;
import dominio.modalidades.GameMode;
import dominio.personajes.Character;
import dominio.personajes.CharacterCatalog;

/**
 * Base para las reglas de los tipos de partido. Lleva el reloj, el ganador y si
 * el encuentro ya terminó.
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
        terminado = false;
        ganador = 0;
        segundosTranscurridos = 0.0;
    }

    public void avanzar(double segundos) {
        segundosTranscurridos += segundos;
    }

    public abstract Character registrarGol(int anotador, int golesAnotador, Character rivalActual,
            CharacterCatalog catalogo) throws POOBSoccerException;

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
        String m = minutos < 10 ? "0" + minutos : String.valueOf(minutos);
        String s = resto < 10 ? "0" + resto : String.valueOf(resto);
        return m + ":" + s;
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
