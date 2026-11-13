package dominio.personajes;

import java.util.ArrayList;
import java.util.List;

import dominio.Ball;
import dominio.Field;
import dominio.efectos.Effect;
import dominio.efectos.StunEffect;
import dominio.excepciones.POOBSoccerException;
import dominio.poderes.Power;

/**
 * Personaje jugable de POOBSoccer. Su posicion (x, y) es el centro horizontal
 * y los pies del personaje. Todo es matematico (sin AWT ni Swing).
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class Character {

    public static final int MIN_STAT = 1;
    public static final int MAX_STAT = 5;
    public static final double POWER_EMPTY = 0.0;
    public static final double POWER_FULL = 100.0;
    public static final double POWER_RECHARGE_PER_SECOND = 5.0;

    public static final double BASE_WIDTH = 60.0;
    public static final double BASE_HEIGHT = 90.0;
    public static final double SPEED_PER_POINT = 70.0;
    public static final double BASE_JUMP_SPEED = 450.0;
    public static final double JUMP_SPEED_PER_POINT = 70.0;
    public static final double GRAVITY = 1800.0;
    public static final double KICK_BUFFER_SECONDS = 0.15;
    public static final double KICK_COOLDOWN_SECONDS = 0.4;

    private final String name;
    private final String country;
    private final int speed;
    private final int jump;
    private final int strength;
    private int goals;
    private double powerBar;
    private boolean powerActive;
    private final Power power;

    private double x;
    private double y;
    private double speedY;
    private double homeX;
    private int facing;
    private int moveDirection;
    private double kickBuffer;
    private double kickCooldown;
    private final List<Effect> effects;

    /**
     * Crea un personaje validando sus datos.
     *
     * @param name Nombre.
     * @param country Pais.
     * @param speed Velocidad (1 a 5).
     * @param jump Salto (1 a 5).
     * @param strength Fuerza (1 a 5).
     * @param power Poder del personaje.
     * @throws POOBSoccerException Si las estadisticas estan fuera de rango o faltan datos.
     */
    public Character(String name, String country, int speed, int jump, int strength, Power power)
            throws POOBSoccerException {
        if (name == null || name.trim().isEmpty() || country == null || country.trim().isEmpty() || power == null) {
            throw new POOBSoccerException(POOBSoccerException.PERSONAJE_INVALIDO);
        }
        this.name = name;
        this.country = country;
        this.power = power;
        validateStat(speed);
        validateStat(jump);
        validateStat(strength);
        this.speed = speed;
        this.jump = jump;
        this.strength = strength;
        this.goals = 0;
        this.powerBar = POWER_EMPTY;
        this.powerActive = false;
        this.facing = 1;
        this.effects = new ArrayList<Effect>();
    }

    /**
     * Crea una copia independiente (sin estado compartido) del personaje.
     *
     * @return Nueva instancia con los mismos datos base.
     * @throws POOBSoccerException Si los datos fueran invalidos.
     */
    public Character copy() throws POOBSoccerException {
        return new Character(name, country, speed, jump, strength, power);
    }

    private static void validateStat(int value) throws POOBSoccerException {
        if (value < MIN_STAT || value > MAX_STAT) {
            throw new POOBSoccerException(POOBSoccerException.ESTADISTICAS_FUERA_DE_RANGO);
        }
    }

    /**
     * Fija la posicion inicial del personaje en su lado de la cancha.
     *
     * @param homeX Posicion horizontal inicial.
     * @param groundY Altura del suelo.
     * @param facing 1 si ataca hacia la derecha, -1 hacia la izquierda.
     */
    public void placeAt(double homeX, double groundY, int facing) {
        this.homeX = homeX;
        this.facing = facing;
        this.x = homeX;
        this.y = groundY;
        this.speedY = 0.0;
    }

    /**
     * Reinicio tras un gol: vuelve a su posicion inicial y limpia efectos temporales.
     *
     * @param groundY Altura del suelo.
     */
    public void resetForRestart(double groundY) {
        this.x = homeX;
        this.y = groundY;
        this.speedY = 0.0;
        this.moveDirection = 0;
        this.kickBuffer = 0.0;
        this.kickCooldown = 0.0;
        this.effects.clear();
    }

    /**
     * Prepara el personaje para un partido nuevo (goles y barra en cero).
     *
     * @param groundY Altura del suelo.
     */
    public void resetForNewMatch(double groundY) {
        resetForRestart(groundY);
        this.goals = 0;
        this.powerBar = POWER_EMPTY;
        this.powerActive = false;
    }

    /**
     * Establece la intencion de movimiento horizontal.
     *
     * @param direction -1 izquierda, 0 quieto, 1 derecha (otros valores se normalizan por signo).
     */
    public void setMoveDirection(int direction) {
        if (direction > 0) {
            this.moveDirection = 1;
        } else if (direction < 0) {
            this.moveDirection = -1;
        } else {
            this.moveDirection = 0;
        }
    }

    /**
     * Intenta saltar. Solo es posible en el suelo y sin efectos que lo impidan.
     *
     * @param groundY Altura del suelo.
     * @return true si salto.
     */
    public boolean jump(double groundY) {
        boolean onGround = y >= groundY;
        if (!canAct() || !onGround) {
            return false;
        }
        speedY = -(BASE_JUMP_SPEED + jump * JUMP_SPEED_PER_POINT);
        return true;
    }

    /**
     * Solicita una patada (queda en espera un instante corto).
     *
     * @return true si el personaje puede patear.
     */
    public boolean requestKick() {
        if (!canAct()) {
            return false;
        }
        kickBuffer = KICK_BUFFER_SECONDS;
        return true;
    }

    /** @return true si hay una patada solicitada y el personaje puede ejecutarla. */
    public boolean canKickNow() {
        return canAct() && kickBuffer > 0.0 && kickCooldown <= 0.0;
    }

    /** Registra que se ejecuto una patada. */
    public void registerKick() {
        kickBuffer = 0.0;
        kickCooldown = KICK_COOLDOWN_SECONDS;
    }

    /**
     * Arma el poder. Solo es posible con la barra llena.
     *
     * @throws POOBSoccerException Si la barra no esta al 100%.
     */
    public void activatePower() throws POOBSoccerException {
        if (powerBar < POWER_FULL) {
            throw new POOBSoccerException(POOBSoccerException.BARRA_PODER_INCOMPLETA);
        }
        powerActive = true;
    }

    /**
     * Si el poder esta armado, lo dispara sobre el oponente y vacia la barra.
     *
     * @param opponent Rival.
     * @param ball Balon.
     */
    public void fireActivePower(Character opponent, Ball ball) {
        if (powerActive) {
            power.applyEffect(opponent, ball);
            powerActive = false;
            powerBar = POWER_EMPTY;
        }
    }

    /** @return true si la barra esta llena. */
    public boolean isPowerReady() {
        return powerBar >= POWER_FULL;
    }


    /**
     * Agrega un efecto; si ya existe uno con el mismo nombre se reemplaza (se reinicia su tiempo).
     *
     * @param effect Efecto a aplicar.
     */
    public void addEffect(Effect effect) {
        for (int i = effects.size() - 1; i >= 0; i--) {
            if (effects.get(i).getName().equals(effect.getName())) {
                effects.remove(i);
            }
        }
        effects.add(effect);
    }

    /** Aturde al personaje durante 3 segundos. */
    public void stun() {
        addEffect(new StunEffect());
    }

    /**
     * @param effectName Nombre del efecto.
     * @return true si el efecto esta activo.
     */
    public boolean hasEffect(String effectName) {
        for (Effect e : effects) {
            if (e.getName().equals(effectName)) {
                return true;
            }
        }
        return false;
    }

    /** @return true si esta aturdido. */
    public boolean isStunned() {
        return hasEffect(StunEffect.NAME);
    }

    /** @return true si ningun efecto impide moverse, saltar o patear. */
    public boolean canAct() {
        for (Effect e : effects) {
            if (!e.allowsActions()) {
                return false;
            }
        }
        return true;
    }

    /** @return Escala actual del cuerpo (producto de los efectos activos). */
    public double getScale() {
        double scale = 1.0;
        for (Effect e : effects) {
            scale *= e.getScale();
        }
        return scale;
    }

    private void removeExpiredEffects() {
        for (int i = effects.size() - 1; i >= 0; i--) {
            if (effects.get(i).isExpired()) {
                effects.remove(i);
            }
        }
    }

    private int horizontalSign() {
        int sign = 1;
        for (Effect e : effects) {
            sign *= e.getHorizontalSign();
        }
        return sign;
    }

    /** @return Nombres de los efectos activos. */
    public List<String> getActiveEffectNames() {
        List<String> names = new ArrayList<String>();
        for (Effect e : effects) {
            names.add(e.getName());
        }
        return names;
    }

    /**
     * Actualiza el personaje: efectos, recarga de la barra, movimiento y gravedad.
     *
     * @param dt Segundos transcurridos.
     * @param field Cancha donde se mueve.
     */
    public void update(double dt, Field field) {
        for (Effect e : effects) {
            e.tick(dt);
        }
        removeExpiredEffects();

        powerBar = Math.min(POWER_FULL, powerBar + POWER_RECHARGE_PER_SECOND * dt);
        kickBuffer = Math.max(0.0, kickBuffer - dt);
        kickCooldown = Math.max(0.0, kickCooldown - dt);

        if (canAct()) {
            x += moveDirection * horizontalSign() * speed * SPEED_PER_POINT * dt;
        }
        double half = getWidth() / 2.0;
        x = Math.max(field.getLeftWallX() + half, Math.min(field.getRightWallX() - half, x));

        speedY += GRAVITY * dt;
        y += speedY * dt;
        if (y >= field.getGroundY()) {
            y = field.getGroundY();
            speedY = 0.0;
        }
    }

    /**
     * Verifica si el hitbox (ampliado por un margen) toca el balon.
     *
     * @param ball Balon.
     * @param margin Margen extra en cada lado.
     * @return true si hay contacto.
     */
    public boolean touches(Ball ball, double margin) {
        double closestX = Math.max(getLeft() - margin, Math.min(ball.getX(), getRight() + margin));
        double closestY = Math.max(getTop() - margin, Math.min(ball.getY(), getBottom() + margin));
        double dx = ball.getX() - closestX;
        double dy = ball.getY() - closestY;
        return dx * dx + dy * dy <= ball.getRadius() * ball.getRadius();
    }

    /**
     * Indica si otro personaje esta lo bastante cerca para recibir una patada directa.
     *
     * @param other Otro personaje.
     * @param range Margen de alcance alrededor del hitbox.
     * @return true si los hitbox (ampliados por el margen) se solapan.
     */
    public boolean isInMeleeRange(Character other, double range) {
        return getLeft() - range <= other.getRight() && getRight() + range >= other.getLeft()
                && getTop() - range <= other.getBottom() && getBottom() + range >= other.getTop();
    }

    /**
     * @return Suma un gol al personaje.
     */
    public void addGoal() {
        goals++;
    }

    /**
     * @return Nombre del personaje.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Pais del personaje.
     */
    public String getCountry() {
        return country;
    }

    /**
     * @return Estadistica de velocidad (1 a 5).
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * @return Estadistica de salto (1 a 5).
     */
    public int getJump() {
        return jump;
    }

    /**
     * @return Estadistica de fuerza (1 a 5).
     */
    public int getStrength() {
        return strength;
    }

    /**
     * @return Goles anotados.
     */
    public int getGoals() {
        return goals;
    }

    /**
     * @return Carga de la barra de poder (0.0 a 100.0).
     */
    public double getPowerBar() {
        return powerBar;
    }

    /**
     * @return true si el poder esta armado.
     */
    public boolean isPowerActive() {
        return powerActive;
    }

    /**
     * @return Poder del personaje.
     */
    public Power getPower() {
        return power;
    }

    /**
     * @return Posicion horizontal (centro).
     */
    public double getX() {
        return x;
    }

    /**
     * @return Posicion vertical (pies).
     */
    public double getY() {
        return y;
    }

    /**
     * @return Sentido de ataque: 1 derecha, -1 izquierda.
     */
    public int getFacing() {
        return facing;
    }

    /**
     * @return Ancho actual del hitbox (con efectos).
     */
    public double getWidth() {
        return BASE_WIDTH * getScale();
    }

    /**
     * @return Alto actual del hitbox (con efectos).
     */
    public double getHeight() {
        return BASE_HEIGHT * getScale();
    }

    /**
     * @return Borde izquierdo del hitbox.
     */
    public double getLeft() {
        return x - getWidth() / 2.0;
    }

    /**
     * @return Borde derecho del hitbox.
     */
    public double getRight() {
        return x + getWidth() / 2.0;
    }

    /**
     * @return Borde superior del hitbox.
     */
    public double getTop() {
        return y - getHeight();
    }

    /**
     * @return Borde inferior del hitbox (pies).
     */
    public double getBottom() {
        return y;
    }

    /**
     * Modifica la barra directamente (util para pruebas y bonificaciones).
     *
     * @param value Valor entre 0 y 100; se ajusta a ese rango.
     */
    public void setPowerBar(double value) {
        this.powerBar = Math.max(POWER_EMPTY, Math.min(POWER_FULL, value));
    }
}
