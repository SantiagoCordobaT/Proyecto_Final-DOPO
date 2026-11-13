package dominio.poderes;

import dominio.personajes.Character;

/**
 * Barra de poder de un personaje (Experto en informacion): se recarga segun el
 * nivel de "Poder Unico", se arma solo cuando esta llena y, armada, dispara el
 * poder sobre el oponente y vuelve a cero.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class PowerBar {

    public static final double FULL = 100.0;
    public static final double RECHARGE_PER_LEVEL = 2.0;

    private final Power power;
    private final int level;
    private double percent;
    private boolean armed;

    /**
     * Crea una barra vacia y desarmada.
     *
     * @param power Poder que dispara la barra.
     * @param level Nivel de "Poder Unico": la barra recarga {@code RECHARGE_PER_LEVEL * level} % por segundo.
     */
    public PowerBar(Power power, int level) {
        this.power = power;
        this.level = level;
    }

    /** @return Nueva barra vacia con el mismo poder y nivel (sin estado compartido). */
    public PowerBar fresh() {
        return new PowerBar(power, level);
    }

    /**
     * Recarga la barra sin superar el 100 %.
     *
     * @param deltaTimeSeconds Segundos transcurridos.
     */
    public void recharge(double deltaTimeSeconds) {
        percent = Math.min(FULL, percent + RECHARGE_PER_LEVEL * level * deltaTimeSeconds);
    }

    /**
     * Arma el poder. Con la barra incompleta no tiene efecto: la orden de armar
     * es una intencion continua del jugador, no un error.
     */
    public void arm() {
        armed = percent >= FULL;
    }

    /**
     * Si el poder esta armado, lo aplica al oponente y vacia la barra.
     *
     * @param opponent Rival que recibe el efecto.
     */
    public void fireOn(Character opponent) {
        if (armed) {
            power.applyEffect(opponent);
            reset();
        }
    }

    /** Vacia y desarma la barra. */
    public void reset() {
        percent = 0.0;
        armed = false;
    }

    /** @return Fraccion llena de la barra (0.0 a 1.0). */
    public double getCharge() {
        return percent / FULL;
    }

    /** @return true si el poder esta armado. */
    public boolean isArmed() {
        return armed;
    }

    /** @return Nivel de "Poder Unico" (1 a 5). */
    public int getLevel() {
        return level;
    }

    /** @return Nombre del poder. */
    public String getPowerName() {
        return power.getName();
    }

    /** @return Descripcion del poder. */
    public String getPowerDescription() {
        return power.getDescription();
    }
}
