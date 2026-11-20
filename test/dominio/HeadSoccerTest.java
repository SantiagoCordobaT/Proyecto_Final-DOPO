package dominio;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dominio.excepciones.POOBSoccerException;
import dominio.modalidades.Action;
import dominio.modalidades.GameModeCatalog;
import dominio.partidos.MatchContext;
import dominio.partidos.MatchType;
import dominio.partidos.MatchTypeCatalog;
import dominio.personajes.Character;
import dominio.personajes.CharacterCatalog;
import dominio.poderes.ConfusionPower;
import dominio.poderes.FreezePower;
import dominio.poderes.ShrinkPower;
import dominio.poderes.StunPower;

/**
 * Pruebas unitarias del dominio de POOBSoccer.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class HeadSoccerTest {

    private static final int MODO_PVSP = 0;
    private static final int MODO_PVSM = 1;
    private static final int MODO_MVSM = 2;
    private static final int GOLES_3 = 0;
    private static final int SUPERVIVENCIA = 3;
    private static final double PASO_TICK = 1.0 / 60.0;

    // Valores esperados del enunciado y de las reglas del juego (el dominio los
    // mantiene privados)
    private static final double X_INICIAL_IZQUIERDA = 200.0;
    private static final double X_INICIAL_DERECHA = 700.0;
    private static final String CONGELADO = "Congelado";
    private static final String ATURDIDO = "Aturdido";
    /**
     * Fraccion de la barra que se recarga por segundo y por nivel de Poder Unico (2
     * %).
     */
    private static final double RECARGA_POR_NIVEL = 0.02;

    private HeadSoccer juego;

    @BeforeEach
    public void preparar() throws POOBSoccerException {
        juego = HeadSoccer.obtenerInstancia();
        juego.nuevoJuego();
    }

    private void iniciar(int modo, int tipoPartido) throws POOBSoccerException {
        iniciar(modo, tipoPartido, 0, 0);
    }

    private void iniciar(int modo, int tipoPartido, int personaje1, int personaje2) throws POOBSoccerException {
        juego.seleccionarModo(modo);
        juego.seleccionarTipoPartido(tipoPartido);
        juego.seleccionarJugador(1, personaje1);
        juego.seleccionarJugador(2, personaje2);
        juego.iniciarPartido();
    }

    private static boolean tieneEfecto(String[] nombresEfectos, String nombreEfecto) {
        return Arrays.asList(nombresEfectos).contains(nombreEfecto);
    }

    private static double centroX(double[] limites) {
        return limites[0] + limites[2] / 2.0;
    }

    /**
     * @return Balon quieto pegado al costado derecho del personaje, a media altura
     *         (al alcance de su patada).
     */
    private static Ball balonAlLado(Character personaje) {
        double[] limites = personaje.obtenerLimites();
        Ball balon = new Ball();
        balon.ubicarEn(limites[0] + limites[2] + 10.0, personaje.obtenerCentroY());
        return balon;
    }

    /**
     * @return Partido creado directamente desde los catalogos, con copias de los
     *         personajes indicados.
     */
    private static Match crearPartido(int modo, int tipoPartido, int personaje1, int personaje2)
            throws POOBSoccerException {
        CharacterCatalog personajes = new CharacterCatalog();
        return new Match(new GameModeCatalog().obtener(modo), new MatchTypeCatalog().obtener(tipoPartido),
                personajes.obtener(personaje1).copiar(), personajes.obtener(personaje2).copiar(), personajes);
    }

    private static void anotar(Match partido, int anotador, int veces) throws POOBSoccerException {
        for (int i = 0; i < veces; i++) {
            partido.anotarGol(anotador);
        }
    }

    /**
     * Partido simulado para probar los tipos de partido sin cancha ni personajes
     * (Protected Variations).
     */
    private static final class StubContext implements MatchContext {

        private final int[] goles = new int[2];
        private int rivalesReemplazados;

        @Override
        public int obtenerGoles(int jugador) {
            return goles[jugador - 1];
        }

        @Override
        public void reemplazarRival() {
            rivalesReemplazados++;
        }
    }

    // ---------------------------------------------------------------- estadisticas

    @Test
    public void deberiaAceptarEstadisticasEntreUnoYCinco() throws POOBSoccerException {
        Character personaje = new Character("A", "Canadá", 1, 3, 5, 3, new FreezePower());
        assertEquals(1, personaje.obtenerVelocidad());
        assertEquals(3, personaje.obtenerSalto());
        assertEquals(5, personaje.obtenerFuerza());
    }

    @Test
    public void deberiaRechazarEstadisticasFueraDeRango() {
        POOBSoccerException excepcion = assertThrows(POOBSoccerException.class,
                () -> new Character("A", "Canadá", 0, 3, 3, 3, new FreezePower()));
        assertEquals(POOBSoccerException.ESTADISTICAS_FUERA_DE_RANGO, excepcion.getMessage());
        assertThrows(POOBSoccerException.class, () -> new Character("A", "Canadá", 3, 6, 3, 3, new FreezePower()));
        assertThrows(POOBSoccerException.class, () -> new Character("A", "Canadá", 3, 3, -1, 3, new FreezePower()));
        assertThrows(POOBSoccerException.class, () -> new Character("A", "Canadá", 3, 3, 3, 6, new FreezePower()));
    }

    @Test
    public void copiarNoDeberiaCompartirEstado() throws POOBSoccerException {
        Character original = new Character("A", "Canadá", 3, 3, 3, 3, new FreezePower());
        Character copia = original.copiar();
        assertNotSame(original, copia);
        copia.aturdir();
        copia.sumarGol();
        copia.actualizar(1.0);
        assertEquals(0, original.obtenerNombresEfectos().length);
        assertEquals(0, original.obtenerGoles());
        assertEquals(0.0, original.obtenerCargaPoder(), 1e-9);
        assertTrue(copia.obtenerCargaPoder() > 0.0);
    }

    // ----------------------------------------------------------------
    // supervivencia

    @Test
    public void supervivenciaSoloDeberiaPermitirseEnPvsM() throws POOBSoccerException {
        juego.seleccionarModo(MODO_PVSP);
        POOBSoccerException excepcion = assertThrows(POOBSoccerException.class,
                () -> juego.seleccionarTipoPartido(SUPERVIVENCIA));
        assertEquals(POOBSoccerException.SUPERVIVENCIA_SOLO_PVSM, excepcion.getMessage());

        juego.seleccionarModo(MODO_MVSM);
        assertThrows(POOBSoccerException.class, () -> juego.seleccionarTipoPartido(SUPERVIVENCIA));

        iniciar(MODO_PVSM, SUPERVIVENCIA, 0, 1);
        assertFalse(juego.estaPartidoTerminado());
        assertEquals("Balones 3-3 | Racha 0", juego.obtenerEstadoPartido());
    }

    @Test
    public void seleccionarModoDeberiaLimpiarTipoPartido() throws POOBSoccerException {
        juego.seleccionarModo(MODO_PVSM);
        juego.seleccionarTipoPartido(SUPERVIVENCIA);
        juego.seleccionarModo(MODO_PVSP);
        juego.seleccionarJugador(1, 0);
        juego.seleccionarJugador(2, 1);
        POOBSoccerException excepcion = assertThrows(POOBSoccerException.class, () -> juego.iniciarPartido());
        assertEquals(POOBSoccerException.MODO_INVALIDO, excepcion.getMessage());
    }

    @Test
    public void tipoPartidoDeberiaRequerirModo() {
        POOBSoccerException excepcion = assertThrows(POOBSoccerException.class,
                () -> juego.seleccionarTipoPartido(GOLES_3));
        assertEquals(POOBSoccerException.MODO_INVALIDO, excepcion.getMessage());
    }

    @Test
    public void supervivenciaDeberiaTerminarCuandoJugadorUnoPierdeBalones() throws POOBSoccerException {
        Match partido = crearPartido(MODO_PVSM, SUPERVIVENCIA, 0, 1);
        anotar(partido, 2, 3);
        assertEquals("Balones 0-3 | Racha 0", partido.obtenerEstado());
        assertTrue(partido.estaTerminado());
        assertEquals(partido.obtenerNombreJugador(2), partido.obtenerNombreGanador());
    }

    @Test
    public void supervivenciaDeberiaContinuarSerieCuandoMaquinaPierdeBalones() throws POOBSoccerException {
        Match partido = crearPartido(MODO_PVSM, SUPERVIVENCIA, 0, 0);
        String primerRival = partido.obtenerNombreJugador(2);
        partido.actualizarTick(PASO_TICK);
        assertTrue(partido.obtenerCargaPoderJugador(2) > 0.0);

        anotar(partido, 1, 3);
        assertEquals(0.0, partido.obtenerCargaPoderJugador(2), 1e-9);
        assertEquals(0, partido.obtenerGoles(2));
        assertFalse(partido.estaTerminado());
        assertEquals("Balones 3-3 | Racha 1", partido.obtenerEstado());
        assertNotEquals(primerRival, partido.obtenerNombreJugador(2));
        assertEquals(X_INICIAL_DERECHA, centroX(partido.obtenerLimitesJugador(2)), 1e-9);
    }

    @Test
    public void marcadorSupervivenciaDeberiaReiniciarseContraNuevoRival() throws POOBSoccerException {
        Match partido = crearPartido(MODO_PVSM, SUPERVIVENCIA, 0, 1);
        partido.anotarGol(2);
        partido.actualizarTick(PASO_TICK);
        double cargaJugadorUno = partido.obtenerCargaPoderJugador(1);
        anotar(partido, 1, 3);
        assertEquals(0, partido.obtenerGoles(1));
        assertEquals(0, partido.obtenerGoles(2));
        assertEquals("Balones 2-3 | Racha 1", partido.obtenerEstado());
        assertEquals(cargaJugadorUno, partido.obtenerCargaPoderJugador(1), 1e-9);
        partido.anotarGol(1);
        assertEquals(1, partido.obtenerGoles(1));
    }

    @Test
    public void supervivenciaSoloDeberiaTerminarCuandoJugadorUnoSeQuedaSinBalones() throws POOBSoccerException {
        Match partido = crearPartido(MODO_PVSM, SUPERVIVENCIA, 0, 1);
        anotar(partido, 1, 3);
        assertEquals("Balones 3-3 | Racha 1", partido.obtenerEstado());
        anotar(partido, 2, 2);
        assertFalse(partido.estaTerminado());
        partido.anotarGol(2);
        assertTrue(partido.estaTerminado());
        assertEquals("Balones 0-3 | Racha 1", partido.obtenerEstado());
    }

    @Test
    public void supervivenciaDeberiaPedirNuevoRivalAlContexto() throws POOBSoccerException {
        MatchType tipo = new MatchTypeCatalog().obtener(SUPERVIVENCIA);
        StubContext contexto = new StubContext();
        tipo.iniciar(contexto);
        for (int i = 0; i < 3; i++) {
            tipo.golAnotado(1);
        }
        assertEquals(1, contexto.rivalesReemplazados);
        assertEquals("Balones 3-3 | Racha 1", tipo.obtenerTextoEstado());
        assertFalse(tipo.estaTerminado());
    }

    @Test
    public void catalogoDeberiaRotarRivales() throws POOBSoccerException {
        String[] nombres = juego.obtenerNombresPersonajes();
        Match partido = crearPartido(MODO_PVSM, SUPERVIVENCIA, 0, nombres.length - 1);
        for (int rival = 0; rival < 2; rival++) {
            anotar(partido, 1, 3);
            assertEquals(nombres[rival], partido.obtenerNombreJugador(2));
        }
        assertEquals("Balones 3-3 | Racha 2", partido.obtenerEstado());
    }

    // ---------------------------------------------------------------- poderes

    @Test
    public void poderSoloDeberiaActivarseConBarraLlena() throws POOBSoccerException {
        iniciar(MODO_PVSP, GOLES_3);
        assertEquals(0.0, juego.obtenerBarraPoderJugador(1), 1e-9);
        juego.presionarAccion(1, "POWER");
        juego.actualizarTick(PASO_TICK);
        assertFalse(juego.estaPoderJugadorArmado(1));

        juego.actualizarTick(17.0);
        assertEquals(1.0, juego.obtenerBarraPoderJugador(1), 1e-9);
        juego.actualizarTick(PASO_TICK);
        assertTrue(juego.estaPoderJugadorArmado(1));
    }

    @Test
    public void barraPoderDeberiaRecargarseConElTiempo() throws POOBSoccerException {
        iniciar(MODO_PVSP, GOLES_3);
        juego.actualizarTick(1.0);
        assertEquals(RECARGA_POR_NIVEL * juego.obtenerNivelPoderPersonaje(0), juego.obtenerBarraPoderJugador(1),
                1e-6);
    }

    @Test
    public void poderCongelamientoDeberiaBloquearAccionesDelOponente() throws POOBSoccerException {
        Character victima = new Character("B", "Canadá", 3, 3, 3, 3, new FreezePower());
        Character rival = new Character("C", "Japón", 3, 3, 3, 3, new StunPower());
        victima.entrarEn(400.0, 1);
        rival.entrarEn(X_INICIAL_DERECHA, -1);
        double arriba = victima.obtenerBordeSuperior();
        new FreezePower().aplicarEfecto(victima);
        assertTrue(tieneEfecto(victima.obtenerNombresEfectos(), CONGELADO));
        victima.solicitarPatada();
        assertFalse(victima.intentarPatear(balonAlLado(victima), rival));
        victima.saltar();
        victima.establecerDireccionMovimiento(1);
        victima.actualizar(0.5);
        assertEquals(400.0, victima.obtenerX(), 1e-9);
        assertEquals(arriba, victima.obtenerBordeSuperior(), 1e-9);
    }

    @Test
    public void poderReduccionDeberiaReducirTamanoALaMitad() throws POOBSoccerException {
        Character victima = new Character("B", "Japón", 3, 3, 3, 3, new ShrinkPower());
        double ancho = victima.obtenerLimites()[2];
        new ShrinkPower().aplicarEfecto(victima);
        assertEquals(ancho / 2.0, victima.obtenerLimites()[2], 1e-9);
    }

    @Test
    public void poderConfusionDeberiaInvertirControlesHorizontales() throws POOBSoccerException {
        Character victima = new Character("B", "Brasil", 3, 3, 3, 3, new ConfusionPower());
        victima.entrarEn(400.0, 1);
        new ConfusionPower().aplicarEfecto(victima);
        victima.establecerDireccionMovimiento(1);
        victima.actualizar(0.5);
        assertTrue(victima.obtenerX() < 400.0);
    }

    @Test
    public void patadaConPoderArmadoDeberiaAfectarAlOponente() throws POOBSoccerException {
        Character pateador = new Character("A", "Canadá", 3, 3, 3, 3, new FreezePower());
        Character rival = new Character("B", "Japón", 3, 3, 3, 3, new StunPower());
        pateador.entrarEn(400.0, 1);
        rival.entrarEn(X_INICIAL_DERECHA, -1);
        pateador.actualizar(17.0);
        pateador.armarPoder();
        assertTrue(pateador.estaPoderArmado());
        pateador.solicitarPatada();
        assertTrue(pateador.intentarPatear(balonAlLado(pateador), rival));
        assertTrue(tieneEfecto(rival.obtenerNombresEfectos(), CONGELADO));
        assertFalse(pateador.estaPoderArmado());
        assertEquals(0.0, pateador.obtenerCargaPoder(), 1e-9);
    }

    @Test
    public void poderAturdimientoDeberiaAturdirAlOponente() throws POOBSoccerException {
        Character victima = new Character("B", "Japón", 3, 3, 3, 3, new StunPower());
        new StunPower().aplicarEfecto(victima);
        assertTrue(tieneEfecto(victima.obtenerNombresEfectos(), ATURDIDO));
    }

    // ---------------------------------------------------------------- aturdimiento

    @Test
    public void aturdimientoDeberiaDurarTresSegundos() throws POOBSoccerException {
        Character personaje = new Character("A", "Canadá", 3, 3, 3, 3, new FreezePower());
        Character rival = new Character("B", "Japón", 3, 3, 3, 3, new StunPower());
        personaje.entrarEn(400.0, 1);
        rival.entrarEn(X_INICIAL_DERECHA, -1);
        double arriba = personaje.obtenerBordeSuperior();
        personaje.aturdir();
        assertTrue(tieneEfecto(personaje.obtenerNombresEfectos(), ATURDIDO));
        personaje.solicitarPatada();
        assertFalse(personaje.intentarPatear(balonAlLado(personaje), rival));
        personaje.saltar();
        personaje.establecerDireccionMovimiento(1);
        personaje.actualizar(2.9);
        assertTrue(tieneEfecto(personaje.obtenerNombresEfectos(), ATURDIDO));
        assertEquals(400.0, personaje.obtenerX(), 1e-9);
        assertEquals(arriba, personaje.obtenerBordeSuperior(), 1e-9);
        personaje.actualizar(0.2);
        assertEquals(0, personaje.obtenerNombresEfectos().length);
        personaje.solicitarPatada();
        assertTrue(personaje.intentarPatear(balonAlLado(personaje), rival));
    }

    @Test
    public void patadaCuerpoACuerpoDeberiaAturdirTresSegundos() throws POOBSoccerException {
        Character personajeA = new Character("A", "Canadá", 3, 3, 3, 3, new FreezePower());
        Character personajeB = new Character("B", "Japón", 3, 3, 3, 3, new StunPower());
        personajeA.entrarEn(400.0, 1);
        double[] limitesA = personajeA.obtenerLimites();
        personajeB.entrarEn(limitesA[0] + limitesA[2] + 5.0 + personajeB.obtenerLimites()[2] / 2.0, -1);
        Ball balonLejano = new Ball();
        balonLejano.ubicarEn(Field.ANCHO - Goal.ANCHO, Ball.RADIO);
        personajeA.solicitarPatada();
        assertFalse(personajeA.intentarPatear(balonLejano, personajeB));
        assertTrue(tieneEfecto(personajeB.obtenerNombresEfectos(), ATURDIDO));
        personajeB.solicitarPatada();
        assertFalse(personajeB.intentarPatear(balonAlLado(personajeB), personajeA));
        personajeB.actualizar(2.9);
        assertTrue(tieneEfecto(personajeB.obtenerNombresEfectos(), ATURDIDO));
        personajeB.actualizar(0.2);
        assertEquals(0, personajeB.obtenerNombresEfectos().length);
    }

    @Test
    public void patadaCuerpoACuerpoFueraDeRangoNoDeberiaAturdir() throws POOBSoccerException {
        iniciar(MODO_PVSP, GOLES_3);
        juego.presionarAccion(1, "KICK");
        juego.actualizarTick(PASO_TICK);
        assertEquals(0, juego.obtenerEfectosJugador(2).length);
    }

    @Test
    public void patadaDeberiaSerVisibleMientrasDura() throws POOBSoccerException {
        Character personajeA = new Character("A", "Canadá", 3, 3, 3, 3, new FreezePower());
        Character personajeB = new Character("B", "Japón", 3, 3, 3, 3, new StunPower());
        personajeA.entrarEn(400.0, 1);
        personajeB.entrarEn(400.0, -1);
        assertFalse(personajeA.estaPateando());
        personajeA.solicitarPatada();
        personajeA.intentarPatear(balonAlLado(personajeA), personajeB);
        assertTrue(personajeA.estaPateando());
        personajeA.actualizar(1.0);
        assertFalse(personajeA.estaPateando());
    }

    @Test
    public void fachadaDeberiaExponerOrientacionYPatada() throws POOBSoccerException {
        iniciar(MODO_PVSP, GOLES_3);
        assertEquals(1, juego.obtenerOrientacionJugador(1));
        assertEquals(-1, juego.obtenerOrientacionJugador(2));
        assertFalse(juego.estaJugadorPateando(1));
    }

    @Test
    public void balonDeberiaAdelantarseEntrePasosSinSalirDeLaCancha() {
        Field cancha = new Field();
        Ball balon = new Ball();
        balon.ubicarEn(450.0, 100.0);
        balon.patear(5, 1);
        assertArrayEquals(new double[] { 450.0, 100.0 }, cancha.balonVisible(balon, 0.0), 1e-9);
        assertArrayEquals(new double[] { 453.0, 97.5 }, cancha.balonVisible(balon, 0.5), 1e-9);
        balon.ubicarEn(Field.ANCHO - Ball.RADIO, 100.0);
        balon.patear(5, 1);
        assertEquals(Field.ANCHO - Ball.RADIO, cancha.balonVisible(balon, 0.5)[0], 1e-9);
    }

    @Test
    public void reiniciarJugadaDeberiaLimpiarEfectosYVolverAlInicio() throws POOBSoccerException {
        Character personaje = new Character("A", "Canadá", 3, 3, 3, 3, new FreezePower());
        personaje.entrarEn(400.0, 1);
        personaje.establecerDireccionMovimiento(1);
        personaje.actualizar(0.5);
        personaje.aturdir();
        personaje.sumarGol();
        personaje.reiniciarJugada();
        assertEquals(0, personaje.obtenerNombresEfectos().length);
        assertEquals(400.0, personaje.obtenerX(), 1e-9);
        assertEquals(1, personaje.obtenerGoles());
        assertTrue(personaje.obtenerCargaPoder() > 0.0);
    }

    @Test
    public void entrarEnDeberiaReiniciarGolesYBarraPoder() throws POOBSoccerException {
        Character personaje = new Character("A", "Canadá", 3, 3, 3, 3, new FreezePower());
        personaje.sumarGol();
        personaje.actualizar(1.0);
        personaje.entrarEn(X_INICIAL_DERECHA, -1);
        assertEquals(0, personaje.obtenerGoles());
        assertEquals(0.0, personaje.obtenerCargaPoder(), 1e-9);
        assertEquals(X_INICIAL_DERECHA, personaje.obtenerX(), 1e-9);
        assertEquals(-1, personaje.obtenerOrientacion());
    }

    // ---------------------------------------------------------------- goles

    @Test
    public void canchaDeberiaAcreditarAlAtacanteDeCadaArco() {
        Field cancha = new Field();
        Ball balon = new Ball();
        balon.ubicarEn(Goal.ANCHO / 2.0, Field.SUELO_Y - Ball.RADIO);
        assertEquals(2, cancha.anotadorDe(balon));
        balon.ubicarEn(Field.ANCHO - Goal.ANCHO / 2.0, Field.SUELO_Y - Ball.RADIO);
        assertEquals(1, cancha.anotadorDe(balon));
        cancha.soltarBalon(balon);
        assertEquals(Field.SIN_GOL, cancha.anotadorDe(balon));
        assertEquals(Field.ANCHO / 2.0, balon.obtenerX(), 1e-9);
    }

    @Test
    public void empujeContraParedDeberiaMantenerBalonEnLaCancha() throws POOBSoccerException {
        Field cancha = new Field();
        Character personaje = new Character("A", "Canadá", 3, 3, 3, 3, new FreezePower());
        personaje.entrarEn(personaje.obtenerLimites()[2] / 2.0, 1);
        Ball balon = new Ball();
        balon.ubicarEn(Ball.RADIO, Field.SUELO_Y - Ball.RADIO);
        assertTrue(personaje.toca(balon));
        personaje.empujar(balon);
        assertTrue(balon.obtenerX() < Ball.RADIO);
        cancha.mantenerBalonDentro(balon);
        assertEquals(Ball.RADIO, balon.obtenerX(), 1e-9);
    }

    @Test
    public void balonDeberiaRebotarAlJuegoDesdeParedLateral() {
        Field cancha = new Field();
        Ball balon = new Ball();
        balon.ubicarEn(Field.ANCHO - 60.0, 100.0);
        balon.patear(5, 1);
        double maximoX = 0.0;
        for (int paso = 0; paso < 120; paso++) {
            cancha.actualizarBalon(balon);
            cancha.mantenerBalonDentro(balon);
            maximoX = Math.max(maximoX, balon.obtenerX());
        }
        assertEquals(Field.ANCHO - Ball.RADIO, maximoX, 1e-9);
        assertTrue(balon.obtenerX() < Field.ANCHO - 200.0, "El balon debe volver al juego tras rebotar en la pared");
    }

    @Test
    public void balonParcialmenteDentroNoDeberiaSerGol() {
        Goal arco = new Goal(0.0, Goal.ANCHO, 2);
        Ball parcial = new Ball();
        parcial.ubicarEn(Goal.ANCHO, Field.SUELO_Y - Ball.RADIO - 5.0);
        assertFalse(arco.contieneBalon(parcial));
        Ball adentro = new Ball();
        adentro.ubicarEn(Goal.ANCHO - Ball.RADIO, Field.SUELO_Y - Ball.RADIO - 5.0);
        assertTrue(arco.contieneBalon(adentro));
    }

    @Test
    public void balonDeberiaRebotarEnTravesano() {
        Goal arco = new Goal(0.0, Goal.ANCHO, 2);
        double travesanoArriba = Field.SUELO_Y - Goal.ALTO;
        Ball balon = new Ball();
        balon.ubicarEn(Goal.ANCHO / 2.0, travesanoArriba - Ball.RADIO + 3.0);
        arco.resolverTravesano(balon);
        assertEquals(travesanoArriba - Ball.RADIO, balon.obtenerY(), 1e-9);
    }

    @Test
    public void golSoloDeberiaContarParaElAnotador() throws POOBSoccerException {
        Match partido = crearPartido(MODO_PVSP, GOLES_3, 0, 0);
        partido.anotarGol(1);
        assertEquals(1, partido.obtenerGoles(1));
        assertEquals(0, partido.obtenerGoles(2));
        partido.anotarGol(2);
        assertEquals(1, partido.obtenerGoles(2));
        POOBSoccerException excepcion = assertThrows(POOBSoccerException.class, () -> partido.anotarGol(3));
        assertEquals(POOBSoccerException.JUGADOR_INVALIDO, excepcion.getMessage());
    }

    @Test
    public void deberiaReiniciarJugadaDespuesDeGol() throws POOBSoccerException {
        CharacterCatalog personajes = new CharacterCatalog();
        Character jugador1 = personajes.obtener(0).copiar();
        Match partido = new Match(new GameModeCatalog().obtener(MODO_PVSP),
                new MatchTypeCatalog().obtener(GOLES_3), jugador1, personajes.obtener(1).copiar(), personajes);
        partido.presionar(1, Action.RIGHT);
        partido.actualizarTick(0.5);
        assertTrue(centroX(partido.obtenerLimitesJugador(1)) > X_INICIAL_IZQUIERDA);
        jugador1.aturdir();
        partido.anotarGol(1);
        assertEquals(X_INICIAL_IZQUIERDA, centroX(partido.obtenerLimitesJugador(1)), 1e-9);
        assertEquals(X_INICIAL_DERECHA, centroX(partido.obtenerLimitesJugador(2)), 1e-9);
        assertEquals(0, partido.obtenerEfectosJugador(1).length);
        assertEquals(Field.ANCHO / 2.0, partido.obtenerPosicionBalon()[0], 1e-9);
    }

    @Test
    public void partidoPorGolesDeberiaTerminarAlAlcanzarMeta() throws POOBSoccerException {
        Match partido = crearPartido(MODO_PVSP, GOLES_3, 0, 0);
        anotar(partido, 1, 2);
        assertFalse(partido.estaTerminado());
        partido.anotarGol(1);
        assertTrue(partido.estaTerminado());
        assertEquals(partido.obtenerNombreJugador(1), partido.obtenerNombreGanador());
    }

    @Test
    public void estadoPartidoPorGolesDeberiaMostrarMeta() throws POOBSoccerException {
        iniciar(MODO_PVSP, GOLES_3);
        assertEquals("Meta: 3 goles | 00:00", juego.obtenerEstadoPartido());
        juego.actualizarTick(0.5);
        assertEquals("Meta: 3 goles | 00:00", juego.obtenerEstadoPartido());
        juego.actualizarTick(0.5);
        assertEquals("Meta: 3 goles | 00:01", juego.obtenerEstadoPartido());
        juego.actualizarTick(0.99);
        assertEquals("Meta: 3 goles | 00:01", juego.obtenerEstadoPartido());
    }

    @Test
    public void golAnotadoDespuesDelFinalDeberiaIgnorarse() throws POOBSoccerException {
        MatchType tipo = new MatchTypeCatalog().obtener(GOLES_3);
        StubContext contexto = new StubContext();
        tipo.iniciar(contexto);
        contexto.goles[0] = 3;
        tipo.golAnotado(1);
        contexto.goles[1] = 3;
        tipo.golAnotado(2);
        assertTrue(tipo.estaTerminado());
        assertEquals(1, tipo.obtenerGanador());

        tipo.iniciar(new StubContext());
        assertFalse(tipo.estaTerminado());
    }

    // ---------------------------------------------------------------- facade,
    // entrada, pausa e IA

    @Test
    public void noDeberiaIniciarSinAmbosJugadores() throws POOBSoccerException {
        juego.seleccionarModo(MODO_PVSP);
        juego.seleccionarTipoPartido(GOLES_3);
        juego.seleccionarJugador(1, 0);
        POOBSoccerException excepcion = assertThrows(POOBSoccerException.class, () -> juego.iniciarPartido());
        assertEquals(POOBSoccerException.JUGADORES_INCOMPLETOS, excepcion.getMessage());
    }

    @Test
    public void deberiaRechazarSeleccionesInvalidas() throws POOBSoccerException {
        POOBSoccerException excepcion = assertThrows(POOBSoccerException.class,
                () -> juego.seleccionarJugador(1, 99));
        assertEquals(POOBSoccerException.PERSONAJE_INVALIDO, excepcion.getMessage());
        excepcion = assertThrows(POOBSoccerException.class, () -> juego.seleccionarJugador(3, 0));
        assertEquals(POOBSoccerException.JUGADOR_INVALIDO, excepcion.getMessage());
        assertThrows(POOBSoccerException.class, () -> juego.seleccionarJugador(0, 0));
        assertThrows(POOBSoccerException.class, () -> juego.seleccionarModo(-1));
        juego.seleccionarModo(MODO_PVSP);
        excepcion = assertThrows(POOBSoccerException.class, () -> juego.seleccionarTipoPartido(99));
        assertEquals(POOBSoccerException.MODO_INVALIDO, excepcion.getMessage());
        excepcion = assertThrows(POOBSoccerException.class, () -> juego.actualizarTick(PASO_TICK));
        assertEquals(POOBSoccerException.PARTIDO_NO_INICIADO, excepcion.getMessage());
        assertThrows(POOBSoccerException.class, () -> juego.presionarAccion(1, "KICK"));
        assertThrows(POOBSoccerException.class, () -> juego.obtenerEstadoPartido());
    }

    @Test
    public void singletonDeberiaSerUnico() throws POOBSoccerException {
        assertSame(juego, HeadSoccer.obtenerInstancia());
    }

    @Test
    public void nuevoJuegoDeberiaLimpiarSesion() throws POOBSoccerException {
        iniciar(MODO_PVSP, GOLES_3);
        juego.nuevoJuego();
        POOBSoccerException excepcion = assertThrows(POOBSoccerException.class, () -> juego.obtenerEstadoPartido());
        assertEquals(POOBSoccerException.PARTIDO_NO_INICIADO, excepcion.getMessage());
        excepcion = assertThrows(POOBSoccerException.class, () -> juego.iniciarPartido());
        assertEquals(POOBSoccerException.MODO_INVALIDO, excepcion.getMessage());
        juego.seleccionarModo(MODO_PVSP);
        juego.seleccionarTipoPartido(GOLES_3);
        excepcion = assertThrows(POOBSoccerException.class, () -> juego.iniciarPartido());
        assertEquals(POOBSoccerException.JUGADORES_INCOMPLETOS, excepcion.getMessage());
    }

    @Test
    public void revanchaDeberiaEmpezarDeCeroConLosMismosPersonajes() throws POOBSoccerException {
        String[] nombres = juego.obtenerNombresPersonajes();
        iniciar(MODO_PVSP, GOLES_3, 0, 1);
        juego.presionarAccion(1, "RIGHT");
        juego.actualizarTick(1.0);
        assertTrue(juego.obtenerBarraPoderJugador(2) > 0.0);

        juego.iniciarPartido();
        assertEquals(nombres[0], juego.obtenerNombreJugador(1));
        assertEquals(nombres[1], juego.obtenerNombreJugador(2));
        assertEquals(0.0, juego.obtenerBarraPoderJugador(2), 1e-9);
        assertEquals("Meta: 3 goles | 00:00", juego.obtenerEstadoPartido());
        juego.actualizarTick(PASO_TICK);
        assertEquals(X_INICIAL_IZQUIERDA, centroX(juego.obtenerLimitesJugador(1)), 1e-9);
    }

    @Test
    public void partidoPausadoNoDeberiaAvanzar() throws POOBSoccerException {
        iniciar(MODO_PVSP, GOLES_3);
        assertEquals("Meta: 3 goles | 00:00", juego.obtenerEstadoPartido());
        juego.pausar();
        double y = juego.obtenerPosicionBalon()[1];
        juego.actualizarTick(1.0);
        assertEquals(y, juego.obtenerPosicionBalon()[1], 1e-9);
        assertEquals("Meta: 3 goles | 00:00", juego.obtenerEstadoPartido());
        juego.reanudar();
        juego.actualizarTick(1.0);
        assertEquals("Meta: 3 goles | 00:01", juego.obtenerEstadoPartido());
    }

    @Test
    public void pausarDeberiaSoltarTeclasMantenidas() throws POOBSoccerException {
        iniciar(MODO_PVSP, GOLES_3);
        juego.presionarAccion(1, "RIGHT");
        juego.pausar();
        juego.reanudar();
        juego.actualizarTick(PASO_TICK);
        assertEquals(X_INICIAL_IZQUIERDA, centroX(juego.obtenerLimitesJugador(1)), 1e-9);
    }

    @Test
    public void soltarTodasLasAccionesDeberiaDetenerTeclasSinPausar() throws POOBSoccerException {
        iniciar(MODO_PVSP, GOLES_3);
        juego.presionarAccion(1, "RIGHT");
        juego.presionarAccion(2, "LEFT");
        juego.soltarTodasLasAcciones();
        juego.actualizarTick(1.0);
        assertEquals(X_INICIAL_IZQUIERDA, centroX(juego.obtenerLimitesJugador(1)), 1e-9);
        assertEquals(X_INICIAL_DERECHA, centroX(juego.obtenerLimitesJugador(2)), 1e-9);
        assertEquals("Meta: 3 goles | 00:01", juego.obtenerEstadoPartido());
    }

    @Test
    public void teclasMantenidasDeberianControlarJugadorHumano() throws POOBSoccerException {
        iniciar(MODO_PVSP, GOLES_3);
        juego.presionarAccion(1, "RIGHT");
        juego.actualizarTick(PASO_TICK);
        double desplazado = centroX(juego.obtenerLimitesJugador(1));
        assertTrue(desplazado > X_INICIAL_IZQUIERDA);
        juego.soltarAccion(1, "RIGHT");
        juego.actualizarTick(PASO_TICK);
        assertEquals(desplazado, centroX(juego.obtenerLimitesJugador(1)), 1e-9);

        double arriba = juego.obtenerLimitesJugador(1)[1];
        juego.presionarAccion(1, "JUMP");
        juego.actualizarTick(PASO_TICK);
        assertTrue(juego.obtenerLimitesJugador(1)[1] < arriba);
    }

    @Test
    public void teclasOpuestasMantenidasDeberianCancelarse() throws POOBSoccerException {
        iniciar(MODO_PVSP, GOLES_3);
        juego.presionarAccion(2, "LEFT");
        juego.presionarAccion(2, "RIGHT");
        juego.actualizarTick(PASO_TICK);
        assertEquals(X_INICIAL_DERECHA, centroX(juego.obtenerLimitesJugador(2)), 1e-9);
    }

    @Test
    public void tecladoNoDeberiaControlarMaquina() throws POOBSoccerException {
        iniciar(MODO_PVSM, GOLES_3);
        double antes = centroX(juego.obtenerLimitesJugador(2));
        assertTrue(juego.obtenerPosicionBalon()[0] < antes);
        juego.presionarAccion(2, "RIGHT");
        juego.presionarAccion(2, "POWER");
        juego.actualizarTick(PASO_TICK);
        assertTrue(centroX(juego.obtenerLimitesJugador(2)) < antes, "La IA debe seguir yendo hacia el balon");
    }

    @Test
    public void accionInvalidaDeberiaSerRechazada() throws POOBSoccerException {
        iniciar(MODO_PVSP, GOLES_3);
        POOBSoccerException excepcion = assertThrows(POOBSoccerException.class,
                () -> juego.presionarAccion(1, "FLY"));
        assertEquals(POOBSoccerException.ACCION_INVALIDA, excepcion.getMessage());
        excepcion = assertThrows(POOBSoccerException.class, () -> juego.soltarAccion(1, null));
        assertEquals(POOBSoccerException.ACCION_INVALIDA, excepcion.getMessage());
        excepcion = assertThrows(POOBSoccerException.class, () -> juego.presionarAccion(3, "KICK"));
        assertEquals(POOBSoccerException.JUGADOR_INVALIDO, excepcion.getMessage());
    }

    @Test
    public void ganadorAntesDelFinalDeberiaLanzarExcepcion() throws POOBSoccerException {
        iniciar(MODO_PVSP, GOLES_3);
        POOBSoccerException excepcion = assertThrows(POOBSoccerException.class, () -> juego.obtenerNombreGanador());
        assertEquals(POOBSoccerException.PARTIDO_EN_CURSO, excepcion.getMessage());
    }

    @Test
    public void iaDeberiaPerseguirElBalon() throws POOBSoccerException {
        iniciar(MODO_PVSM, GOLES_3);
        double antes = centroX(juego.obtenerLimitesJugador(2));
        juego.actualizarTick(PASO_TICK);
        assertTrue(centroX(juego.obtenerLimitesJugador(2)) < antes);
    }

    @Test
    public void maquinaVsMaquinaDeberiaEjecutarseSinErrores() throws POOBSoccerException {
        iniciar(MODO_MVSM, GOLES_3);
        for (int i = 0; i < 600; i++) {
            juego.actualizarTick(PASO_TICK);
        }
        assertTrue(juego.obtenerPosicionBalon()[0] >= 0.0 && juego.obtenerPosicionBalon()[0] <= Field.ANCHO);
    }

    // ---------------------------------------------------------------- catalogos

    @Test
    public void cadaPersonajeDeberiaTenerPoderUnico() throws POOBSoccerException {
        Set<String> poderes = new HashSet<String>();
        int cantidad = juego.obtenerNombresPersonajes().length;
        for (int i = 0; i < cantidad; i++) {
            assertTrue(poderes.add(juego.obtenerNombrePoderPersonaje(i)),
                    "Poder repetido: " + juego.obtenerNombrePoderPersonaje(i));
        }
        assertEquals(cantidad, poderes.size());
    }

    @Test
    public void catalogosDeberianListarOpcionesEnOrden() throws POOBSoccerException {
        assertEquals(3, juego.obtenerNombresModos().length);
        assertEquals(4, juego.obtenerNombresTiposPartido().length);
        assertEquals("Por Goles (3)", juego.obtenerNombresTiposPartido()[GOLES_3]);
        assertEquals("Jacob", juego.obtenerNombresPersonajes()[0]);
        CharacterCatalog catalogo = new CharacterCatalog();
        POOBSoccerException excepcion = assertThrows(POOBSoccerException.class, () -> catalogo.obtener(99));
        assertEquals(POOBSoccerException.PERSONAJE_INVALIDO, excepcion.getMessage());
    }

    @Test
    public void fachadaDeberiaExponerNombrePoderActivo() throws POOBSoccerException {
        iniciar(MODO_PVSP, GOLES_3);
        assertEquals(juego.obtenerNombrePoderPersonaje(0), juego.obtenerNombrePoderJugador(1));
    }

    @Test
    public void fachadaDeberiaExponerDatosPoderPersonaje() throws POOBSoccerException {
        assertEquals(3, juego.obtenerNivelPoderPersonaje(0));
        assertEquals(2, juego.obtenerNivelPoderPersonaje(1));
        assertEquals(4, juego.obtenerNivelPoderPersonaje(2));
        assertEquals(3, juego.obtenerNivelPoderPersonaje(3));
        assertFalse(juego.obtenerDescripcionPoderPersonaje(0).isEmpty());
        assertThrows(POOBSoccerException.class, () -> juego.obtenerNivelPoderPersonaje(99));
    }

    @Test
    public void fachadaDeberiaExponerDatosFichaPersonaje() throws POOBSoccerException {
        assertEquals("Canadá", juego.obtenerPaisPersonaje(0));
        assertEquals(3, juego.obtenerVelocidadPersonaje(0));
        assertEquals(3, juego.obtenerSaltoPersonaje(0));
        assertEquals(4, juego.obtenerFuerzaPersonaje(0));
        POOBSoccerException excepcion = assertThrows(POOBSoccerException.class,
                () -> juego.obtenerPaisPersonaje(-1));
        assertEquals(POOBSoccerException.PERSONAJE_INVALIDO, excepcion.getMessage());
    }

    @Test
    public void fachadaDeberiaExponerGeometriaDeLaFisica() {
        assertEquals(Field.ANCHO, juego.obtenerAnchoCancha(), 1e-9);
        assertEquals(Field.SUELO_Y, juego.obtenerSueloY(), 1e-9);
        assertEquals(Ball.RADIO, juego.obtenerRadioBalon(), 1e-9);
        assertEquals(Goal.GROSOR_BORDE, juego.obtenerGrosorTravesano(), 1e-9);
    }

    @Test
    public void fachadaDeberiaExponerUbicacionArcosEnLaCancha() throws POOBSoccerException {
        iniciar(MODO_PVSP, GOLES_3);
        double arriba = Field.SUELO_Y - Goal.ALTO;
        double[][] arcos = juego.obtenerLimitesArcos();
        assertEquals(2, arcos.length);
        assertArrayEquals(new double[] { 0.0, arriba, Goal.ANCHO, Goal.ALTO }, arcos[0], 1e-9);
        assertArrayEquals(new double[] { Field.ANCHO - Goal.ANCHO, arriba, Goal.ANCHO, Goal.ALTO }, arcos[1], 1e-9);
    }
}
