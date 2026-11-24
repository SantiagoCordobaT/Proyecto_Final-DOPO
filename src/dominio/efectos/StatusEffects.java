package dominio.efectos;

import java.util.ArrayList;
import java.util.List;

/**
 * Lista de efectos temporales activos sobre un personaje: evita duplicados,
 * descuenta su tiempo y consulta bloqueo, tamaño reducido o controles
 * invertidos.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class StatusEffects {

    private final List<Effect> efectos = new ArrayList<>();

    public StatusEffects() {
    }

    /**
     * Agrega un efecto y, si ya existe uno igual, lo quita antes para reiniciar su
     * tiempo.
     */
    public void agregar(Effect efecto) {
        if (efecto == null) {
            return;
        }
        for (int i = efectos.size() - 1; i >= 0; i--) {
            if (efectos.get(i).esMismoTipoQue(efecto)) {
                efectos.remove(i);
            }
        }
        efectos.add(efecto);
    }

    /**
     * Descuenta tiempo a cada efecto y borra de atrás hacia adelante los que
     * expiraron.
     */
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
            if (efectos.get(i).obtenerEscala() != 1.0) {
                return efectos.get(i).obtenerEscala();
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
