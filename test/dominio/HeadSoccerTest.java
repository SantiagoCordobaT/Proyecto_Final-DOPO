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

    private void putBallInLeftGoal() {
        game.getBall().resetPosition(Field.GOAL_WIDTH / 2.0, Field.GROUND_Y - Field.BALL_RADIUS - 5.0);
    }

    private void putBallInRightGoal() {
        game.getBall().resetPosition(Field.WIDTH - Field.GOAL_WIDTH / 2.0, Field.GROUND_Y - Field.BALL_RADIUS - 5.0);
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
        copy.update(1.0, new Field());
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
        Field field = new Field();
        Character victim = new Character("B", "Canadá", 3, 3, 3, 3, new FreezePower());
        victim.placeAt(400.0, field.getGroundY(), 1);
        new FreezePower().applyEffect(victim);
        assertTrue(hasEffect(victim.getEffectNames(), FreezeEffect.NAME));
        assertFalse(victim.jump(field.getGroundY()));
        assertFalse(victim.requestKick());
        victim.setMoveDirection(1);
        victim.update(0.5, field);
        assertEquals(400.0, victim.getX(), 1e-9);
    }

    @Test
    public void shrinkPowerShouldHalveOpponentSize() throws POOBSoccerException {
        Character victim = new Character("B", "Japón", 3, 3, 3, 3, new ShrinkPower());
        double width = victim.getWidth();
        new ShrinkPower().applyEffect(victim);
        assertEquals(width / 2.0, victim.getWidth(), 1e-9);
    }

    @Test
    public void confusionPowerShouldInvertHorizontalControls() throws POOBSoccerException {
        Field field = new Field();
        Character victim = new Character("B", "Brasil", 3, 3, 3, 3, new ConfusionPower());
        victim.placeAt(400.0, field.getGroundY(), 1);
        new ConfusionPower().applyEffect(victim);
        victim.setMoveDirection(1);
        victim.update(0.5, field);
        assertTrue(victim.getX() < 400.0);
    }

    @Test
    public void kickWithActivePowerShouldAffectOpponent() throws POOBSoccerException {
        start(PVSP, TIME);
        Character p1 = game.getPlayer1();
        game.updateTick(17.0);
        game.activatePlayerPower(1);
        game.getBall().resetPosition(p1.getX() + 40.0, p1.getY() - 40.0);
        game.playerKick(1);
        game.updateTick(TICK);
        assertTrue(hasEffect(game.getPlayerEffects(2), FreezeEffect.NAME));
        assertFalse(game.isPlayerPowerArmed(1));
        assertEquals(0.0, game.getPlayerPowerBar(1), 1e-9);
    }

    // ---------------------------------------------------------------- aturdimiento

    @Test
    public void stunShouldLastThreeSeconds() throws POOBSoccerException {
        Field field = new Field();
        Character c = new Character("A", "Canadá", 3, 3, 3, 3, new FreezePower());
        c.placeAt(400.0, field.getGroundY(), 1);
        c.stun();
        assertTrue(hasEffect(c.getEffectNames(), StunEffect.NAME));
        assertFalse(c.jump(field.getGroundY()));
        assertFalse(c.requestKick());
        c.setMoveDirection(1);
        c.update(2.9, field);
        assertTrue(hasEffect(c.getEffectNames(), StunEffect.NAME));
        assertEquals(400.0, c.getX(), 1e-9);
        c.update(0.2, field);
        assertEquals(0, c.getEffectNames().length);
        assertTrue(c.requestKick());
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
    public void ballPartiallyInsideShouldNotBeGoal() throws POOBSoccerException {
        Field field = new Field();
        Ball ball = new Ball(Field.GOAL_WIDTH, Field.GROUND_Y - Field.BALL_RADIUS - 5.0, Field.BALL_RADIUS);
        assertFalse(field.getLeftGoal().isBallInside(ball));
        ball.setX(Field.GOAL_WIDTH - Field.BALL_RADIUS);
        assertTrue(field.getLeftGoal().isBallInside(ball));
    }

    @Test
    public void ballShouldBounceOnCrossbar() throws POOBSoccerException {
        Field field = new Field();
        Goal goal = field.getLeftGoal();
        Ball ball = new Ball(Field.GOAL_WIDTH / 2.0, goal.getCrossbarTop() - Field.BALL_RADIUS + 3.0, Field.BALL_RADIUS);
        ball.setSpeedY(5.0);
        goal.resolveCrossbar(ball);
        assertTrue(ball.getSpeedY() < 0.0);
    }

    @Test
    public void shouldRestartPlayAfterGoal() throws POOBSoccerException {
        start(PVSP, GOALS_3);
        game.getPlayer1().stun();
        putBallInRightGoal();
        game.updateTick(TICK);
        assertEquals(Field.PLAYER1_START_X, game.getPlayerX(1), 1e-9);
        assertEquals(Field.PLAYER2_START_X, game.getPlayerX(2), 1e-9);
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
        game.getBall().resetPosition(Field.WIDTH / 2.0 + 150.0, Field.GROUND_Y - Field.BALL_RADIUS);
        double before = game.getPlayerX(2);
        game.updateTick(TICK);
        assertTrue(game.getPlayerX(2) < before);
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
    public void meleeKickShouldStunOpponentForThreeSeconds() throws POOBSoccerException {
        start(PVSP, TIME);
        Character p1 = game.getPlayer1();
        Character p2 = game.getPlayer2();
        p2.placeAt(p1.getX() + Character.BASE_WIDTH + 5.0, Field.GROUND_Y, -1);
        game.playerKick(1);
        game.updateTick(TICK);
        assertTrue(hasEffect(game.getPlayerEffects(2), StunEffect.NAME));
        assertFalse(p2.requestKick());
        game.updateTick(2.9);
        assertTrue(hasEffect(game.getPlayerEffects(2), StunEffect.NAME));
        game.updateTick(0.2);
        assertEquals(0, game.getPlayerEffects(2).length);
    }

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
