package dominio.efectos;

import java.util.ArrayList;
import java.util.List;

/**
 * Administra los efectos temporales activos sobre un personaje.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class StatusEffects {

    private final List<Effect> efectos = new ArrayList<>();

    public StatusEffects() {
    }

    public void agregar(Effect efecto) {
        if (efecto == null) {
            return;
        }
        // Si ya existe el mismo efecto se reemplaza para reiniciar su duración
        for (int i = efectos.size() - 1; i >= 0; i--) {
            if (efectos.get(i).esMismoTipoQue(efecto)) {
                efectos.remove(i);
            }
        }
        efectos.add(efecto);
    }

    public void transcurrir(double segundosTranscurridos) {
        for (int i = efectos.size() - 1; i >= 0; i--) {
            Effect efecto = efectos.get(i);
            efecto.transcurrir(segundosTranscurridos);
            if (efecto.haExpirado()) {
                efectos.remove(i);
            }
        }
    }

    public void limpiar() {
        efectos.clear();
    }

    public boolean permitenAcciones() {
        for (int i = 0; i < efectos.size(); i++) {
            if (!efectos.get(i).permiteAcciones()) {
                return false;
            }
        }
        return true;
    }

    public double escala() {
        for (int i = 0; i < efectos.size(); i++) {
            double escalaActual = efectos.get(i).obtenerEscala();
            if (escalaActual != 1.0) {
                return escalaActual;
            }
        }
        return 1.0;
    }

    public int signoHorizontal() {
        for (int i = 0; i < efectos.size(); i++) {
            if (efectos.get(i).obtenerSignoHorizontal() < 0) {
                return -1;
            }
        }
        return 1;
    }

    public String[] obtenerNombres() {
        String[] nombres = new String[efectos.size()];
        for (int i = 0; i < nombres.length; i++) {
            nombres[i] = efectos.get(i).obtenerNombre();
        }
        return nombres;
    }
}
