package dominio;

import dominio.excepciones.POOBSoccerException;
import dominio.modalidades.Action;
import dominio.modalidades.Controller;
import dominio.modalidades.GameMode;
import dominio.partidos.MatchContext;
import dominio.partidos.MatchType;
import dominio.personajes.Character;
import dominio.personajes.CharacterCatalog;

/**
 * Coordina la simulacion de un partido: cancha, balon, los dos jugadores con
 * sus controladores y las reglas de MatchType. Los personajes avanzan en tiempo
 * real y el balon en pasos fijos de 1/60 s para que la fisica sea estable sin
 * importar los FPS. Implementa MatchContext para comunicarse con el tipo de partido.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class Match implements MatchContext {

    private static final double SEGUNDOS_PASO = 1.0 / 60.0;
    private static final double TOLERANCIA = 1e-9;
    /** Cantidad de jugadores de un partido, numerados 1 y 2. */
    static final int JUGADORES = 2;
    private static final int JUGADOR_IZQUIERDO = 0;
    private static final int JUGADOR_DERECHO = 1;

    private final Field cancha = new Field();
    private final Ball balon = new Ball();
    private final Character[] jugadores;
    private final Controller[] controladores;
    private final MatchType tipo;
    private final CharacterCatalog rivales;

    private Character ultimoPateador;
    private boolean pausado;
    private double acumulador;

    /**
     * Crea el partido, ubica a cada jugador en su lado, suelta el balon en el
     * centro e inicia las reglas del tipo de partido.
     *
     * @param modo     Modalidad que crea los controladores (humano o IA).
     * @param tipo     Reglas de duracion/victoria del partido.
     * @param jugador1 Personaje del jugador 1 (lado izquierdo).
     * @param jugador2 Personaje del jugador 2 (lado derecho).
     * @param rivales  Catalogo usado para traer nuevos rivales en Supervivencia.
     */
    Match(GameMode modo, MatchType tipo, Character jugador1, Character jugador2, CharacterCatalog rivales) {
        this.jugadores = new Character[] { jugador1, jugador2 };
        this.controladores = new Controller[] { modo.crearControladorJugador1(), modo.crearControladorJugador2() };
        this.tipo = tipo;
        this.rivales = rivales;
        Side.LEFT.ubicar(jugador1);
        Side.RIGHT.ubicar(jugador2);
        cancha.soltarBalon(balon);
        tipo.iniciar(this);
    }

    /**
     * Avanza un ciclo de simulacion: actualiza controladores y personajes en tiempo
     * real, ejecuta pasos fijos de fisica para el balon (detectando goles) y
     * avanza el reloj del tipo de partido.
     *
     * @param segundosTranscurridos Segundos transcurridos desde el tick anterior.
     * @throws POOBSoccerException Si el tiempo recibido es negativo.
     */
    void actualizarTick(double segundosTranscurridos) throws POOBSoccerException {
        if (segundosTranscurridos < 0) {
            throw new POOBSoccerException(POOBSoccerException.TIEMPO_INVALIDO);
        }
        if (pausado || tipo.estaTerminado()) {
            return;
        }
        for (int i = 0; i < jugadores.length; i++) {
            controladores[i].controlar(jugadores[i], balon);
            jugadores[i].actualizar(segundosTranscurridos);
        }
        acumulador += segundosTranscurridos;
        double pasoMinimo = SEGUNDOS_PASO - TOLERANCIA;
        while (acumulador >= pasoMinimo) {
            acumulador -= SEGUNDOS_PASO;
            avanzarBalon();
            int anotador = cancha.anotadorDe(balon);
            if (anotador != Field.SIN_GOL) {
                anotarGol(anotador);
                acumulador = 0.0;
            }
        }
        tipo.avanzar(segundosTranscurridos);
    }

    /** Mueve el balon en la cancha, resuelve contactos con jugadores y rebota paredes. */
    private void avanzarBalon() {
        cancha.actualizarBalon(balon);
        resolverContacto(jugadores[JUGADOR_IZQUIERDO], jugadores[JUGADOR_DERECHO]);
        resolverContacto(jugadores[JUGADOR_DERECHO], jugadores[JUGADOR_IZQUIERDO]);
        cancha.mantenerBalonDentro(balon);
    }

    /**
     * Resuelve patada y choque corporal de un jugador con el balon: si patea
     * queda como ultimo pateador; si lo toca sin patear, un balonazo fuerte de
     * otro jugador lo aturde y frena el balon antes de empujarlo.
     */
    private void resolverContacto(Character personaje, Character oponente) {
        if (personaje.intentarPatear(balon, oponente)) {
            ultimoPateador = personaje;
            return;
        } else if (!personaje.toca(balon)) {
            return;
        }
        if (personaje != ultimoPateador && balon.esGolpeFuerte()) {
            personaje.aturdir();
            balon.frenar();
        }
        personaje.empujar(balon);
    }

    /**
     * Suma el gol al jugador indicado por el arco, notifica al tipo de partido y
     * reinicia posiciones para la siguiente jugada.
     *
     * @param anotador Jugador (1 o 2) que recibe el gol a favor.
     * @throws POOBSoccerException Si el numero de jugador no es 1 ni 2.
     */
    void anotarGol(int anotador) throws POOBSoccerException {
        jugador(anotador).sumarGol();
        tipo.golAnotado(anotador);
        reiniciarJugada();
    }

    /** Devuelve jugadores y balon a sus posiciones iniciales tras un gol. */
    private void reiniciarJugada() {
        for (Character jugador : jugadores) {
            jugador.reiniciarJugada();
        }
        cancha.soltarBalon(balon);
        ultimoPateador = null;
    }

    /**
     * Reemplaza al rival en modo Supervivencia por el siguiente del catalogo,
     * ubicandolo en el lado derecho y reiniciando el marcador del jugador 1.
     */
    @Override
    public void reemplazarRival() throws POOBSoccerException {
        jugadores[JUGADOR_DERECHO] = rivales.siguienteDespuesDe(jugadores[JUGADOR_DERECHO]);
        Side.RIGHT.ubicar(jugadores[JUGADOR_DERECHO]);
        jugadores[JUGADOR_IZQUIERDO].reiniciarGoles();
    }

    /** Pausa el partido y suelta todas las teclas para evitar movimientos pegados. */
    void pausar() {
        pausado = true;
        soltarTodo();
    }

    /** Libera todas las acciones activas de ambos controladores. */
    void soltarTodo() {
        for (Controller controlador : controladores) {
            controlador.soltarTodo();
        }
    }

    /** Reanuda la simulacion del partido. */
    void reanudar() {
        pausado = false;
    }

    /**
     * Registra que un jugador mantiene presionada una accion.
     *
     * @param numero Jugador (1 o 2).
     * @param accion Accion activada.
     * @throws POOBSoccerException Si el jugador no es 1 ni 2.
     */
    void presionar(int numero, Action accion) throws POOBSoccerException {
        controladores[indiceJugador(numero)].presionar(accion);
    }

    /**
     * Registra que un jugador solto una accion mantenida.
     *
     * @param numero Jugador (1 o 2).
     * @param accion Accion liberada.
     * @throws POOBSoccerException Si el jugador no es 1 ni 2.
     */
    void soltar(int numero, Action accion) throws POOBSoccerException {
        controladores[indiceJugador(numero)].soltar(accion);
    }

    /**
     * Convierte el numero de jugador (1 o 2) al indice interno del arreglo (0 o 1).
     *
     * @param numero Jugador (1 o 2).
     * @return Indice valido (0 o 1).
     * @throws POOBSoccerException Si el numero no es 1 ni 2.
     */
    static int indiceJugador(int numero) throws POOBSoccerException {
        if (numero < 1 || numero > JUGADORES) {
            throw new POOBSoccerException(POOBSoccerException.JUGADOR_INVALIDO);
        }
        return numero - 1;
    }

    /** Devuelve el personaje del jugador 1 o 2 tras validar el numero. */
    private Character jugador(int numero) throws POOBSoccerException {
        return jugadores[indiceJugador(numero)];
    }

    /**
     * Consulta los goles anotados por un jugador en el encuentro actual.
     *
     * @param jugador Jugador (1 o 2).
     * @return Cantidad de goles del jugador.
     * @throws POOBSoccerException Si el jugador no es 1 ni 2.
     */
    @Override
    public int obtenerGoles(int jugador) throws POOBSoccerException {
        return jugador(jugador).obtenerGoles();
    }

    /**
     * Devuelve el nombre del personaje de un jugador.
     *
     * @param numero Jugador (1 o 2).
     * @return Nombre del personaje.
     * @throws POOBSoccerException Si el jugador no es 1 ni 2.
     */
    String obtenerNombreJugador(int numero) throws POOBSoccerException {
        return jugador(numero).obtenerNombre();
    }

    /**
     * Devuelve el pais del personaje de un jugador.
     *
     * @param numero Jugador (1 o 2).
     * @return Pais del personaje.
     * @throws POOBSoccerException Si el jugador no es 1 ni 2.
     */
    String obtenerPaisJugador(int numero) throws POOBSoccerException {
        return jugador(numero).obtenerPais();
    }

    /**
     * Devuelve la posicion y tamano actual de un jugador para dibujarlo.
     *
     * @param numero Jugador (1 o 2).
     * @return Hitbox {izquierda, arriba, ancho, alto}.
     * @throws POOBSoccerException Si el jugador no es 1 ni 2.
     */
    double[] obtenerLimitesJugador(int numero) throws POOBSoccerException {
        return jugador(numero).obtenerLimites();
    }

    /**
     * Consulta que fraccion de la barra de poder tiene llena un jugador.
     *
     * @param numero Jugador (1 o 2).
     * @return Carga entre 0.0 y 1.0.
     * @throws POOBSoccerException Si el jugador no es 1 ni 2.
     */
    double obtenerCargaPoderJugador(int numero) throws POOBSoccerException {
        return jugador(numero).obtenerCargaPoder();
    }

    /**
     * Devuelve el nombre del poder especial de un jugador.
     *
     * @param numero Jugador (1 o 2).
     * @return Nombre del poder.
     * @throws POOBSoccerException Si el jugador no es 1 ni 2.
     */
    String obtenerNombrePoderJugador(int numero) throws POOBSoccerException {
        return jugador(numero).obtenerNombrePoder();
    }

    /**
     * Indica si un jugador ya armo su poder para aplicarlo en la proxima patada.
     *
     * @param numero Jugador (1 o 2).
     * @return true si el poder esta armado.
     * @throws POOBSoccerException Si el jugador no es 1 ni 2.
     */
    boolean estaPoderJugadorArmado(int numero) throws POOBSoccerException {
        return jugador(numero).estaPoderArmado();
    }

    /**
     * Lista los nombres de los efectos activos que afectan actualmente a un jugador.
     *
     * @param numero Jugador (1 o 2).
     * @return Nombres de los efectos activos.
     * @throws POOBSoccerException Si el jugador no es 1 ni 2.
     */
    String[] obtenerEfectosJugador(int numero) throws POOBSoccerException {
        return jugador(numero).obtenerNombresEfectos();
    }

    /**
     * Devuelve las coordenadas de los dos arcos de la cancha para dibujarlos.
     *
     * @return Matriz con {izquierda, arriba, ancho, alto} de cada arco.
     */
    double[][] obtenerLimitesArcos() {
        return cancha.obtenerLimitesArcos();
    }

    /**
     * Indica hacia que lado mira un jugador (1 derecha, -1 izquierda).
     *
     * @param numero Jugador (1 o 2).
     * @return Orientacion horizontal del jugador.
     * @throws POOBSoccerException Si el jugador no es 1 ni 2.
     */
    int obtenerOrientacionJugador(int numero) throws POOBSoccerException {
        return jugador(numero).obtenerOrientacion();
    }

    /**
     * Indica si el jugador se encuentra ejecutando la animacion de patada.
     *
     * @param numero Jugador (1 o 2).
     * @return true mientras dura la patada.
     * @throws POOBSoccerException Si el jugador no es 1 ni 2.
     */
    boolean estaJugadorPateando(int numero) throws POOBSoccerException {
        return jugador(numero).estaPateando();
    }

    /**
     * Calcula el centro visible del balon interpolado entre dos ticks de fisica
     * usando el tiempo acumulado pendiente (acotado a 0.0 por tolerancia).
     *
     * @return Centro visible del balon como {x, y}.
     */
    double[] obtenerPosicionBalon() {
        double fraccion = acumulador / SEGUNDOS_PASO;
        if (fraccion < 0.0) {
            fraccion = 0.0;
        }
        return cancha.balonVisible(balon, fraccion);
    }

    /**
     * Devuelve el texto de estado generado por el tipo de partido (meta/reloj o
     * balones/racha).
     *
     * @return Texto de estado para el HUD.
     */
    String obtenerEstado() {
        return tipo.obtenerTextoEstado();
    }

    /**
     * Indica si el tipo de partido ya declaro el final del encuentro.
     *
     * @return true si el partido termino.
     */
    boolean estaTerminado() {
        return tipo.estaTerminado();
    }

    /**
     * Devuelve el nombre del personaje que gano el partido finalizado.
     *
     * @return Nombre del personaje ganador.
     * @throws POOBSoccerException Si el partido aun sigue en curso.
     */
    String obtenerNombreGanador() throws POOBSoccerException {
        if (!tipo.estaTerminado()) {
            throw new POOBSoccerException(POOBSoccerException.PARTIDO_EN_CURSO);
        }
        return obtenerNombreJugador(tipo.obtenerGanador());
    }
}
