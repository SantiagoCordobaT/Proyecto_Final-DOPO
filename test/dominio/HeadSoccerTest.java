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
    private static final int POR_TIEMPO = 0;
    private static final int GOLES_3 = 1;
    private static final int SUPERVIVENCIA = 4;
    private static final double PASO_TICK = 1.0 / 60.0;

    // Valores esperados del enunciado y de las reglas del juego (el dominio los
    // mantiene privados)
    private static final double X_INICIAL_IZQUIERDA = 200.0;
    private static final double X_INICIAL_DERECHA = 700.0;
    private static final double SEGUNDOS_PARTIDO = 60.0;
    private static final String GOL_DE_ORO = "GOL DE ORO";
    private static final String CONGELADO = "Congelado";
    private static final String ATURDIDO = "Aturdido";
    /**
     * Fraccion de la barra que se recarga por segundo y por nivel de Poder Unico (2
     * %).
     */
    private static final double RECARGA_POR_NIVEL = 0.02;

    private HeadSoccer juego;

    @BeforeEach
    public void setUp() throws POOBSoccerException {
        juego = HeadSoccer.getInstance();
        juego.newGame();
    }

    private void start(int modo, int tipoPartido) throws POOBSoccerException {
        start(modo, tipoPartido, 0, 0);
    }

    private void start(int modo, int tipoPartido, int personaje1, int personaje2) throws POOBSoccerException {
        juego.selectMode(modo);
        juego.selectMatchType(tipoPartido);
        juego.selectPlayer(1, personaje1);
        juego.selectPlayer(2, personaje2);
        juego.startMatch();
    }

    private static boolean hasEffect(String[] nombresEfectos, String nombreEfecto) {
        return Arrays.asList(nombresEfectos).contains(nombreEfecto);
    }

    private static double centerX(double[] limites) {
        return limites[0] + limites[2] / 2.0;
    }

    /**
     * @return Balon quieto pegado al costado derecho del personaje, a media altura
     *         (al alcance de su patada).
     */
    private static Ball ballBeside(Character personaje) {
        double[] limites = personaje.getBounds();
        Ball balon = new Ball();
        balon.placeAt(limites[0] + limites[2] + 10.0, personaje.getCenterY());
        return balon;
    }

    /**
     * @return Partido creado directamente desde los catalogos, con copias de los
     *         personajes indicados.
     */
    private static Match match(int modo, int tipoPartido, int personaje1, int personaje2) throws POOBSoccerException {
        CharacterCatalog personajes = new CharacterCatalog();
        return new Match(new GameModeCatalog().get(modo), new MatchTypeCatalog().get(tipoPartido),
                personajes.get(personaje1).copy(), personajes.get(personaje2).copy(), personajes);
    }

    private static void score(Match partido, int anotador, int veces) throws POOBSoccerException {
        for (int i = 0; i < veces; i++) {
            partido.scoreGoal(anotador);
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
        public int getGoals(int jugador) {
            return goles[jugador - 1];
        }

        @Override
        public void replaceRival() {
            rivalesReemplazados++;
        }
    }

    // ---------------------------------------------------------------- estadisticas

    @Test
    public void shouldAcceptStatsBetweenOneAndFive() throws POOBSoccerException {
        Character personaje = new Character("A", "Canadá", 1, 3, 5, 3, new FreezePower());
        assertEquals(1, personaje.getSpeed());
        assertEquals(3, personaje.getJump());
        assertEquals(5, personaje.getStrength());
    }

    @Test
    public void shouldRejectStatsOutOfRange() {
        POOBSoccerException excepcion = assertThrows(POOBSoccerException.class,
                () -> new Character("A", "Canadá", 0, 3, 3, 3, new FreezePower()));
        assertEquals(POOBSoccerException.ESTADISTICAS_FUERA_DE_RANGO, excepcion.getMessage());
        assertThrows(POOBSoccerException.class, () -> new Character("A", "Canadá", 3, 6, 3, 3, new FreezePower()));
        assertThrows(POOBSoccerException.class, () -> new Character("A", "Canadá", 3, 3, -1, 3, new FreezePower()));
        assertThrows(POOBSoccerException.class, () -> new Character("A", "Canadá", 3, 3, 3, 6, new FreezePower()));
    }

    @Test
    public void copyShouldNotShareState() throws POOBSoccerException {
        Character original = new Character("A", "Canadá", 3, 3, 3, 3, new FreezePower());
        Character copia = original.copy();
        assertNotSame(original, copia);
        copia.stun();
        copia.addGoal();
        copia.update(1.0);
        assertEquals(0, original.getEffectNames().length);
        assertEquals(0, original.getGoals());
        assertEquals(0.0, original.getPowerCharge(), 1e-9);
        assertTrue(copia.getPowerCharge() > 0.0);
    }

    // ----------------------------------------------------------------
    // supervivencia

    @Test
    public void survivalShouldOnlyBeAllowedInPvsM() throws POOBSoccerException {
        juego.selectMode(MODO_PVSP);
        POOBSoccerException excepcion = assertThrows(POOBSoccerException.class,
                () -> juego.selectMatchType(SUPERVIVENCIA));
        assertEquals(POOBSoccerException.SUPERVIVENCIA_SOLO_PVSM, excepcion.getMessage());

        juego.selectMode(MODO_MVSM);
        assertThrows(POOBSoccerException.class, () -> juego.selectMatchType(SUPERVIVENCIA));

        start(MODO_PVSM, SUPERVIVENCIA, 0, 1);
        assertFalse(juego.isMatchFinished());
        assertEquals("Balones 3-3 | Racha 0", juego.getMatchStatus());
    }

    @Test
    public void selectingModeShouldClearMatchType() throws POOBSoccerException {
        juego.selectMode(MODO_PVSM);
        juego.selectMatchType(SUPERVIVENCIA);
        juego.selectMode(MODO_PVSP);
        juego.selectPlayer(1, 0);
        juego.selectPlayer(2, 1);
        POOBSoccerException excepcion = assertThrows(POOBSoccerException.class, () -> juego.startMatch());
        assertEquals(POOBSoccerException.MODO_INVALIDO, excepcion.getMessage());
    }

    @Test
    public void matchTypeShouldRequireMode() {
        POOBSoccerException excepcion = assertThrows(POOBSoccerException.class,
                () -> juego.selectMatchType(POR_TIEMPO));
        assertEquals(POOBSoccerException.MODO_INVALIDO, excepcion.getMessage());
    }

    @Test
    public void survivalShouldEndWhenPlayerOneRunsOutOfBalls() throws POOBSoccerException {
        Match partido = match(MODO_PVSM, SUPERVIVENCIA, 0, 1);
        score(partido, 2, 3);
        assertEquals("Balones 0-3 | Racha 0", partido.getStatus());
        assertTrue(partido.isFinished());
        assertEquals(partido.getPlayerName(2), partido.getWinnerName());
    }

    @Test
    public void survivalShouldContinueSeriesWhenMachineLosesAllBalls() throws POOBSoccerException {
        Match partido = match(MODO_PVSM, SUPERVIVENCIA, 0, 0);
        String primerRival = partido.getPlayerName(2);
        partido.updateTick(PASO_TICK);
        assertTrue(partido.getPlayerPowerCharge(2) > 0.0);

        score(partido, 1, 3);
        assertEquals(0.0, partido.getPlayerPowerCharge(2), 1e-9);
        assertEquals(0, partido.getGoals(2));
        assertFalse(partido.isFinished());
        assertEquals("Balones 3-3 | Racha 1", partido.getStatus());
        assertNotEquals(primerRival, partido.getPlayerName(2));
        assertEquals(X_INICIAL_DERECHA, centerX(partido.getPlayerBounds(2)), 1e-9);
    }

    @Test
    public void survivalScoreShouldRestartAgainstEachNewRival() throws POOBSoccerException {
        Match partido = match(MODO_PVSM, SUPERVIVENCIA, 0, 1);
        partido.scoreGoal(2);
        partido.updateTick(PASO_TICK);
        double cargaJugadorUno = partido.getPlayerPowerCharge(1);
        score(partido, 1, 3);
        assertEquals(0, partido.getGoals(1));
        assertEquals(0, partido.getGoals(2));
        assertEquals("Balones 2-3 | Racha 1", partido.getStatus());
        assertEquals(cargaJugadorUno, partido.getPlayerPowerCharge(1), 1e-9);
        partido.scoreGoal(1);
        assertEquals(1, partido.getGoals(1));
    }

    @Test
    public void survivalShouldEndOnlyWhenPlayerOneLosesAllBalls() throws POOBSoccerException {
        Match partido = match(MODO_PVSM, SUPERVIVENCIA, 0, 1);
        score(partido, 1, 3);
        assertEquals("Balones 3-3 | Racha 1", partido.getStatus());
        score(partido, 2, 2);
        assertFalse(partido.isFinished());
        partido.scoreGoal(2);
        assertTrue(partido.isFinished());
        assertEquals("Balones 0-3 | Racha 1", partido.getStatus());
    }

    @Test
    public void survivalShouldAskContextForNewRival() throws POOBSoccerException {
        MatchType tipo = new MatchTypeCatalog().get(SUPERVIVENCIA);
        StubContext contexto = new StubContext();
        tipo.begin(contexto);
        for (int i = 0; i < 3; i++) {
            tipo.goalScored(1);
        }
        assertEquals(1, contexto.rivalesReemplazados);
        assertEquals("Balones 3-3 | Racha 1", tipo.getStatusText());
        assertFalse(tipo.isFinished());
    }

    @Test
    public void catalogShouldCycleRivals() throws POOBSoccerException {
        String[] nombres = juego.getCharacterNames();
        Match partido = match(MODO_PVSM, SUPERVIVENCIA, 0, nombres.length - 1);
        for (int rival = 0; rival < 2; rival++) {
            score(partido, 1, 3);
            assertEquals(nombres[rival], partido.getPlayerName(2));
        }
        assertEquals("Balones 3-3 | Racha 2", partido.getStatus());
    }

    // ---------------------------------------------------------------- poderes

    @Test
    public void powerShouldOnlyActivateWhenBarIsFull() throws POOBSoccerException {
        start(MODO_PVSP, POR_TIEMPO);
        assertEquals(0.0, juego.getPlayerPowerBar(1), 1e-9);
        juego.pressAction(1, "POWER");
        juego.updateTick(PASO_TICK);
        assertFalse(juego.isPlayerPowerArmed(1));

        juego.updateTick(17.0);
        assertEquals(1.0, juego.getPlayerPowerBar(1), 1e-9);
        juego.updateTick(PASO_TICK);
        assertTrue(juego.isPlayerPowerArmed(1));
    }

    @Test
    public void powerBarShouldRechargeOverTime() throws POOBSoccerException {
        start(MODO_PVSP, POR_TIEMPO);
        juego.updateTick(1.0);
        assertEquals(RECARGA_POR_NIVEL * juego.getCharacterPowerLevel(0), juego.getPlayerPowerBar(1), 1e-6);
    }

    @Test
    public void freezePowerShouldBlockOpponentActions() throws POOBSoccerException {
        Character victima = new Character("B", "Canadá", 3, 3, 3, 3, new FreezePower());
        Character rival = new Character("C", "Japón", 3, 3, 3, 3, new StunPower());
        victima.enterAt(400.0, 1);
        rival.enterAt(X_INICIAL_DERECHA, -1);
        double arriba = victima.getTop();
        new FreezePower().applyEffect(victima);
        assertTrue(hasEffect(victima.getEffectNames(), CONGELADO));
        victima.requestKick();
        assertFalse(victima.tryKick(ballBeside(victima), rival));
        victima.jump();
        victima.setMoveDirection(1);
        victima.update(0.5);
        assertEquals(400.0, victima.getX(), 1e-9);
        assertEquals(arriba, victima.getTop(), 1e-9);
    }

    @Test
    public void shrinkPowerShouldHalveOpponentSize() throws POOBSoccerException {
        Character victima = new Character("B", "Japón", 3, 3, 3, 3, new ShrinkPower());
        double ancho = victima.getBounds()[2];
        new ShrinkPower().applyEffect(victima);
        assertEquals(ancho / 2.0, victima.getBounds()[2], 1e-9);
    }

    @Test
    public void confusionPowerShouldInvertHorizontalControls() throws POOBSoccerException {
        Character victima = new Character("B", "Brasil", 3, 3, 3, 3, new ConfusionPower());
        victima.enterAt(400.0, 1);
        new ConfusionPower().applyEffect(victima);
        victima.setMoveDirection(1);
        victima.update(0.5);
        assertTrue(victima.getX() < 400.0);
    }

    @Test
    public void kickWithActivePowerShouldAffectOpponent() throws POOBSoccerException {
        Character pateador = new Character("A", "Canadá", 3, 3, 3, 3, new FreezePower());
        Character rival = new Character("B", "Japón", 3, 3, 3, 3, new StunPower());
        pateador.enterAt(400.0, 1);
        rival.enterAt(X_INICIAL_DERECHA, -1);
        pateador.update(17.0);
        pateador.armPower();
        assertTrue(pateador.isPowerArmed());
        pateador.requestKick();
        assertTrue(pateador.tryKick(ballBeside(pateador), rival));
        assertTrue(hasEffect(rival.getEffectNames(), CONGELADO));
        assertFalse(pateador.isPowerArmed());
        assertEquals(0.0, pateador.getPowerCharge(), 1e-9);
    }

    @Test
    public void stunPowerShouldStunOpponent() throws POOBSoccerException {
        Character victima = new Character("B", "Japón", 3, 3, 3, 3, new StunPower());
        new StunPower().applyEffect(victima);
        assertTrue(hasEffect(victima.getEffectNames(), ATURDIDO));
    }

    // ---------------------------------------------------------------- aturdimiento

    @Test
    public void stunShouldLastThreeSeconds() throws POOBSoccerException {
        Character personaje = new Character("A", "Canadá", 3, 3, 3, 3, new FreezePower());
        Character rival = new Character("B", "Japón", 3, 3, 3, 3, new StunPower());
        personaje.enterAt(400.0, 1);
        rival.enterAt(X_INICIAL_DERECHA, -1);
        double arriba = personaje.getTop();
        personaje.stun();
        assertTrue(hasEffect(personaje.getEffectNames(), ATURDIDO));
        personaje.requestKick();
        assertFalse(personaje.tryKick(ballBeside(personaje), rival));
        personaje.jump();
        personaje.setMoveDirection(1);
        personaje.update(2.9);
        assertTrue(hasEffect(personaje.getEffectNames(), ATURDIDO));
        assertEquals(400.0, personaje.getX(), 1e-9);
        assertEquals(arriba, personaje.getTop(), 1e-9);
        personaje.update(0.2);
        assertEquals(0, personaje.getEffectNames().length);
        personaje.requestKick();
        assertTrue(personaje.tryKick(ballBeside(personaje), rival));
    }

    @Test
    public void meleeKickShouldStunOpponentForThreeSeconds() throws POOBSoccerException {
        Character personajeA = new Character("A", "Canadá", 3, 3, 3, 3, new FreezePower());
        Character personajeB = new Character("B", "Japón", 3, 3, 3, 3, new StunPower());
        personajeA.enterAt(400.0, 1);
        double[] limitesA = personajeA.getBounds();
        personajeB.enterAt(limitesA[0] + limitesA[2] + 5.0 + personajeB.getBounds()[2] / 2.0, -1);
        Ball balonLejano = new Ball();
        balonLejano.placeAt(Field.ANCHO - Goal.ANCHO, Ball.RADIO);
        personajeA.requestKick();
        assertFalse(personajeA.tryKick(balonLejano, personajeB));
        assertTrue(hasEffect(personajeB.getEffectNames(), ATURDIDO));
        personajeB.requestKick();
        assertFalse(personajeB.tryKick(ballBeside(personajeB), personajeA));
        personajeB.update(2.9);
        assertTrue(hasEffect(personajeB.getEffectNames(), ATURDIDO));
        personajeB.update(0.2);
        assertEquals(0, personajeB.getEffectNames().length);
    }

    @Test
    public void meleeKickOutOfRangeShouldNotStun() throws POOBSoccerException {
        start(MODO_PVSP, POR_TIEMPO);
        juego.pressAction(1, "KICK");
        juego.updateTick(PASO_TICK);
        assertEquals(0, juego.getPlayerEffects(2).length);
    }

    @Test
    public void kickShouldBeVisibleWhileItLasts() throws POOBSoccerException {
        Character personajeA = new Character("A", "Canadá", 3, 3, 3, 3, new FreezePower());
        Character personajeB = new Character("B", "Japón", 3, 3, 3, 3, new StunPower());
        personajeA.enterAt(400.0, 1);
        personajeB.enterAt(400.0, -1);
        assertFalse(personajeA.isKicking());
        personajeA.requestKick();
        personajeA.tryKick(ballBeside(personajeA), personajeB);
        assertTrue(personajeA.isKicking());
        personajeA.update(1.0);
        assertFalse(personajeA.isKicking());
    }

    @Test
    public void facadeShouldExposeFacingAndKicking() throws POOBSoccerException {
        start(MODO_PVSP, POR_TIEMPO);
        assertEquals(1, juego.getPlayerFacing(1));
        assertEquals(-1, juego.getPlayerFacing(2));
        assertFalse(juego.isPlayerKicking(1));
    }

    @Test
    public void ballShouldLookAheadBetweenPhysicsStepsWithoutLeavingTheField() {
        Field cancha = new Field();
        Ball balon = new Ball();
        balon.placeAt(450.0, 100.0);
        balon.kick(5, 1);
        assertArrayEquals(new double[] { 450.0, 100.0 }, cancha.visibleBall(balon, 0.0), 1e-9);
        assertArrayEquals(new double[] { 455.0, 95.0 }, cancha.visibleBall(balon, 0.5), 1e-9);
        balon.placeAt(Field.ANCHO - Ball.RADIO, 100.0);
        balon.kick(5, 1);
        assertEquals(Field.ANCHO - Ball.RADIO, cancha.visibleBall(balon, 0.5)[0], 1e-9);
    }

    @Test
    public void restartShouldClearEffectsAndReturnHome() throws POOBSoccerException {
        Character personaje = new Character("A", "Canadá", 3, 3, 3, 3, new FreezePower());
        personaje.enterAt(400.0, 1);
        personaje.setMoveDirection(1);
        personaje.update(0.5);
        personaje.stun();
        personaje.addGoal();
        personaje.restart();
        assertEquals(0, personaje.getEffectNames().length);
        assertEquals(400.0, personaje.getX(), 1e-9);
        assertEquals(1, personaje.getGoals());
        assertTrue(personaje.getPowerCharge() > 0.0);
    }

    @Test
    public void enterAtShouldResetGoalsAndPowerBar() throws POOBSoccerException {
        Character personaje = new Character("A", "Canadá", 3, 3, 3, 3, new FreezePower());
        personaje.addGoal();
        personaje.update(1.0);
        personaje.enterAt(X_INICIAL_DERECHA, -1);
        assertEquals(0, personaje.getGoals());
        assertEquals(0.0, personaje.getPowerCharge(), 1e-9);
        assertEquals(X_INICIAL_DERECHA, personaje.getX(), 1e-9);
        assertEquals(-1, personaje.getFacing());
    }

    // ---------------------------------------------------------------- goles

    @Test
    public void fieldShouldCreditTheAttackerOfEachGoal() {
        Field cancha = new Field();
        Ball balon = new Ball();
        balon.placeAt(Goal.ANCHO / 2.0, Field.SUELO_Y - Ball.RADIO);
        assertEquals(2, cancha.scorerOf(balon));
        balon.placeAt(Field.ANCHO - Goal.ANCHO / 2.0, Field.SUELO_Y - Ball.RADIO);
        assertEquals(1, cancha.scorerOf(balon));
        cancha.dropBall(balon);
        assertEquals(Field.SIN_GOL, cancha.scorerOf(balon));
        assertEquals(Field.ANCHO / 2.0, balon.getX(), 1e-9);
    }

    @Test
    public void bodyPushAgainstAWallShouldKeepTheBallInsideTheField() throws POOBSoccerException {
        Field cancha = new Field();
        Character personaje = new Character("A", "Canadá", 3, 3, 3, 3, new FreezePower());
        personaje.enterAt(personaje.getBounds()[2] / 2.0, 1);
        Ball balon = new Ball();
        balon.placeAt(Ball.RADIO, Field.SUELO_Y - Ball.RADIO);
        assertTrue(personaje.touches(balon));
        personaje.push(balon);
        assertTrue(balon.getX() < Ball.RADIO);
        cancha.keepBallInside(balon);
        assertEquals(Ball.RADIO, balon.getX(), 1e-9);
    }

    @Test
    public void ballShouldBounceBackIntoPlayFromASideWall() {
        Field cancha = new Field();
        Ball balon = new Ball();
        balon.placeAt(Field.ANCHO - 60.0, 100.0);
        balon.kick(5, 1);
        double maximoX = 0.0;
        for (int paso = 0; paso < 120; paso++) {
            cancha.updateBall(balon);
            cancha.keepBallInside(balon);
            maximoX = Math.max(maximoX, balon.getX());
        }
        assertEquals(Field.ANCHO - Ball.RADIO, maximoX, 1e-9);
        assertTrue(balon.getX() < Field.ANCHO - 200.0, "El balon debe volver al juego tras rebotar en la pared");
    }

    @Test
    public void ballPartiallyInsideShouldNotBeGoal() {
        Goal arco = new Goal(0.0, Goal.ANCHO, 2);
        Ball parcial = new Ball();
        parcial.placeAt(Goal.ANCHO, Field.SUELO_Y - Ball.RADIO - 5.0);
        assertFalse(arco.isBallInside(parcial));
        Ball adentro = new Ball();
        adentro.placeAt(Goal.ANCHO - Ball.RADIO, Field.SUELO_Y - Ball.RADIO - 5.0);
        assertTrue(arco.isBallInside(adentro));
    }

    @Test
    public void ballShouldBounceOnCrossbar() {
        Goal arco = new Goal(0.0, Goal.ANCHO, 2);
        double travesanoArriba = Field.SUELO_Y - Goal.ALTO;
        Ball balon = new Ball();
        balon.placeAt(Goal.ANCHO / 2.0, travesanoArriba - Ball.RADIO + 3.0);
        arco.resolveCrossbar(balon);
        assertEquals(travesanoArriba - Ball.RADIO, balon.getY(), 1e-9);
    }

    @Test
    public void goalShouldOnlyCountForTheScorer() throws POOBSoccerException {
        Match partido = match(MODO_PVSP, GOLES_3, 0, 0);
        partido.scoreGoal(1);
        assertEquals(1, partido.getGoals(1));
        assertEquals(0, partido.getGoals(2));
        partido.scoreGoal(2);
        assertEquals(1, partido.getGoals(2));
        POOBSoccerException excepcion = assertThrows(POOBSoccerException.class, () -> partido.scoreGoal(3));
        assertEquals(POOBSoccerException.JUGADOR_INVALIDO, excepcion.getMessage());
    }

    @Test
    public void shouldRestartPlayAfterGoal() throws POOBSoccerException {
        CharacterCatalog personajes = new CharacterCatalog();
        Character jugador1 = personajes.get(0).copy();
        Match partido = new Match(new GameModeCatalog().get(MODO_PVSP), new MatchTypeCatalog().get(GOLES_3), jugador1,
                personajes.get(1).copy(), personajes);
        partido.press(1, Action.RIGHT);
        partido.updateTick(0.5);
        assertTrue(centerX(partido.getPlayerBounds(1)) > X_INICIAL_IZQUIERDA);
        jugador1.stun();
        partido.scoreGoal(1);
        assertEquals(X_INICIAL_IZQUIERDA, centerX(partido.getPlayerBounds(1)), 1e-9);
        assertEquals(X_INICIAL_DERECHA, centerX(partido.getPlayerBounds(2)), 1e-9);
        assertEquals(0, partido.getPlayerEffects(1).length);
        assertEquals(Field.ANCHO / 2.0, partido.getBallPosition()[0], 1e-9);
    }

    @Test
    public void goalMatchShouldEndAtTarget() throws POOBSoccerException {
        Match partido = match(MODO_PVSP, GOLES_3, 0, 0);
        score(partido, 1, 2);
        assertFalse(partido.isFinished());
        partido.scoreGoal(1);
        assertTrue(partido.isFinished());
        assertEquals(partido.getPlayerName(1), partido.getWinnerName());
    }

    @Test
    public void goalMatchStatusShouldShowTarget() throws POOBSoccerException {
        start(MODO_PVSP, GOLES_3);
        assertEquals("Meta: 3 goles | 00:00", juego.getMatchStatus());
        juego.updateTick(0.5);
        assertEquals("Meta: 3 goles | 00:00", juego.getMatchStatus());
        juego.updateTick(0.5);
        assertEquals("Meta: 3 goles | 00:01", juego.getMatchStatus());
        juego.updateTick(0.99);
        assertEquals("Meta: 3 goles | 00:01", juego.getMatchStatus());
    }

    @Test
    public void goalScoredAfterTheEndShouldBeIgnored() throws POOBSoccerException {
        MatchType tipo = new MatchTypeCatalog().get(GOLES_3);
        StubContext contexto = new StubContext();
        tipo.begin(contexto);
        contexto.goles[0] = 3;
        tipo.goalScored(1);
        contexto.goles[1] = 3;
        tipo.goalScored(2);
        assertTrue(tipo.isFinished());
        assertEquals(1, tipo.getWinner());

        tipo.begin(new StubContext());
        assertFalse(tipo.isFinished());
    }

    // ---------------------------------------------------------------- tiempo y gol
    // de oro

    @Test
    public void timeMatchShouldGiveWinToLeaderAfterSixtySeconds() throws POOBSoccerException {
        Match partido = match(MODO_PVSP, POR_TIEMPO, 0, 0);
        partido.scoreGoal(1);
        partido.updateTick(PASO_TICK);
        assertFalse(partido.isFinished());
        partido.updateTick(SEGUNDOS_PARTIDO);
        assertTrue(partido.isFinished());
        assertEquals("00:00", partido.getStatus());
        assertEquals(partido.getPlayerName(1), partido.getWinnerName());
    }

    @Test
    public void tieShouldActivateGoldenGoal() throws POOBSoccerException {
        Match partido = match(MODO_PVSP, POR_TIEMPO, 0, 1);
        partido.updateTick(SEGUNDOS_PARTIDO);
        assertEquals(GOL_DE_ORO, partido.getStatus());
        assertFalse(partido.isFinished());

        partido.scoreGoal(2);
        assertTrue(partido.isFinished());
        assertEquals(1, partido.getGoals(2));
        assertEquals(partido.getPlayerName(2), partido.getWinnerName());
    }

    @Test
    public void timeMatchShouldCountDownAgainstAnyContext() throws POOBSoccerException {
        MatchType tipo = new MatchTypeCatalog().get(POR_TIEMPO);
        StubContext contexto = new StubContext();
        tipo.begin(contexto);
        assertEquals("01:00", tipo.getStatusText());
        tipo.tick(0.5);
        assertEquals("01:00", tipo.getStatusText());
        tipo.tick(0.5);
        assertEquals("00:59", tipo.getStatusText());
        contexto.goles[1] = 2;
        tipo.tick(SEGUNDOS_PARTIDO);
        assertTrue(tipo.isFinished());
        assertEquals(2, tipo.getWinner());
    }

    // ---------------------------------------------------------------- facade,
    // entrada, pausa e IA

    @Test
    public void shouldNotStartWithoutBothPlayers() throws POOBSoccerException {
        juego.selectMode(MODO_PVSP);
        juego.selectMatchType(POR_TIEMPO);
        juego.selectPlayer(1, 0);
        POOBSoccerException excepcion = assertThrows(POOBSoccerException.class, () -> juego.startMatch());
        assertEquals(POOBSoccerException.JUGADORES_INCOMPLETOS, excepcion.getMessage());
    }

    @Test
    public void shouldRejectInvalidSelections() throws POOBSoccerException {
        POOBSoccerException excepcion = assertThrows(POOBSoccerException.class, () -> juego.selectPlayer(1, 99));
        assertEquals(POOBSoccerException.PERSONAJE_INVALIDO, excepcion.getMessage());
        excepcion = assertThrows(POOBSoccerException.class, () -> juego.selectPlayer(3, 0));
        assertEquals(POOBSoccerException.JUGADOR_INVALIDO, excepcion.getMessage());
        assertThrows(POOBSoccerException.class, () -> juego.selectPlayer(0, 0));
        assertThrows(POOBSoccerException.class, () -> juego.selectMode(-1));
        juego.selectMode(MODO_PVSP);
        excepcion = assertThrows(POOBSoccerException.class, () -> juego.selectMatchType(99));
        assertEquals(POOBSoccerException.MODO_INVALIDO, excepcion.getMessage());
        excepcion = assertThrows(POOBSoccerException.class, () -> juego.updateTick(PASO_TICK));
        assertEquals(POOBSoccerException.PARTIDO_NO_INICIADO, excepcion.getMessage());
        assertThrows(POOBSoccerException.class, () -> juego.pressAction(1, "KICK"));
        assertThrows(POOBSoccerException.class, () -> juego.getMatchStatus());
    }

    @Test
    public void singletonShouldBeUnique() throws POOBSoccerException {
        assertSame(juego, HeadSoccer.getInstance());
    }

    @Test
    public void newGameShouldClearSession() throws POOBSoccerException {
        start(MODO_PVSP, POR_TIEMPO);
        juego.newGame();
        POOBSoccerException excepcion = assertThrows(POOBSoccerException.class, () -> juego.getMatchStatus());
        assertEquals(POOBSoccerException.PARTIDO_NO_INICIADO, excepcion.getMessage());
        excepcion = assertThrows(POOBSoccerException.class, () -> juego.startMatch());
        assertEquals(POOBSoccerException.MODO_INVALIDO, excepcion.getMessage());
        juego.selectMode(MODO_PVSP);
        juego.selectMatchType(POR_TIEMPO);
        excepcion = assertThrows(POOBSoccerException.class, () -> juego.startMatch());
        assertEquals(POOBSoccerException.JUGADORES_INCOMPLETOS, excepcion.getMessage());
    }

    @Test
    public void rematchShouldStartFreshWithTheChosenCharacters() throws POOBSoccerException {
        String[] nombres = juego.getCharacterNames();
        start(MODO_PVSP, POR_TIEMPO, 0, 1);
        juego.pressAction(1, "RIGHT");
        juego.updateTick(1.0);
        assertTrue(juego.getPlayerPowerBar(2) > 0.0);

        juego.startMatch();
        assertEquals(nombres[0], juego.getPlayerName(1));
        assertEquals(nombres[1], juego.getPlayerName(2));
        assertEquals(0.0, juego.getPlayerPowerBar(2), 1e-9);
        assertEquals("01:00", juego.getMatchStatus());
        juego.updateTick(PASO_TICK);
        assertEquals(X_INICIAL_IZQUIERDA, centerX(juego.getPlayerBounds(1)), 1e-9);
    }

    @Test
    public void pausedGameShouldNotAdvance() throws POOBSoccerException {
        start(MODO_PVSP, POR_TIEMPO);
        assertEquals("01:00", juego.getMatchStatus());
        juego.pause();
        double y = juego.getBallPosition()[1];
        juego.updateTick(1.0);
        assertEquals(y, juego.getBallPosition()[1], 1e-9);
        assertEquals("01:00", juego.getMatchStatus());
        juego.resume();
        juego.updateTick(1.0);
        assertEquals("00:59", juego.getMatchStatus());
    }

    @Test
    public void pauseShouldReleaseHeldKeys() throws POOBSoccerException {
        start(MODO_PVSP, POR_TIEMPO);
        juego.pressAction(1, "RIGHT");
        juego.pause();
        juego.resume();
        juego.updateTick(PASO_TICK);
        assertEquals(X_INICIAL_IZQUIERDA, centerX(juego.getPlayerBounds(1)), 1e-9);
    }

    @Test
    public void releasingAllActionsShouldStopHeldKeysWithoutPausing() throws POOBSoccerException {
        start(MODO_PVSP, POR_TIEMPO);
        juego.pressAction(1, "RIGHT");
        juego.pressAction(2, "LEFT");
        juego.releaseAllActions();
        juego.updateTick(1.0);
        assertEquals(X_INICIAL_IZQUIERDA, centerX(juego.getPlayerBounds(1)), 1e-9);
        assertEquals(X_INICIAL_DERECHA, centerX(juego.getPlayerBounds(2)), 1e-9);
        assertEquals("00:59", juego.getMatchStatus());
    }

    @Test
    public void heldKeysShouldDriveHumanPlayer() throws POOBSoccerException {
        start(MODO_PVSP, POR_TIEMPO);
        juego.pressAction(1, "RIGHT");
        juego.updateTick(PASO_TICK);
        double desplazado = centerX(juego.getPlayerBounds(1));
        assertTrue(desplazado > X_INICIAL_IZQUIERDA);
        juego.releaseAction(1, "RIGHT");
        juego.updateTick(PASO_TICK);
        assertEquals(desplazado, centerX(juego.getPlayerBounds(1)), 1e-9);

        double arriba = juego.getPlayerBounds(1)[1];
        juego.pressAction(1, "JUMP");
        juego.updateTick(PASO_TICK);
        assertTrue(juego.getPlayerBounds(1)[1] < arriba);
    }

    @Test
    public void oppositeHeldKeysShouldCancel() throws POOBSoccerException {
        start(MODO_PVSP, POR_TIEMPO);
        juego.pressAction(2, "LEFT");
        juego.pressAction(2, "RIGHT");
        juego.updateTick(PASO_TICK);
        assertEquals(X_INICIAL_DERECHA, centerX(juego.getPlayerBounds(2)), 1e-9);
    }

    @Test
    public void keyboardShouldNotDriveMachine() throws POOBSoccerException {
        start(MODO_PVSM, GOLES_3);
        double antes = centerX(juego.getPlayerBounds(2));
        assertTrue(juego.getBallPosition()[0] < antes);
        juego.pressAction(2, "RIGHT");
        juego.pressAction(2, "POWER");
        juego.updateTick(PASO_TICK);
        assertTrue(centerX(juego.getPlayerBounds(2)) < antes, "La IA debe seguir yendo hacia el balon");
    }

    @Test
    public void invalidActionShouldBeRejected() throws POOBSoccerException {
        start(MODO_PVSP, POR_TIEMPO);
        POOBSoccerException excepcion = assertThrows(POOBSoccerException.class, () -> juego.pressAction(1, "FLY"));
        assertEquals(POOBSoccerException.ACCION_INVALIDA, excepcion.getMessage());
        excepcion = assertThrows(POOBSoccerException.class, () -> juego.releaseAction(1, null));
        assertEquals(POOBSoccerException.ACCION_INVALIDA, excepcion.getMessage());
        excepcion = assertThrows(POOBSoccerException.class, () -> juego.pressAction(3, "KICK"));
        assertEquals(POOBSoccerException.JUGADOR_INVALIDO, excepcion.getMessage());
    }

    @Test
    public void winnerBeforeTheEndShouldThrow() throws POOBSoccerException {
        start(MODO_PVSP, POR_TIEMPO);
        POOBSoccerException excepcion = assertThrows(POOBSoccerException.class, () -> juego.getWinnerName());
        assertEquals(POOBSoccerException.PARTIDO_EN_CURSO, excepcion.getMessage());
    }

    @Test
    public void aiShouldChaseTheBall() throws POOBSoccerException {
        start(MODO_PVSM, GOLES_3);
        double antes = centerX(juego.getPlayerBounds(2));
        juego.updateTick(PASO_TICK);
        assertTrue(centerX(juego.getPlayerBounds(2)) < antes);
    }

    @Test
    public void machineVsMachineShouldRunWithoutErrors() throws POOBSoccerException {
        start(MODO_MVSM, POR_TIEMPO);
        for (int i = 0; i < 600; i++) {
            juego.updateTick(PASO_TICK);
        }
        assertTrue(juego.getBallPosition()[0] >= 0.0 && juego.getBallPosition()[0] <= Field.ANCHO);
    }

    // ---------------------------------------------------------------- catalogos

    @Test
    public void everyCharacterShouldHaveAUniquePower() throws POOBSoccerException {
        Set<String> poderes = new HashSet<String>();
        int cantidad = juego.getCharacterNames().length;
        for (int i = 0; i < cantidad; i++) {
            assertTrue(poderes.add(juego.getCharacterPowerName(i)), "Poder repetido: " + juego.getCharacterPowerName(i));
        }
        assertEquals(cantidad, poderes.size());
    }

    @Test
    public void catalogsShouldListOptionsInOrder() throws POOBSoccerException {
        assertEquals(3, juego.getModeNames().length);
        assertEquals(5, juego.getMatchTypeNames().length);
        assertEquals("Por Goles (3)", juego.getMatchTypeNames()[GOLES_3]);
        assertEquals("Jacob", juego.getCharacterNames()[0]);
        CharacterCatalog catalogo = new CharacterCatalog();
        POOBSoccerException excepcion = assertThrows(POOBSoccerException.class, () -> catalogo.get(99));
        assertEquals(POOBSoccerException.PERSONAJE_INVALIDO, excepcion.getMessage());
    }

    @Test
    public void facadeShouldExposeActivePowerName() throws POOBSoccerException {
        start(MODO_PVSP, POR_TIEMPO);
        assertEquals(juego.getCharacterPowerName(0), juego.getPlayerPowerName(1));
    }

    @Test
    public void facadeShouldExposeCharacterPowerData() throws POOBSoccerException {
        assertEquals(3, juego.getCharacterPowerLevel(0));
        assertEquals(2, juego.getCharacterPowerLevel(1));
        assertEquals(4, juego.getCharacterPowerLevel(2));
        assertEquals(3, juego.getCharacterPowerLevel(3));
        assertFalse(juego.getCharacterPowerDescription(0).isEmpty());
        assertThrows(POOBSoccerException.class, () -> juego.getCharacterPowerLevel(99));
    }

    @Test
    public void facadeShouldExposeCharacterCardData() throws POOBSoccerException {
        assertEquals("Canadá", juego.getCharacterCountry(0));
        assertEquals(3, juego.getCharacterSpeed(0));
        assertEquals(3, juego.getCharacterJump(0));
        assertEquals(4, juego.getCharacterStrength(0));
        POOBSoccerException excepcion = assertThrows(POOBSoccerException.class, () -> juego.getCharacterCountry(-1));
        assertEquals(POOBSoccerException.PERSONAJE_INVALIDO, excepcion.getMessage());
    }

    @Test
    public void facadeShouldExposeTheGeometryThePhysicsUses() {
        assertEquals(Field.ANCHO, juego.getFieldWidth(), 1e-9);
        assertEquals(Field.SUELO_Y, juego.getGroundY(), 1e-9);
        assertEquals(Ball.RADIO, juego.getBallRadius(), 1e-9);
        assertEquals(Goal.GROSOR_TRAVESANO, juego.getCrossbarThickness(), 1e-9);
    }

    @Test
    public void facadeShouldExposeWhereTheFieldPlacesEachGoal() throws POOBSoccerException {
        start(MODO_PVSP, POR_TIEMPO);
        double arriba = Field.SUELO_Y - Goal.ALTO;
        double[][] arcos = juego.getGoalBounds();
        assertEquals(2, arcos.length);
        assertArrayEquals(new double[] { 0.0, arriba, Goal.ANCHO, Goal.ALTO }, arcos[0], 1e-9);
        assertArrayEquals(new double[] { Field.ANCHO - Goal.ANCHO, arriba, Goal.ANCHO, Goal.ALTO }, arcos[1], 1e-9);
    }
}
