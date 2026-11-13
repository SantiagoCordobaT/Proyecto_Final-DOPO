package dominio;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dominio.efectos.FreezeEffect;
import dominio.efectos.StunEffect;
import dominio.excepciones.POOBSoccerException;
import dominio.partidos.TimeMatch;
import dominio.personajes.Character;
import dominio.poderes.ConfusionPower;
import dominio.poderes.FreezePower;
import dominio.poderes.PowerBar;
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

    private HeadSoccer game;

    @BeforeEach
    public void setUp() throws POOBSoccerException {
        game = new HeadSoccer();
    }

    private void start(int mode, int matchType) throws POOBSoccerException {
        game.selectMode(mode);
        game.selectMatchType(matchType);
        game.selectPlayer1(0);
        game.selectPlayer2(0);
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

    private void putBallInLeftGoal() {
        game.getBall().placeAt(Goal.WIDTH / 2.0, Field.GROUND_Y - Ball.RADIUS - 5.0);
    }

    private void putBallInRightGoal() {
        game.getBall().placeAt(Field.WIDTH - Goal.WIDTH / 2.0, Field.GROUND_Y - Ball.RADIUS - 5.0);
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

        game.selectMode(PVSM);
        game.selectMatchType(SURVIVAL);
        game.selectPlayer1(0);
        game.selectPlayer2(1);
        game.startMatch();
        assertFalse(game.isMatchFinished());
    }

    @Test
    public void survivalShouldNotAllowSwitchingToOtherModes() throws POOBSoccerException {
        game.selectMode(PVSM);
        game.selectMatchType(SURVIVAL);
        assertThrows(POOBSoccerException.class, () -> game.selectMode(PVSP));
    }

    @Test
    public void survivalShouldEndWhenPlayerOneRunsOutOfBalls() throws POOBSoccerException {
        game.selectMode(PVSM);
        game.selectMatchType(SURVIVAL);
        game.selectPlayer1(0);
        game.selectPlayer2(1);
        game.startMatch();
        assertEquals(3, game.getPlayerBalls(1));
        for (int i = 0; i < 3; i++) {
            putBallInLeftGoal();
            game.updateTick(TICK);
        }
        assertEquals(0, game.getPlayerBalls(1));
        assertTrue(game.isMatchFinished());
        assertEquals(game.getPlayerName(2), game.getWinnerName());
    }

    // ---------------------------------------------------------------- poderes

    @Test
    public void powerShouldOnlyActivateWhenBarIsFull() throws POOBSoccerException {
        start(PVSP, TIME);
        assertEquals(0.0, game.getPlayerPowerBar(1), 1e-9);
        game.activatePlayerPower(1);
        assertFalse(game.isPlayerPowerArmed(1));

        game.updateTick(17.0);
        assertEquals(1.0, game.getPlayerPowerBar(1), 1e-9);
        game.activatePlayerPower(1);
        assertTrue(game.isPlayerPowerArmed(1));
    }

    @Test
    public void powerBarShouldRechargeOverTime() throws POOBSoccerException {
        start(PVSP, TIME);
        game.updateTick(1.0);
        assertEquals(PowerBar.RECHARGE_PER_LEVEL * game.getCharacterPowerLevel(0) / PowerBar.FULL,
                game.getPlayerPowerBar(1), 1e-6);
    }

    @Test
    public void freezePowerShouldBlockOpponentActions() throws POOBSoccerException {
        Character victim = new Character("B", "Canadá", 3, 3, 3, 3, new FreezePower());
        Character rival = new Character("C", "Japón", 3, 3, 3, 3, new StunPower());
        victim.enterAt(400.0, 1);
        rival.enterAt(Side.RIGHT.homeX, -1);
        double top = victim.getTop();
        new FreezePower().applyEffect(victim);
        assertTrue(hasEffect(victim.getEffectNames(), FreezeEffect.NAME));
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
        start(PVSP, TIME);
        game.updateTick(17.0);
        game.activatePlayerPower(1);
        double[] bounds = game.getPlayerBounds(1);
        game.getBall().placeAt(bounds[0] + bounds[2] + 10.0, bounds[1] + bounds[3] / 2.0);
        game.playerKick(1);
        game.updateTick(TICK);
        assertTrue(hasEffect(game.getPlayerEffects(2), FreezeEffect.NAME));
        assertFalse(game.isPlayerPowerArmed(1));
        assertEquals(0.0, game.getPlayerPowerBar(1), 1e-9);
    }

    // ---------------------------------------------------------------- aturdimiento

    @Test
    public void stunShouldLastThreeSeconds() throws POOBSoccerException {
        Character c = new Character("A", "Canadá", 3, 3, 3, 3, new FreezePower());
        Character rival = new Character("B", "Japón", 3, 3, 3, 3, new StunPower());
        c.enterAt(400.0, 1);
        rival.enterAt(Side.RIGHT.homeX, -1);
        double top = c.getTop();
        c.stun();
        assertTrue(hasEffect(c.getEffectNames(), StunEffect.NAME));
        c.requestKick();
        assertFalse(c.tryKick(ballBeside(c), rival));
        c.jump();
        c.setMoveDirection(1);
        c.update(2.9);
        assertTrue(hasEffect(c.getEffectNames(), StunEffect.NAME));
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
        assertTrue(hasEffect(b.getEffectNames(), StunEffect.NAME));
        b.requestKick();
        assertFalse(b.tryKick(ballBeside(b), a));
        b.update(2.9);
        assertTrue(hasEffect(b.getEffectNames(), StunEffect.NAME));
        b.update(0.2);
        assertEquals(0, b.getEffectNames().length);
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
        c.enterAt(Side.RIGHT.homeX, -1);
        assertEquals(0, c.getGoals());
        assertEquals(0.0, c.getPowerCharge(), 1e-9);
        assertEquals(Side.RIGHT.homeX, c.getX(), 1e-9);
        assertEquals(-1, c.getFacing());
    }

    // ---------------------------------------------------------------- goles

    @Test
    public void shouldDetectGoalInRivalGoal() throws POOBSoccerException {
        start(PVSP, GOALS_3);
        putBallInRightGoal();
        game.updateTick(TICK);
        assertEquals(1, game.getPlayerGoals(1));
        assertEquals(0, game.getPlayerGoals(2));
    }

    @Test
    public void ownGoalShouldScoreForRival() throws POOBSoccerException {
        start(PVSP, GOALS_3);
        putBallInLeftGoal();
        game.updateTick(TICK);
        assertEquals(0, game.getPlayerGoals(1));
        assertEquals(1, game.getPlayerGoals(2));
    }

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
    public void shouldRestartPlayAfterGoal() throws POOBSoccerException {
        start(PVSP, GOALS_3);
        game.getPlayer1().stun();
        putBallInRightGoal();
        game.updateTick(TICK);
        assertEquals(Side.LEFT.homeX, centerX(game.getPlayerBounds(1)), 1e-9);
        assertEquals(Side.RIGHT.homeX, centerX(game.getPlayerBounds(2)), 1e-9);
        assertEquals(0, game.getPlayerEffects(1).length);
        assertEquals(Field.WIDTH / 2.0, game.getBallX(), 1e-9);
    }

    @Test
    public void goalMatchShouldEndAtTarget() throws POOBSoccerException {
        start(PVSP, GOALS_3);
        for (int i = 0; i < 3; i++) {
            putBallInRightGoal();
            game.updateTick(TICK);
        }
        assertTrue(game.isMatchFinished());
        assertEquals(game.getPlayerName(1), game.getWinnerName());
    }

    // ---------------------------------------------------------------- tiempo y gol de oro

    @Test
    public void timeMatchShouldGiveWinToLeaderAfterSixtySeconds() throws POOBSoccerException {
        start(PVSP, TIME);
        putBallInRightGoal();
        game.updateTick(TICK);
        assertFalse(game.isMatchFinished());
        game.updateTick(60.0);
        assertTrue(game.isMatchFinished());
        assertFalse(game.isGoldenGoal());
        assertEquals(game.getPlayerName(1), game.getWinnerName());
    }

    @Test
    public void tieShouldActivateGoldenGoal() throws POOBSoccerException {
        start(PVSP, TIME);
        game.updateTick(60.0);
        assertTrue(game.isGoldenGoal());
        assertFalse(game.isMatchFinished());

        putBallInLeftGoal();
        game.updateTick(TICK);
        assertTrue(game.isMatchFinished());
        assertEquals(1, game.getPlayerGoals(2));
    }

    // ---------------------------------------------------------------- facade, pausa e IA

    @Test
    public void shouldNotStartWithoutBothPlayers() throws POOBSoccerException {
        game.selectMode(PVSP);
        game.selectMatchType(TIME);
        game.selectPlayer1(0);
        POOBSoccerException e = assertThrows(POOBSoccerException.class, () -> game.startMatch());
        assertEquals(POOBSoccerException.JUGADORES_INCOMPLETOS, e.getMessage());
    }

    @Test
    public void shouldRejectInvalidSelections() {
        assertThrows(POOBSoccerException.class, () -> game.selectPlayer1(99));
        assertThrows(POOBSoccerException.class, () -> game.selectMode(-1));
        assertThrows(POOBSoccerException.class, () -> game.selectMatchType(99));
        assertThrows(POOBSoccerException.class, () -> game.updateTick(TICK));
    }

    @Test
    public void pausedGameShouldNotAdvance() throws POOBSoccerException {
        start(PVSP, TIME);
        game.pause();
        assertTrue(game.isPaused());
        double y = game.getBallY();
        game.updateTick(1.0);
        assertEquals(y, game.getBallY(), 1e-9);
        assertEquals(TimeMatch.MATCH_SECONDS, game.getRemainingSeconds(), 1e-9);
        game.resume();
        game.updateTick(1.0);
        assertTrue(game.getRemainingSeconds() < TimeMatch.MATCH_SECONDS);
    }

    @Test
    public void aiShouldChaseTheBall() throws POOBSoccerException {
        start(PVSM, GOALS_3);
        game.getBall().placeAt(Field.WIDTH / 2.0 + 150.0, Field.GROUND_Y - Ball.RADIUS);
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
        assertTrue(game.getBallX() >= 0.0 && game.getBallX() <= Field.WIDTH);
    }

    // ---------------------------------------------------------------- ajustes del PDF

    @Test
    public void meleeKickOutOfRangeShouldNotStun() throws POOBSoccerException {
        start(PVSP, TIME);
        game.playerKick(1);
        game.updateTick(TICK);
        assertEquals(0, game.getPlayerEffects(2).length);
    }

    @Test
    public void survivalShouldContinueSeriesWhenMachineLosesAllBalls() throws POOBSoccerException {
        game.selectMode(PVSM);
        game.selectMatchType(SURVIVAL);
        game.selectPlayer1(0);
        game.selectPlayer2(0);
        game.startMatch();
        assertEquals(0, game.getSurvivalStreak());
        String firstRival = game.getPlayerName(2);

        for (int i = 0; i < 3; i++) {
            putBallInRightGoal();
            game.updateTick(TICK);
        }
        assertEquals(0.0, game.getPlayerPowerBar(2), 1e-9);
        assertFalse(game.isMatchFinished());
        assertEquals(1, game.getSurvivalStreak());
        assertEquals(3, game.getPlayerBalls(2));
        assertEquals(3, game.getPlayerBalls(1));
        assertNotEquals(firstRival, game.getPlayerName(2));
    }

    @Test
    public void survivalShouldEndOnlyWhenPlayerOneLosesAllBalls() throws POOBSoccerException {
        game.selectMode(PVSM);
        game.selectMatchType(SURVIVAL);
        game.selectPlayer1(0);
        game.selectPlayer2(1);
        game.startMatch();
        for (int i = 0; i < 3; i++) {
            putBallInRightGoal();
            game.updateTick(TICK);
        }
        assertEquals(1, game.getSurvivalStreak());
        for (int i = 0; i < 2; i++) {
            putBallInLeftGoal();
            game.updateTick(TICK);
        }
        assertFalse(game.isMatchFinished());
        putBallInLeftGoal();
        game.updateTick(TICK);
        assertTrue(game.isMatchFinished());
        assertEquals(0, game.getPlayerBalls(1));
        assertEquals(1, game.getSurvivalStreak());
    }

    @Test
    public void everyCharacterShouldHaveAUniquePower() throws POOBSoccerException {
        Set<String> powers = new HashSet<String>();
        for (int i = 0; i < game.getCharacterCount(); i++) {
            assertTrue(powers.add(game.getCharacterPowerName(i)), "Poder repetido: " + game.getCharacterPowerName(i));
        }
        assertEquals(game.getCharacterCount(), powers.size());
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
    public void stunPowerShouldStunOpponent() throws POOBSoccerException {
        Character victim = new Character("B", "Japón", 3, 3, 3, 3, new StunPower());
        new StunPower().applyEffect(victim);
        assertTrue(hasEffect(victim.getEffectNames(), StunEffect.NAME));
    }
}
