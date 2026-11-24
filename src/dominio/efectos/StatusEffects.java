package dominio.efectos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Administra la lista de efectos temporales activos sobre un personaje.
 * Permite agregar o reemplazar efectos repetidos, descontar su duracion
 * paso a paso y combinar sus consecuencias sobre acciones, escala y direccion.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class StatusEffects {

    private final List<Effect> efectos = new ArrayList<>();

    /** Crea una lista vacia de efectos activos. */
    public StatusEffects() {
    }

    /**
     * Agrega un efecto nuevo; si ya existe otro del mismo tipo lo quita primero
     * para reiniciar su duracion en vez de acumular duplicados.
     *
     * @param efecto Efecto temporal a aplicar.
     */
    public void agregar(Effect efecto) {
        if (efecto == null) {
            return;
        }
        Iterator<Effect> iterador = efectos.iterator();
        while (iterador.hasNext()) {
            if (iterador.next().esMismoTipoQue(efecto)) {
                iterador.remove();
            }
        }
        efectos.add(efecto);
    }

    /**
     * Descuenta el tiempo transcurrido a cada efecto y elimina con Iterator los
     * que ya expiraron en una sola pasada.
     *
     * @param segundosTranscurridos Segundos transcurridos desde el ultimo paso.
     */
    public void transcurrir(double segundosTranscurridos) {
        Iterator<Effect> iterador = efectos.iterator();
        while (iterador.hasNext()) {
            Effect efecto = iterador.next();
            efecto.transcurrir(segundosTranscurridos);
            if (efecto.haExpirado()) {
                iterador.remove();
            }
        }
    }

    /** Elimina todos los efectos activos del personaje. */
    public void limpiar() {
        efectos.clear();
    }

    /**
     * Verifica si el personaje puede moverse, saltar y patear segun sus efectos
     * actuales (basta uno bloqueante para impedir acciones).
     *
     * @return true si ningun efecto bloquea las acciones.
     */
    public boolean permitenAcciones() {
        for (Effect efecto : efectos) {
            if (!efecto.permiteAcciones()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Calcula la escala combinada del cuerpo multiplicando la escala de todos
     * los efectos activos.
     *
     * @return Factor total de escala (1.0 si no hay reducciones).
     */
    public double escala() {
        double escala = 1.0;
        for (Effect efecto : efectos) {
            escala *= efecto.obtenerEscala();
        }
        return escala;
    }

    /**
     * Calcula el signo horizontal resultante multiplicando el signo de cada
     * efecto activo (1 normal, -1 controles invertidos).
     *
     * @return 1 si avanza normal o -1 si esta invertido.
     */
    public int signoHorizontal() {
        int signo = 1;
        for (Effect efecto : efectos) {
            signo *= efecto.obtenerSignoHorizontal();
        }
        return signo;
    }

    /**
     * Devuelve los nombres de todos los efectos activos para mostrarlos en
     * pantalla.
     * 
     * 
     * @return Arreglo con los nombres de los efectos vigentes.
     */
    public String[] obtenerNombres() {
        String[] nombres = new String[efectos.size()];
        for (int i = 0; i < nombres.length; i++) {
            nombres[i] = efectos.get(i).obtenerNombre();
        }
        return nombres;
    }
}
