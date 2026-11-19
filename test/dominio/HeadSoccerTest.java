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

    private static final int PVSP = 0;
    private static final int PVSM = 1;
    private static final int MVSM = 2;
    private static final int TIME = 0;
    private static final int GOALS_3 = 1;
    private static final int SURVIVAL = 4;
    private static final double TICK = 1.0 / 60.0;

    // Valores esperados del enunciado y de las reglas del juego (el dominio los mantiene privados)
    private static final double LEFT_HOME_X = 200.0;
    private static final double RIGHT_HOME_X = 700.0;
    private static final double MATCH_SECONDS = 60.0;
    private static final String GOLDEN_GOAL = "GOL DE ORO";
    private static final String FROZEN = "Congelado";
    private static final String STUNNED = "Aturdido";
    /** Fraccion de la barra que se recarga por segundo y por nivel de Poder Unico (2 %). */
    private static final double RECHARGE_PER_LEVEL = 0.02;

    private HeadSoccer game;

    @BeforeEach
    public void setUp() throws POOBSoccerException {
        game = HeadSoccer.getInstance();
        game.newGame();
    }

    private void start(int mode, int matchType) throws POOBSoccerException {
        start(mode, matchType, 0, 0);
    }

    private void start(int mode, int matchType, int character1, int character2) throws POOBSoccerException {
        game.selectMode(mode);
        game.selectMatchType(matchType);
        game.selectPlayer(1, character1);
        game.selectPlayer(2, character2);
        game.startMatch();
    }

    private static boolean hasEffect(String[] effectNames, String effectName) {
        return Arrays.asList(effectNames).contains(effectName);
    }

    private static double centerX(double[] bounds) {
        return bounds[0] + bounds[2] / 2.0;
    }

    /** @return Balon quieto pegado al costado derecho del personaje, a media altura (al alcance de su patada). */
    private static Ball ballBeside(Character character) {
        double[] bounds = character.getBounds();
        Ball ball = new Ball();
        ball.placeAt(bounds[0] + bounds[2] + 10.0, character.getCenterY());
        return ball;
    }

    /** @return Partido creado directamente desde los catalogos, con copias de los personajes indicados. */
    private static Match match(int mode, int matchType, int character1, int character2) throws POOBSoccerException {
        CharacterCatalog characters = new CharacterCatalog();
        return new Match(new GameModeCatalog().get(mode), new MatchTypeCatalog().get(matchType),
                characters.get(character1).copy(), characters.get(character2).copy(), characters);
    }

    private static void score(Match match, int scorer, int times) throws POOBSoccerException {
        for (int i = 0; i < times; i++) {
            match.scoreGoal(scorer);
        }
    }

    /** Partido simulado para probar los tipos de partido sin cancha ni personajes (Protected Variations). */
    private static final class StubContext implements MatchContext {

        private final int[] goals = new int[2];
        private int rivalsReplaced;

        @Override
        public int getGoals(int player) {
            return goals[player - 1];
        }

        @Override
        public void replaceRival() {
            rivalsReplaced++;
        }
    }

    // ---------------------------------------------------------------- estadisticas

    @Test
    public void shouldAcceptStatsBetweenOneAndFive() throws POOBSoccerException {
        Character c = new Character("A", "Canadá", 1, 3, 5, 3, new FreezePower());
        assertEquals(1, c.getSpeed());
        assertEquals(3, c.getJump());
        assertEquals(5, c.getStrength());
    }

    @Test
    public void shouldRejectStatsOutOfRange() {
        POOBSoccerException e = assertThrows(POOBSoccerException.class,
                () -> new Character("A", "Canadá", 0, 3, 3, 3, new FreezePower()));
        assertEquals(POOBSoccerException.ESTADISTICAS_FUERA_DE_RANGO, e.getMessage());
        assertThrows(POOBSoccerException.class, () -> new Character("A", "Canadá", 3, 6, 3, 3, new FreezePower()));
        assertThrows(POOBSoccerException.class, () -> new Character("A", "Canadá", 3, 3, -1, 3, new FreezePower()));
        assertThrows(POOBSoccerException.class, () -> new Character("A", "Canadá", 3, 3, 3, 6, new FreezePower()));
    }

    @Test
    public void copyShouldNotShareState() throws POOBSoccerException {
        Character original = new Character("A", "Canadá", 3, 3, 3, 3, new FreezePower());
        Character copy = original.copy();
        assertNotSame(original, copy);
        copy.stun();
        copy.addGoal();
        copy.update(1.0);
        assertEquals(0, original.getEffectNames().length);
        assertEquals(0, original.getGoals());
        assertEquals(0.0, original.getPowerCharge(), 1e-9);
        assertTrue(copy.getPowerCharge() > 0.0);
    }

    // ---------------------------------------------------------------- supervivencia

    @Test
    public void survivalShouldOnlyBeAllowedInPvsM() throws POOBSoccerException {
        game.selectMode(PVSP);
        POOBSoccerException e = assertThrows(POOBSoccerException.class, () -> game.selectMatchType(SURVIVAL));
        assertEquals(POOBSoccerException.SUPERVIVENCIA_SOLO_PVSM, e.getMessage());

        game.selectMode(MVSM);
        assertThrows(POOBSoccerException.class, () -> game.selectMatchType(SURVIVAL));

        start(PVSM, SURVIVAL, 0, 1);
        assertFalse(game.isMatchFinished());
        assertEquals("Balones 3-3 | Racha 0", game.getMatchStatus());
    }

    @Test
    public void selectingModeShouldClearMatchType() throws POOBSoccerException {
        game.selectMode(PVSM);
        game.selectMatchType(SURVIVAL);
        game.selectMode(PVSP);
        game.selectPlayer(1, 0);
        game.selectPlayer(2, 1);
        POOBSoccerException e = assertThrows(POOBSoccerException.class, () -> game.startMatch());
        assertEquals(POOBSoccerException.MODO_INVALIDO, e.getMessage());
    }

    @Test
    public void matchTypeShouldRequireMode() {
        POOBSoccerException e = assertThrows(POOBSoccerException.class, () -> game.selectMatchType(TIME));
        assertEquals(POOBSoccerException.MODO_INVALIDO, e.getMessage());
    }

    @Test
    public void survivalShouldEndWhenPlayerOneRunsOutOfBalls() throws POOBSoccerException {
        Match m = match(PVSM, SURVIVAL, 0, 1);
        score(m, 2, 3);
        assertEquals("Balones 0-3 | Racha 0", m.getStatus());
        assertTrue(m.isFinished());
        assertEquals(m.getPlayerName(2), m.getWinnerName());
    }

    @Test
    public void survivalShouldContinueSeriesWhenMachineLosesAllBalls() throws POOBSoccerException {
        Match m = match(PVSM, SURVIVAL, 0, 0);
        String firstRival = m.getPlayerName(2);
        m.updateTick(TICK);
        assertTrue(m.getPlayerPowerCharge(2) > 0.0);

        score(m, 1, 3);
        assertEquals(0.0, m.getPlayerPowerCharge(2), 1e-9);
        assertEquals(0, m.getGoals(2));
        assertFalse(m.isFinished());
        assertEquals("Balones 3-3 | Racha 1", m.getStatus());
        assertNotEquals(firstRival, m.getPlayerName(2));
        assertEquals(RIGHT_HOME_X, centerX(m.getPlayerBounds(2)), 1e-9);
    }

    @Test
    public void survivalScoreShouldRestartAgainstEachNewRival() throws POOBSoccerException {
        Match m = match(PVSM, SURVIVAL, 0, 1);
        m.scoreGoal(2);
        m.updateTick(TICK);
        double playerOneCharge = m.getPlayerPowerCharge(1);
        score(m, 1, 3);
        assertEquals(0, m.getGoals(1));
        assertEquals(0, m.getGoals(2));
        assertEquals("Balones 2-3 | Racha 1", m.getStatus());
        assertEquals(playerOneCharge, m.getPlayerPowerCharge(1), 1e-9);
        m.scoreGoal(1);
        assertEquals(1, m.getGoals(1));
    }

    @Test
    public void survivalShouldEndOnlyWhenPlayerOneLosesAllBalls() throws POOBSoccerException {
        Match m = match(PVSM, SURVIVAL, 0, 1);
        score(m, 1, 3);
        assertEquals("Balones 3-3 | Racha 1", m.getStatus());
        score(m, 2, 2);
        assertFalse(m.isFinished());
        m.scoreGoal(2);
        assertTrue(m.isFinished());
        assertEquals("Balones 0-3 | Racha 1", m.getStatus());
    }

    @Test
    public void survivalShouldAskContextForNewRival() throws POOBSoccerException {
        MatchType type = new MatchTypeCatalog().get(SURVIVAL);
        StubContext context = new StubContext();
        type.begin(context);
        for (int i = 0; i < 3; i++) {
            type.goalScored(1);
        }
        assertEquals(1, context.rivalsReplaced);
        assertEquals("Balones 3-3 | Racha 1", type.getStatusText());
        assertFalse(type.isFinished());
    }

    @Test
    public void catalogShouldCycleRivals() throws POOBSoccerException {
        String[] names = game.getCharacterNames();
        Match m = match(PVSM, SURVIVAL, 0, names.length - 1);
        for (int rival = 0; rival < 2; rival++) {
            score(m, 1, 3);
            assertEquals(names[rival], m.getPlayerName(2));
        }
        assertEquals("Balones 3-3 | Racha 2", m.getStatus());
    }

    // ---------------------------------------------------------------- poderes

    @Test
    public void powerShouldOnlyActivateWhenBarIsFull() throws POOBSoccerException {
        start(PVSP, TIME);
        assertEquals(0.0, game.getPlayerPowerBar(1), 1e-9);
        game.pressAction(1, "POWER");
        game.updateTick(TICK);
        assertFalse(game.isPlayerPowerArmed(1));

        game.updateTick(17.0);
        assertEquals(1.0, game.getPlayerPowerBar(1), 1e-9);
        game.updateTick(TICK);
        assertTrue(game.isPlayerPowerArmed(1));
    }

    @Test
    public void powerBarShouldRechargeOverTime() throws POOBSoccerException {
        start(PVSP, TIME);
        game.updateTick(1.0);
        assertEquals(RECHARGE_PER_LEVEL * game.getCharacterPowerLevel(0), game.getPlayerPowerBar(1), 1e-6);
    }

    @Test
    public void freezePowerShouldBlockOpponentActions() throws POOBSoccerException {
        Character victim = new Character("B", "Canadá", 3, 3, 3, 3, new FreezePower());
        Character rival = new Character("C", "Japón", 3, 3, 3, 3, new StunPower());
        victim.enterAt(400.0, 1);
        rival.enterAt(RIGHT_HOME_X, -1);
        double top = victim.getTop();
        new FreezePower().applyEffect(victim);
        assertTrue(hasEffect(victim.getEffectNames(), FROZEN));
        victim.requestKick();
        assertFalse(victim.tryKick(ballBeside(victim), rival));
        victim.jump();
        victim.setMoveDirection(1);
        victim.update(0.5);
        assertEquals(400.0, victim.getX(), 1e-9);
        assertEquals(top, victim.getTop(), 1e-9);
    }

    @Test
    public void shrinkPowerShouldHalveOpponentSize() throws POOBSoccerException {
        Character victim = new Character("B", "Japón", 3, 3, 3, 3, new ShrinkPower());
        double width = victim.getBounds()[2];
        new ShrinkPower().applyEffect(victim);
        assertEquals(width / 2.0, victim.getBounds()[2], 1e-9);
    }

    @Test
    public void confusionPowerShouldInvertHorizontalControls() throws POOBSoccerException {
        Character victim = new Character("B", "Brasil", 3, 3, 3, 3, new ConfusionPower());
        victim.enterAt(400.0, 1);
        new ConfusionPower().applyEffect(victim);
        victim.setMoveDirection(1);
        victim.update(0.5);
        assertTrue(victim.getX() < 400.0);
    }

    @Test
    public void kickWithActivePowerShouldAffectOpponent() throws POOBSoccerException {
        Character kicker = new Character("A", "Canadá", 3, 3, 3, 3, new FreezePower());
        Character rival = new Character("B", "Japón", 3, 3, 3, 3, new StunPower());
        kicker.enterAt(400.0, 1);
        rival.enterAt(RIGHT_HOME_X, -1);
        kicker.update(17.0);
        kicker.armPower();
        assertTrue(kicker.isPowerArmed());
        kicker.requestKick();
        assertTrue(kicker.tryKick(ballBeside(kicker), rival));
        assertTrue(hasEffect(rival.getEffectNames(), FROZEN));
        assertFalse(kicker.isPowerArmed());
        assertEquals(0.0, kicker.getPowerCharge(), 1e-9);
    }

    @Test
    public void stunPowerShouldStunOpponent() throws POOBSoccerException {
        Character victim = new Character("B", "Japón", 3, 3, 3, 3, new StunPower());
        new StunPower().applyEffect(victim);
        assertTrue(hasEffect(victim.getEffectNames(), STUNNED));
    }

    // ---------------------------------------------------------------- aturdimiento

    @Test
    public void stunShouldLastThreeSeconds() throws POOBSoccerException {
        Character c = new Character("A", "Canadá", 3, 3, 3, 3, new FreezePower());
        Character rival = new Character("B", "Japón", 3, 3, 3, 3, new StunPower());
        c.enterAt(400.0, 1);
        rival.enterAt(RIGHT_HOME_X, -1);
        double top = c.getTop();
        c.stun();
        assertTrue(hasEffect(c.getEffectNames(), STUNNED));
        c.requestKick();
        assertFalse(c.tryKick(ballBeside(c), rival));
        c.jump();
        c.setMoveDirection(1);
        c.update(2.9);
        assertTrue(hasEffect(c.getEffectNames(), STUNNED));
        assertEquals(400.0, c.getX(), 1e-9);
        assertEquals(top, c.getTop(), 1e-9);
        c.update(0.2);
        assertEquals(0, c.getEffectNames().length);
        c.requestKick();
        assertTrue(c.tryKick(ballBeside(c), rival));
    }

    @Test
    public void meleeKickShouldStunOpponentForThreeSeconds() throws POOBSoccerException {
        Character a = new Character("A", "Canadá", 3, 3, 3, 3, new FreezePower());
        Character b = new Character("B", "Japón", 3, 3, 3, 3, new StunPower());
        a.enterAt(400.0, 1);
        double[] boundsA = a.getBounds();
        b.enterAt(boundsA[0] + boundsA[2] + 5.0 + b.getBounds()[2] / 2.0, -1);
        Ball farBall = new Ball();
        farBall.placeAt(Field.WIDTH - Goal.WIDTH, Ball.RADIUS);
        a.requestKick();
        assertFalse(a.tryKick(farBall, b));
        assertTrue(hasEffect(b.getEffectNames(), STUNNED));
        b.requestKick();
        assertFalse(b.tryKick(ballBeside(b), a));
        b.update(2.9);
        assertTrue(hasEffect(b.getEffectNames(), STUNNED));
        b.update(0.2);
        assertEquals(0, b.getEffectNames().length);
    }

    @Test
    public void meleeKickOutOfRangeShouldNotStun() throws POOBSoccerException {
        start(PVSP, TIME);
        game.pressAction(1, "KICK");
        game.updateTick(TICK);
        assertEquals(0, game.getPlayerEffects(2).length);
    }

    @Test
    public void kickShouldBeVisibleWhileItLasts() throws POOBSoccerException {
        Character a = new Character("A", "Canadá", 3, 3, 3, 3, new FreezePower());
        Character b = new Character("B", "Japón", 3, 3, 3, 3, new StunPower());
        a.enterAt(400.0, 1);
        b.enterAt(400.0, -1);
        assertFalse(a.isKicking());
        a.requestKick();
        a.tryKick(ballBeside(a), b);
        assertTrue(a.isKicking());
        a.update(1.0);
        assertFalse(a.isKicking());
    }

    @Test
    public void facadeShouldExposeFacingAndKicking() throws POOBSoccerException {
        start(PVSP, TIME);
        assertEquals(1, game.getPlayerFacing(1));
        assertEquals(-1, game.getPlayerFacing(2));
        assertFalse(game.isPlayerKicking(1));
    }

    @Test
    public void ballShouldLookAheadBetweenPhysicsStepsWithoutLeavingTheField() {
        Field field = new Field();
        Ball ball = new Ball();
        ball.placeAt(450.0, 100.0);
        ball.kick(5, 1);
        assertArrayEquals(new double[] { 450.0, 100.0 }, field.visibleBall(ball, 0.0), 1e-9);
        assertArrayEquals(new double[] { 455.0, 95.0 }, field.visibleBall(ball, 0.5), 1e-9);
        ball.placeAt(Field.WIDTH - Ball.RADIUS, 100.0);
        ball.kick(5, 1);
        assertEquals(Field.WIDTH - Ball.RADIUS, field.visibleBall(ball, 0.5)[0], 1e-9);
    }

    @Test
    public void restartShouldClearEffectsAndReturnHome() throws POOBSoccerException {
        Character c = new Character("A", "Canadá", 3, 3, 3, 3, new FreezePower());
        c.enterAt(400.0, 1);
        c.setMoveDirection(1);
        c.update(0.5);
        c.stun();
        c.addGoal();
        c.restart();
        assertEquals(0, c.getEffectNames().length);
        assertEquals(400.0, c.getX(), 1e-9);
        assertEquals(1, c.getGoals());
        assertTrue(c.getPowerCharge() > 0.0);
    }

    @Test
    public void enterAtShouldResetGoalsAndPowerBar() throws POOBSoccerException {
        Character c = new Character("A", "Canadá", 3, 3, 3, 3, new FreezePower());
        c.addGoal();
        c.update(1.0);
        c.enterAt(RIGHT_HOME_X, -1);
        assertEquals(0, c.getGoals());
        assertEquals(0.0, c.getPowerCharge(), 1e-9);
        assertEquals(RIGHT_HOME_X, c.getX(), 1e-9);
        assertEquals(-1, c.getFacing());
    }

    // ---------------------------------------------------------------- goles

    @Test
    public void fieldShouldCreditTheAttackerOfEachGoal() {
        Field field = new Field();
        Ball ball = new Ball();
        ball.placeAt(Goal.WIDTH / 2.0, Field.GROUND_Y - Ball.RADIUS);
        assertEquals(2, field.scorerOf(ball));
        ball.placeAt(Field.WIDTH - Goal.WIDTH / 2.0, Field.GROUND_Y - Ball.RADIUS);
        assertEquals(1, field.scorerOf(ball));
        field.dropBall(ball);
        assertEquals(Field.NO_GOAL, field.scorerOf(ball));
        assertEquals(Field.WIDTH / 2.0, ball.getX(), 1e-9);
    }

    @Test
    public void bodyPushAgainstAWallShouldKeepTheBallInsideTheField() throws POOBSoccerException {
        Field field = new Field();
        Character c = new Character("A", "Canadá", 3, 3, 3, 3, new FreezePower());
        c.enterAt(c.getBounds()[2] / 2.0, 1);
        Ball ball = new Ball();
        ball.placeAt(Ball.RADIUS, Field.GROUND_Y - Ball.RADIUS);
        assertTrue(c.touches(ball));
        c.push(ball);
        assertTrue(ball.getX() < Ball.RADIUS);
        field.keepBallInside(ball);
        assertEquals(Ball.RADIUS, ball.getX(), 1e-9);
    }

    @Test
    public void ballShouldBounceBackIntoPlayFromASideWall() {
        Field field = new Field();
        Ball ball = new Ball();
        ball.placeAt(Field.WIDTH - 60.0, 100.0);
        ball.kick(5, 1);
        double maxX = 0.0;
        for (int step = 0; step < 120; step++) {
            field.updateBall(ball);
            field.keepBallInside(ball);
            maxX = Math.max(maxX, ball.getX());
        }
        assertEquals(Field.WIDTH - Ball.RADIUS, maxX, 1e-9);
        assertTrue(ball.getX() < Field.WIDTH - 200.0, "El balon debe volver al juego tras rebotar en la pared");
    }

    @Test
    public void ballPartiallyInsideShouldNotBeGoal() {
        Goal goal = new Goal(0.0, Goal.WIDTH, 2);
        Ball partial = new Ball();
        partial.placeAt(Goal.WIDTH, Field.GROUND_Y - Ball.RADIUS - 5.0);
        assertFalse(goal.isBallInside(partial));
        Ball inside = new Ball();
        inside.placeAt(Goal.WIDTH - Ball.RADIUS, Field.GROUND_Y - Ball.RADIUS - 5.0);
        assertTrue(goal.isBallInside(inside));
    }

    @Test
    public void ballShouldBounceOnCrossbar() {
        Goal goal = new Goal(0.0, Goal.WIDTH, 2);
        double crossbarTop = Field.GROUND_Y - Goal.HEIGHT;
        Ball ball = new Ball();
        ball.placeAt(Goal.WIDTH / 2.0, crossbarTop - Ball.RADIUS + 3.0);
        goal.resolveCrossbar(ball);
        assertEquals(crossbarTop - Ball.RADIUS, ball.getY(), 1e-9);
    }

    @Test
    public void goalShouldOnlyCountForTheScorer() throws POOBSoccerException {
        Match m = match(PVSP, GOALS_3, 0, 0);
        m.scoreGoal(1);
        assertEquals(1, m.getGoals(1));
        assertEquals(0, m.getGoals(2));
        m.scoreGoal(2);
        assertEquals(1, m.getGoals(2));
        POOBSoccerException e = assertThrows(POOBSoccerException.class, () -> m.scoreGoal(3));
        assertEquals(POOBSoccerException.JUGADOR_INVALIDO, e.getMessage());
    }

    @Test
    public void shouldRestartPlayAfterGoal() throws POOBSoccerException {
        CharacterCatalog characters = new CharacterCatalog();
        Character player1 = characters.get(0).copy();
        Match m = new Match(new GameModeCatalog().get(PVSP), new MatchTypeCatalog().get(GOALS_3), player1,
                characters.get(1).copy(), characters);
        m.press(1, Action.RIGHT);
        m.updateTick(0.5);
        assertTrue(centerX(m.getPlayerBounds(1)) > LEFT_HOME_X);
        player1.stun();
        m.scoreGoal(1);
        assertEquals(LEFT_HOME_X, centerX(m.getPlayerBounds(1)), 1e-9);
        assertEquals(RIGHT_HOME_X, centerX(m.getPlayerBounds(2)), 1e-9);
        assertEquals(0, m.getPlayerEffects(1).length);
        assertEquals(Field.WIDTH / 2.0, m.getBallPosition()[0], 1e-9);
    }

    @Test
    public void goalMatchShouldEndAtTarget() throws POOBSoccerException {
        Match m = match(PVSP, GOALS_3, 0, 0);
        score(m, 1, 2);
        assertFalse(m.isFinished());
        m.scoreGoal(1);
        assertTrue(m.isFinished());
        assertEquals(m.getPlayerName(1), m.getWinnerName());
    }

    @Test
    public void goalMatchStatusShouldShowTarget() throws POOBSoccerException {
        start(PVSP, GOALS_3);
        assertEquals("Meta: 3 goles | 00:00", game.getMatchStatus());
        game.updateTick(0.5);
        assertEquals("Meta: 3 goles | 00:00", game.getMatchStatus());
        game.updateTick(0.5);
        assertEquals("Meta: 3 goles | 00:01", game.getMatchStatus());
        game.updateTick(0.99);
        assertEquals("Meta: 3 goles | 00:01", game.getMatchStatus());
    }

    @Test
    public void goalScoredAfterTheEndShouldBeIgnored() throws POOBSoccerException {
        MatchType type = new MatchTypeCatalog().get(GOALS_3);
        StubContext context = new StubContext();
        type.begin(context);
        context.goals[0] = 3;
        type.goalScored(1);
        context.goals[1] = 3;
        type.goalScored(2);
        assertTrue(type.isFinished());
        assertEquals(1, type.getWinner());

        type.begin(new StubContext());
        assertFalse(type.isFinished());
    }

    // ---------------------------------------------------------------- tiempo y gol de oro

    @Test
    public void timeMatchShouldGiveWinToLeaderAfterSixtySeconds() throws POOBSoccerException {
        Match m = match(PVSP, TIME, 0, 0);
        m.scoreGoal(1);
        m.updateTick(TICK);
        assertFalse(m.isFinished());
        m.updateTick(MATCH_SECONDS);
        assertTrue(m.isFinished());
        assertEquals("00:00", m.getStatus());
        assertEquals(m.getPlayerName(1), m.getWinnerName());
    }

    @Test
    public void tieShouldActivateGoldenGoal() throws POOBSoccerException {
        Match m = match(PVSP, TIME, 0, 1);
        m.updateTick(MATCH_SECONDS);
        assertEquals(GOLDEN_GOAL, m.getStatus());
        assertFalse(m.isFinished());

        m.scoreGoal(2);
        assertTrue(m.isFinished());
        assertEquals(1, m.getGoals(2));
        assertEquals(m.getPlayerName(2), m.getWinnerName());
    }

    @Test
    public void timeMatchShouldCountDownAgainstAnyContext() throws POOBSoccerException {
        MatchType type = new MatchTypeCatalog().get(TIME);
        StubContext context = new StubContext();
        type.begin(context);
        assertEquals("01:00", type.getStatusText());
        type.tick(0.5);
        assertEquals("01:00", type.getStatusText());
        type.tick(0.5);
        assertEquals("00:59", type.getStatusText());
        context.goals[1] = 2;
        type.tick(MATCH_SECONDS);
        assertTrue(type.isFinished());
        assertEquals(2, type.getWinner());
    }

    // ---------------------------------------------------------------- facade, entrada, pausa e IA

    @Test
    public void shouldNotStartWithoutBothPlayers() throws POOBSoccerException {
        game.selectMode(PVSP);
        game.selectMatchType(TIME);
        game.selectPlayer(1, 0);
        POOBSoccerException e = assertThrows(POOBSoccerException.class, () -> game.startMatch());
        assertEquals(POOBSoccerException.JUGADORES_INCOMPLETOS, e.getMessage());
    }

    @Test
    public void shouldRejectInvalidSelections() throws POOBSoccerException {
        POOBSoccerException e = assertThrows(POOBSoccerException.class, () -> game.selectPlayer(1, 99));
        assertEquals(POOBSoccerException.PERSONAJE_INVALIDO, e.getMessage());
        e = assertThrows(POOBSoccerException.class, () -> game.selectPlayer(3, 0));
        assertEquals(POOBSoccerException.JUGADOR_INVALIDO, e.getMessage());
        assertThrows(POOBSoccerException.class, () -> game.selectPlayer(0, 0));
        assertThrows(POOBSoccerException.class, () -> game.selectMode(-1));
        game.selectMode(PVSP);
        e = assertThrows(POOBSoccerException.class, () -> game.selectMatchType(99));
        assertEquals(POOBSoccerException.MODO_INVALIDO, e.getMessage());
        e = assertThrows(POOBSoccerException.class, () -> game.updateTick(TICK));
        assertEquals(POOBSoccerException.PARTIDO_NO_INICIADO, e.getMessage());
        assertThrows(POOBSoccerException.class, () -> game.pressAction(1, "KICK"));
        assertThrows(POOBSoccerException.class, () -> game.getMatchStatus());
    }

    @Test
    public void singletonShouldBeUnique() throws POOBSoccerException {
        assertSame(game, HeadSoccer.getInstance());
    }

    @Test
    public void newGameShouldClearSession() throws POOBSoccerException {
        start(PVSP, TIME);
        game.newGame();
        POOBSoccerException e = assertThrows(POOBSoccerException.class, () -> game.getMatchStatus());
        assertEquals(POOBSoccerException.PARTIDO_NO_INICIADO, e.getMessage());
        e = assertThrows(POOBSoccerException.class, () -> game.startMatch());
        assertEquals(POOBSoccerException.MODO_INVALIDO, e.getMessage());
        game.selectMode(PVSP);
        game.selectMatchType(TIME);
        e = assertThrows(POOBSoccerException.class, () -> game.startMatch());
        assertEquals(POOBSoccerException.JUGADORES_INCOMPLETOS, e.getMessage());
    }

    @Test
    public void rematchShouldStartFreshWithTheChosenCharacters() throws POOBSoccerException {
        String[] names = game.getCharacterNames();
        start(PVSP, TIME, 0, 1);
        game.pressAction(1, "RIGHT");
        game.updateTick(1.0);
        assertTrue(game.getPlayerPowerBar(2) > 0.0);

        game.startMatch();
        assertEquals(names[0], game.getPlayerName(1));
        assertEquals(names[1], game.getPlayerName(2));
        assertEquals(0.0, game.getPlayerPowerBar(2), 1e-9);
        assertEquals("01:00", game.getMatchStatus());
        game.updateTick(TICK);
        assertEquals(LEFT_HOME_X, centerX(game.getPlayerBounds(1)), 1e-9);
    }

    @Test
    public void pausedGameShouldNotAdvance() throws POOBSoccerException {
        start(PVSP, TIME);
        assertEquals("01:00", game.getMatchStatus());
        game.pause();
        double y = game.getBallPosition()[1];
        game.updateTick(1.0);
        assertEquals(y, game.getBallPosition()[1], 1e-9);
        assertEquals("01:00", game.getMatchStatus());
        game.resume();
        game.updateTick(1.0);
        assertEquals("00:59", game.getMatchStatus());
    }

    @Test
    public void pauseShouldReleaseHeldKeys() throws POOBSoccerException {
        start(PVSP, TIME);
        game.pressAction(1, "RIGHT");
        game.pause();
        game.resume();
        game.updateTick(TICK);
        assertEquals(LEFT_HOME_X, centerX(game.getPlayerBounds(1)), 1e-9);
    }

    @Test
    public void releasingAllActionsShouldStopHeldKeysWithoutPausing() throws POOBSoccerException {
        start(PVSP, TIME);
        game.pressAction(1, "RIGHT");
        game.pressAction(2, "LEFT");
        game.releaseAllActions();
        game.updateTick(1.0);
        assertEquals(LEFT_HOME_X, centerX(game.getPlayerBounds(1)), 1e-9);
        assertEquals(RIGHT_HOME_X, centerX(game.getPlayerBounds(2)), 1e-9);
        assertEquals("00:59", game.getMatchStatus());
    }

    @Test
    public void heldKeysShouldDriveHumanPlayer() throws POOBSoccerException {
        start(PVSP, TIME);
        game.pressAction(1, "RIGHT");
        game.updateTick(TICK);
        double moved = centerX(game.getPlayerBounds(1));
        assertTrue(moved > LEFT_HOME_X);
        game.releaseAction(1, "RIGHT");
        game.updateTick(TICK);
        assertEquals(moved, centerX(game.getPlayerBounds(1)), 1e-9);

        double top = game.getPlayerBounds(1)[1];
        game.pressAction(1, "JUMP");
        game.updateTick(TICK);
        assertTrue(game.getPlayerBounds(1)[1] < top);
    }

    @Test
    public void oppositeHeldKeysShouldCancel() throws POOBSoccerException {
        start(PVSP, TIME);
        game.pressAction(2, "LEFT");
        game.pressAction(2, "RIGHT");
        game.updateTick(TICK);
        assertEquals(RIGHT_HOME_X, centerX(game.getPlayerBounds(2)), 1e-9);
    }

    @Test
    public void keyboardShouldNotDriveMachine() throws POOBSoccerException {
        start(PVSM, GOALS_3);
        double before = centerX(game.getPlayerBounds(2));
        assertTrue(game.getBallPosition()[0] < before);
        game.pressAction(2, "RIGHT");
        game.pressAction(2, "POWER");
        game.updateTick(TICK);
        assertTrue(centerX(game.getPlayerBounds(2)) < before, "La IA debe seguir yendo hacia el balon");
    }

    @Test
    public void invalidActionShouldBeRejected() throws POOBSoccerException {
        start(PVSP, TIME);
        POOBSoccerException e = assertThrows(POOBSoccerException.class, () -> game.pressAction(1, "FLY"));
        assertEquals(POOBSoccerException.ACCION_INVALIDA, e.getMessage());
        e = assertThrows(POOBSoccerException.class, () -> game.releaseAction(1, null));
        assertEquals(POOBSoccerException.ACCION_INVALIDA, e.getMessage());
        e = assertThrows(POOBSoccerException.class, () -> game.pressAction(3, "KICK"));
        assertEquals(POOBSoccerException.JUGADOR_INVALIDO, e.getMessage());
    }

    @Test
    public void winnerBeforeTheEndShouldThrow() throws POOBSoccerException {
        start(PVSP, TIME);
        POOBSoccerException e = assertThrows(POOBSoccerException.class, () -> game.getWinnerName());
        assertEquals(POOBSoccerException.PARTIDO_EN_CURSO, e.getMessage());
    }

    @Test
    public void aiShouldChaseTheBall() throws POOBSoccerException {
        start(PVSM, GOALS_3);
        double before = centerX(game.getPlayerBounds(2));
        game.updateTick(TICK);
        assertTrue(centerX(game.getPlayerBounds(2)) < before);
    }

    @Test
    public void machineVsMachineShouldRunWithoutErrors() throws POOBSoccerException {
        start(MVSM, TIME);
        for (int i = 0; i < 600; i++) {
            game.updateTick(TICK);
        }
        assertTrue(game.getBallPosition()[0] >= 0.0 && game.getBallPosition()[0] <= Field.WIDTH);
    }

    // ---------------------------------------------------------------- catalogos

    @Test
    public void everyCharacterShouldHaveAUniquePower() throws POOBSoccerException {
        Set<String> powers = new HashSet<String>();
        int count = game.getCharacterNames().length;
        for (int i = 0; i < count; i++) {
            assertTrue(powers.add(game.getCharacterPowerName(i)), "Poder repetido: " + game.getCharacterPowerName(i));
        }
        assertEquals(count, powers.size());
    }

    @Test
    public void catalogsShouldListOptionsInOrder() throws POOBSoccerException {
        assertEquals(3, game.getModeNames().length);
        assertEquals(5, game.getMatchTypeNames().length);
        assertEquals("Por Goles (3)", game.getMatchTypeNames()[GOALS_3]);
        assertEquals("Jacob", game.getCharacterNames()[0]);
        CharacterCatalog catalog = new CharacterCatalog();
        POOBSoccerException e = assertThrows(POOBSoccerException.class, () -> catalog.get(99));
        assertEquals(POOBSoccerException.PERSONAJE_INVALIDO, e.getMessage());
    }

    @Test
    public void facadeShouldExposeActivePowerName() throws POOBSoccerException {
        start(PVSP, TIME);
        assertEquals(game.getCharacterPowerName(0), game.getPlayerPowerName(1));
    }

    @Test
    public void facadeShouldExposeCharacterPowerData() throws POOBSoccerException {
        assertEquals(3, game.getCharacterPowerLevel(0));
        assertEquals(2, game.getCharacterPowerLevel(1));
        assertEquals(4, game.getCharacterPowerLevel(2));
        assertEquals(3, game.getCharacterPowerLevel(3));
        assertFalse(game.getCharacterPowerDescription(0).isEmpty());
        assertThrows(POOBSoccerException.class, () -> game.getCharacterPowerLevel(99));
    }

    @Test
    public void facadeShouldExposeCharacterCardData() throws POOBSoccerException {
        assertEquals("Canadá", game.getCharacterCountry(0));
        assertEquals(3, game.getCharacterSpeed(0));
        assertEquals(3, game.getCharacterJump(0));
        assertEquals(4, game.getCharacterStrength(0));
        POOBSoccerException e = assertThrows(POOBSoccerException.class, () -> game.getCharacterCountry(-1));
        assertEquals(POOBSoccerException.PERSONAJE_INVALIDO, e.getMessage());
    }

    @Test
    public void facadeShouldExposeTheGeometryThePhysicsUses() {
        assertEquals(Field.WIDTH, game.getFieldWidth(), 1e-9);
        assertEquals(Field.GROUND_Y, game.getGroundY(), 1e-9);
        assertEquals(Ball.RADIUS, game.getBallRadius(), 1e-9);
        assertEquals(Goal.CROSSBAR_THICKNESS, game.getCrossbarThickness(), 1e-9);
    }

    @Test
    public void facadeShouldExposeWhereTheFieldPlacesEachGoal() throws POOBSoccerException {
        start(PVSP, TIME);
        double top = Field.GROUND_Y - Goal.HEIGHT;
        double[][] goals = game.getGoalBounds();
        assertEquals(2, goals.length);
        assertArrayEquals(new double[] { 0.0, top, Goal.WIDTH, Goal.HEIGHT }, goals[0], 1e-9);
        assertArrayEquals(new double[] { Field.WIDTH - Goal.WIDTH, top, Goal.WIDTH, Goal.HEIGHT }, goals[1], 1e-9);
    }
}
