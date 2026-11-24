package dominio.efectos;

import java.util.ArrayList;
import java.util.List;

/**
 * Guarda los efectos activos de un personaje y recalcula si está bloqueado,
 * confundido o encogido.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class StatusEffects {

    private final List<Effect> activos = new ArrayList<>();
    private boolean bloqueado;
    private boolean invertido;
    private double escala = 1.0;

    public StatusEffects() {
    }

    public void agregar(Effect efecto) {
        if (efecto == null) {
            return;
        }
        // Si ya tiene el mismo efecto se quita el anterior para reiniciar el tiempo
        for (int i = activos.size() - 1; i >= 0; i--) {
            if (activos.get(i).esMismoTipoQue(efecto)) {
                activos.remove(i);
            }
        }
        activos.add(efecto);
        recalcular();
    }

    public void transcurrir(double segundos) {
        boolean cambio = false;
        for (int i = activos.size() - 1; i >= 0; i--) {
            Effect efecto = activos.get(i);
            efecto.transcurrir(segundos);
            if (efecto.haExpirado()) {
                activos.remove(i);
                cambio = true;
            }
        }
        if (cambio) {
            recalcular();
        }
    }

    public void limpiar() {
        activos.clear();
        recalcular();
    }

    void bloquear() {
        bloqueado = true;
    }

    void invertirMovimiento() {
        invertido = true;
    }

    void cambiarEscala(double nuevaEscala) {
        escala = nuevaEscala;
    }

    private void recalcular() {
        bloqueado = false;
        invertido = false;
        escala = 1.0;
        for (int i = 0; i < activos.size(); i++) {
            activos.get(i).aplicarSobre(this);
        }
    }

    public boolean permitenAcciones() {
        return !bloqueado;
    }

    public double escala() {
        return escala;
    }

    public int signoHorizontal() {
        return invertido ? -1 : 1;
    }

    public String[] obtenerNombres() {
        String[] nombres = new String[activos.size()];
        for (int i = 0; i < activos.size(); i++) {
            nombres[i] = activos.get(i).obtenerNombre();
        }
        return nombres;
    }
}
