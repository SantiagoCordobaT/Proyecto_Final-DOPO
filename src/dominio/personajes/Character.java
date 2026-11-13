package dominio.personajes;

import dominio.Ball;
import dominio.Field;
import dominio.Named;
import dominio.efectos.Effect;
import dominio.efectos.StatusEffects;
import dominio.efectos.StunEffect;
import dominio.excepciones.POOBSoccerException;
import dominio.poderes.Power;
import dominio.poderes.PowerBar;

/**
 * Personaje jugable de POOBSoccer. Su posicion (x, y) es el centro horizontal
 * y los pies del personaje. Es experto en su cuerpo: se mueve, salta, patea y
 * empuja el balon. Todo es matematico (sin AWT ni Swing).
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class Character implements Named {

    public static final int MIN_STAT = 1;
    public static final int MAX_STAT = 5;

    public static final double BASE_WIDTH = 60.0;
    public static final double BASE_HEIGHT = 90.0;
    public static final double SPEED_PER_POINT = 70.0;
    public static final double BASE_JUMP_SPEED = 450.0;
    public static final double JUMP_SPEED_PER_POINT = 70.0;
    public static final double GRAVITY = 1800.0;
    public static final double KICK_BUFFER_SECONDS = 0.15;
    public static final double KICK_COOLDOWN_SECONDS = 0.4;

    // Alcance de la patada alrededor del hitbox y alcance cuerpo a cuerpo contra el rival
    private static final double KICK_REACH = 15.0;
    private static final double MELEE_RANGE = 10.0;

    private final String name;
    private final String country;
    private final int speed;
    private final int jump;
    private final int strength;
    private int goals;
    private final PowerBar powerBar;
    private final StatusEffects effects = new StatusEffects();

    private double x;
    private double y;
    private double speedY;
    private double homeX;
    private int facing = 1;
    private int moveDirection;
    private double kickBuffer;
    private double kickCooldown;

    /**
     * Crea un personaje validando sus datos.
     *
     * @param name Nombre.
     * @param country Pais.
     * @param speed Velocidad (1 a 5).
     * @param jump Salto (1 a 5).
     * @param strength Fuerza (1 a 5).
     * @param powerLevel Poder Unico (1 a 5): rapidez con la que se recarga la barra de poder.
     * @param power Poder del personaje.
     * @throws POOBSoccerException Si las estadisticas estan fuera de rango o faltan datos.
     */
    public Character(String name, String country, int speed, int jump, int strength, int powerLevel,
            Power power) throws POOBSoccerException {
        this(name, country, speed, jump, strength, new PowerBar(power, powerLevel));
        if (name == null || name.trim().isEmpty() || country == null || country.trim().isEmpty() || power == null) {
            throw new POOBSoccerException(POOBSoccerException.PERSONAJE_INVALIDO);
        }
        validateStat(speed);
        validateStat(jump);
        validateStat(strength);
        validateStat(powerLevel);
    }

    /**
     * Constructor base, sin validacion: lo usan el constructor publico (que valida
     * despues) y {@link #copy()} (Prototype). El personaje empieza sin efectos.
     *
     * @param name Nombre.
     * @param country Pais.
     * @param speed Velocidad.
     * @param jump Salto.
     * @param strength Fuerza.
     * @param powerBar Barra de poder propia de la copia.
     */
    private Character(String name, String country, int speed, int jump, int strength, PowerBar powerBar) {
        this.name = name;
        this.country = country;
        this.speed = speed;
        this.jump = jump;
        this.strength = strength;
        this.powerBar = powerBar;
    }

    /**
     * Crea una copia independiente (sin estado compartido) del personaje.
     *
     * @return Nueva instancia con los mismos datos base y la barra vacia.
     */
    public Character copy() {
        return new Character(name, country, speed, jump, strength, powerBar.fresh());
    }

    private static void validateStat(int value) throws POOBSoccerException {
        if (value < MIN_STAT || value > MAX_STAT) {
            throw new POOBSoccerException(POOBSoccerException.ESTADISTICAS_FUERA_DE_RANGO);
        }
    }

    /**
     * Entra a la cancha para un partido nuevo: fija su lado, pone goles y barra
     * en cero y queda en su posicion inicial.
     *
     * @param homeX Posicion horizontal inicial.
     * @param facing 1 si ataca hacia la derecha, -1 hacia la izquierda.
     */
    public void enterAt(double homeX, int facing) {
        this.homeX = homeX;
        this.facing = facing;
        this.goals = 0;
        powerBar.reset();
        restart();
    }

    /** Reinicio tras un gol: vuelve a su posicion inicial, en el suelo, y limpia efectos temporales. */
    public void restart() {
        this.x = homeX;
        this.y = Field.GROUND_Y;
        this.speedY = 0.0;
        this.moveDirection = 0;
        this.kickBuffer = 0.0;
        this.kickCooldown = 0.0;
        this.effects.clear();
    }

    /**
     * Establece la intencion de movimiento horizontal.
     *
     * @param direction -1 izquierda, 0 quieto, 1 derecha (otros valores se normalizan por signo).
     */
    public void setMoveDirection(int direction) {
        this.moveDirection = Integer.signum(direction);
    }

    /** Salta; solo es posible en el suelo y sin efectos que lo impidan. */
    public void jump() {
        boolean onGround = y >= Field.GROUND_Y;
        if (canAct() && onGround) {
            speedY = -(BASE_JUMP_SPEED + jump * JUMP_SPEED_PER_POINT);
        }
    }

    /** Solicita una patada (queda en espera un instante corto) si ningun efecto lo impide. */
    public void requestKick() {
        if (canAct()) {
            kickBuffer = KICK_BUFFER_SECONDS;
        }
    }

    /**
     * Ejecuta la patada solicitada si alcanza el balon o al rival: aturde al rival
     * en rango cuerpo a cuerpo y patea el balon en alcance, disparando el poder armado.
     *
     * @param ball Balon.
     * @param opponent Rival.
     * @return true si pateo el balon.
     */
    public boolean tryKick(Ball ball, Character opponent) {
        boolean ballInReach = overlaps(ball, KICK_REACH);
        boolean opponentInReach = isInMeleeRange(opponent);
        if (!canKickNow() || !(ballInReach || opponentInReach)) {
            return false;
        }
        kickBuffer = 0.0;
        kickCooldown = KICK_COOLDOWN_SECONDS;
        if (opponentInReach) {
            opponent.stun();
        }
        if (ballInReach) {
            ball.kick(strength, facing);
            powerBar.fireOn(opponent);
        }
        return ballInReach;
    }

    /**
     * @param ball Balon.
     * @return true si el hitbox toca el balon.
     */
    public boolean touches(Ball ball) {
        return overlaps(ball, 0.0);
    }

    /**
     * Empuja el balon que toca: rebota en la cabeza si viene desde arriba; si no, sale hacia un lado.
     *
     * @param ball Balon en contacto.
     */
    public void push(Ball ball) {
        if (ball.isAbove(getTop())) {
            ball.bounceUp(getTop());
        } else {
            int side = ball.sideOf(x);
            ball.pushSideways(x + side * getWidth() / 2.0, side);
        }
    }

    /** Arma el poder; solo tiene efecto con la barra llena. */
    public void armPower() {
        powerBar.arm();
    }

    /**
     * Agrega un efecto; si ya existe uno del mismo tipo se reemplaza (se reinicia su tiempo).
     *
     * @param effect Efecto a aplicar.
     */
    public void addEffect(Effect effect) {
        effects.add(effect);
    }

    /** Aturde al personaje durante 3 segundos. */
    public void stun() {
        addEffect(new StunEffect());
    }

    /**
     * Actualiza el personaje: efectos, recarga de la barra, movimiento dentro de la cancha y gravedad.
     *
     * @param dt Segundos transcurridos.
     */
    public void update(double dt) {
        effects.tick(dt);
        powerBar.recharge(dt);
        kickBuffer = Math.max(0.0, kickBuffer - dt);
        kickCooldown = Math.max(0.0, kickCooldown - dt);

        if (canAct()) {
            x += moveDirection * effects.horizontalSign() * speed * SPEED_PER_POINT * dt;
        }
        double half = getWidth() / 2.0;
        x = Math.max(half, Math.min(Field.WIDTH - half, x));

        speedY += GRAVITY * dt;
        y += speedY * dt;
        if (y >= Field.GROUND_Y) {
            y = Field.GROUND_Y;
            speedY = 0.0;
        }
    }

    /** Suma un gol al personaje. */
    public void addGoal() {
        goals++;
    }

    /** @return true si ningun efecto impide moverse, saltar o patear. */
    private boolean canAct() {
        return effects.allowActions();
    }

    /** @return true si hay una patada solicitada y el personaje puede ejecutarla. */
    private boolean canKickNow() {
        return canAct() && kickBuffer > 0.0 && kickCooldown <= 0.0;
    }

    /**
     * @param ball Balon.
     * @param margin Margen extra en cada lado del hitbox.
     * @return true si el hitbox ampliado por el margen toca el balon.
     */
    private boolean overlaps(Ball ball, double margin) {
        return ball.overlaps(getLeft() - margin, getTop() - margin, getRight() + margin, getBottom() + margin);
    }

    /**
     * @param other Otro personaje.
     * @return true si los hitbox, ampliados por el alcance cuerpo a cuerpo, se solapan.
     */
    private boolean isInMeleeRange(Character other) {
        return getLeft() - MELEE_RANGE <= other.getRight() && getRight() + MELEE_RANGE >= other.getLeft()
                && getTop() - MELEE_RANGE <= other.getBottom() && getBottom() + MELEE_RANGE >= other.getTop();
    }

    /** @return Escala actual del cuerpo (producto de los efectos activos). */
    private double getScale() {
        return effects.scale();
    }

    /** @return Nombres de los efectos activos. */
    public String[] getEffectNames() {
        return effects.names();
    }

    /**
     * @return Nombre del personaje.
     */
    @Override
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
     * @return Estadistica de Poder Unico (1 a 5).
     */
    public int getPowerLevel() {
        return powerBar.getLevel();
    }

    /**
     * @return Goles anotados.
     */
    public int getGoals() {
        return goals;
    }

    /**
     * @return Fraccion llena de la barra de poder (0.0 a 1.0).
     */
    public double getPowerCharge() {
        return powerBar.getCharge();
    }

    /**
     * @return true si el poder esta armado.
     */
    public boolean isPowerArmed() {
        return powerBar.isArmed();
    }

    /**
     * @return Nombre del poder del personaje.
     */
    public String getPowerName() {
        return powerBar.getPowerName();
    }

    /**
     * @return Descripcion del poder del personaje.
     */
    public String getPowerDescription() {
        return powerBar.getPowerDescription();
    }

    /**
     * @return Hitbox actual (con efectos) como {izquierda, arriba, ancho, alto}.
     */
    public double[] getBounds() {
        return new double[] { getLeft(), getTop(), getWidth(), getHeight() };
    }

    /**
     * @return Posicion horizontal (centro).
     */
    public double getX() {
        return x;
    }

    /**
     * @return Borde superior del hitbox.
     */
    public double getTop() {
        return y - getHeight();
    }

    /**
     * @return Altura del centro del hitbox.
     */
    public double getCenterY() {
        return getTop() + getHeight() / 2.0;
    }

    /**
     * @return Sentido de ataque: 1 derecha, -1 izquierda.
     */
    public int getFacing() {
        return facing;
    }

    private double getWidth() {
        return BASE_WIDTH * getScale();
    }

    private double getHeight() {
        return BASE_HEIGHT * getScale();
    }

    private double getLeft() {
        return x - getWidth() / 2.0;
    }

    private double getRight() {
        return x + getWidth() / 2.0;
    }

    private double getBottom() {
        return y;
    }
}
