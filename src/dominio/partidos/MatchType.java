package dominio.partidos;

import dominio.Named;
import dominio.excepciones.POOBSoccerException;
import dominio.modalidades.GameMode;

/**
 * Clase base para las reglas de partido (por goles o supervivencia).
 * Lleva el reloj, controla si el partido termino y quien gano, y se comunica
 * con Match a traves de MatchContext. Cada subclase define como reaccionar
 * al gol y que estado mostrar en pantalla.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public abstract class MatchType implements Named {

    protected static final int JUGADOR_DOS = 2;
    // Margen pequeno para evitar errores de decimales al redondear el reloj.
    protected static final double TOLERANCIA = 1e-9;

    private final String nombre;
    private MatchContext contexto;
    private boolean terminado;
    private int ganador; // 0 mientras no haya ganador; 1 o 2 al finalizar.
    private double segundosTranscurridos;

    protected MatchType(String nombre) {
        this.nombre = nombre;
    }

    // Por defecto acepta cualquier modalidad; Supervivencia lo restringe.
    public boolean admite(GameMode modo) {
        return true;
    }

    // Reinicia el estado para reutilizar la instancia al empezar un partido nuevo.
    public void iniciar(MatchContext contexto) {
        this.contexto = contexto;
        this.terminado = false;
        this.ganador = 0;
        this.segundosTranscurridos = 0.0;
    }

    // Acumula el tiempo jugado; cada subclase decide si lo muestra en pantalla.
    public void avanzar(double segundos) {
        segundosTranscurridos += segundos;
    }

    // Es final para ignorar goles si el partido ya termino antes de delegar a la
    // subclase.
    public final void golAnotado(int anotador) throws POOBSoccerException {
        if (estaTerminado()) {
            return;
        }
        alAnotarGol(anotador);
    }

    // Cada tipo de partido define aqui sus propias reglas al recibir un gol y su
    // marcador.
    protected abstract void alAnotarGol(int anotador) throws POOBSoccerException;

    public abstract String obtenerTextoEstado();

    // Cierra el partido y asigna ganador juntos para no dejar estados a medias.
    protected void finalizar(int ganador) {
        this.ganador = ganador;
        this.terminado = true;
    }

    // Atajos protegidos para consultar el partido o pedir rival sin exponer
    // contexto.
    protected int golesDe(int jugador) throws POOBSoccerException {
        return contexto.obtenerGoles(jugador);
    }

    protected void reemplazarRival() throws POOBSoccerException {
        contexto.reemplazarRival();
    }

    protected double obtenerSegundosTranscurridos() {
        return segundosTranscurridos;
    }

    // Convierte segundos enteros a formato "mm:ss".
    protected static String formatoReloj(int segundos) {
        if (segundos < 0) {
            segundos = 0;
        }
        int minutos = segundos / 60;
        int resto = segundos % 60;
        String textoMinutos = (minutos < 10) ? "0" + minutos : String.valueOf(minutos);
        String textoSegundos = (resto < 10) ? "0" + resto : String.valueOf(resto);
        return textoMinutos + ":" + textoSegundos;
    }

    public boolean estaTerminado() {
        return terminado;
    }

    public int obtenerGanador() {
        return ganador;
    }

    @Override
    public String obtenerNombre() {
        return nombre;
    }
}
