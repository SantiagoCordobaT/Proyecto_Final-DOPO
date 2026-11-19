package presentacion;

import static presentacion.Texts.texto;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Marcador del partido: placa de cada jugador a los lados y, en el centro, el
 * estado del partido (reloj, meta o balones), el tanteador y la ayuda de pausa.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class HudP extends JPanel {

    private static final String SEPARADOR_MARCADOR = " - ";
    private static final double SEGUNDOS_DESTELLO = 0.8;

    private final HeadSoccer modelo;
    private final PlayerPlateP izquierda;
    private final PlayerPlateP derecha;
    private final JLabel estado;
    private final JLabel marcador;
    private final JLabel ayuda;
    private double destello;

    /** @param modelo Fachada del dominio. */
    HudP(HeadSoccer modelo) {
        super(new GridLayout(1, 3));
        Resources recursos = Resources.obtenerInstancia();
        this.modelo = modelo;
        izquierda = new PlayerPlateP(modelo, 1, BorderLayout.WEST);
        derecha = new PlayerPlateP(modelo, 2, BorderLayout.EAST);
        estado = recursos.crearEtiqueta("", Resources.FUENTE_TEXTO);
        marcador = recursos.crearEtiqueta("", Resources.FUENTE_MARCADOR);
        ayuda = recursos.crearEtiqueta("", Resources.FUENTE_AYUDA);
        estado.setForeground(Resources.LISTO);
        JPanel centro = recursos.crearPanel(new BorderLayout());
        centro.add(estado, BorderLayout.NORTH);
        centro.add(marcador, BorderLayout.CENTER);
        centro.add(ayuda, BorderLayout.SOUTH);
        add(izquierda);
        add(centro);
        add(derecha);
    }

    /** Rotula la ayuda del marcador en el idioma actual. */
    void rotular() {
        ayuda.setText(texto("match.hint"));
    }

    /**
     * Actualiza todo el marcador con el estado actual del partido; el tanteador
     * destella un instante cada vez que cambia.
     *
     * @param segundos Segundos reales transcurridos desde el cuadro anterior.
     * @throws POOBSoccerException Si no hay un partido iniciado.
     */
    void refrescar(double segundos) throws POOBSoccerException {
        String actual = modelo.obtenerGolesJugador(1) + SEPARADOR_MARCADOR + modelo.obtenerGolesJugador(2);
        if (!actual.equals(marcador.getText())) {
            destello = SEGUNDOS_DESTELLO;
            marcador.setText(actual);
        }
        destello = Math.max(0.0, destello - segundos);
        marcador.setForeground(destello > 0.0 ? Resources.LISTO : Resources.TEXTO);
        izquierda.refrescar(segundos);
        derecha.refrescar(segundos);
        estado.setText(modelo.obtenerEstadoPartido());
    }
}
