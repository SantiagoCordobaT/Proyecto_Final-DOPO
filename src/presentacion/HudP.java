package presentacion;

import static presentacion.Texts.texto;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Barra superior del partido que muestra la placa de cada jugador, el estado
 * del partido, los goles y el aviso de pausa.
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

    void rotular() {
        ayuda.setText(texto("match.hint"));
    }

    void refrescar(double segundos) throws POOBSoccerException {
        String actual = modelo.obtenerGolesJugador(1) + SEPARADOR_MARCADOR + modelo.obtenerGolesJugador(2);
        if (!actual.equals(marcador.getText())) {
            destello = SEGUNDOS_DESTELLO;
            marcador.setText(actual);
        }
        destello -= segundos;
        if (destello < 0.0) {
            destello = 0.0;
        }
        if (destello > 0.0) {
            marcador.setForeground(Resources.LISTO);
        } else {
            marcador.setForeground(Resources.TEXTO);
        }
        izquierda.refrescar();
        derecha.refrescar();
        estado.setText(modelo.obtenerEstadoPartido());
    }
}
