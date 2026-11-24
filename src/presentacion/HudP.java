package presentacion;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Barra superior del partido con las placas de los dos jugadores, el marcador y
 * el estado del encuentro.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class HudP extends JPanel {

    private final HeadSoccer modelo;
    private final PlayerPlateP izquierda;
    private final PlayerPlateP derecha;
    private final JLabel estado;
    private final JLabel marcador;
    private final JLabel ayuda;

    HudP(HeadSoccer modelo) {
        super(new GridLayout(1, 3));
        this.modelo = modelo;
        izquierda = new PlayerPlateP(modelo, 1, BorderLayout.WEST);
        derecha = new PlayerPlateP(modelo, 2, BorderLayout.EAST);
        estado = Style.etiqueta("", Style.FUENTE_NORMAL);
        marcador = Style.etiqueta("", Style.FUENTE_MARCADOR);
        ayuda = Style.etiqueta("", Style.FUENTE_PEQUENA);
        estado.setForeground(Style.RESALTE);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(Style.FONDO);
        centro.add(estado, BorderLayout.NORTH);
        centro.add(marcador, BorderLayout.CENTER);
        centro.add(ayuda, BorderLayout.SOUTH);

        add(izquierda);
        add(centro);
        add(derecha);
    }

    void rotular() {
        ayuda.setText(Texts.t("P / Esc: pausa", "P / Esc: pause"));
    }

    void refrescar() throws POOBSoccerException {
        int goles1 = modelo.obtenerResumenJugador(1).obtenerGoles();
        int goles2 = modelo.obtenerResumenJugador(2).obtenerGoles();
        marcador.setText(goles1 + " - " + goles2);
        izquierda.refrescar();
        derecha.refrescar();
        estado.setText(modelo.obtenerEstadoPartido());
    }
}
