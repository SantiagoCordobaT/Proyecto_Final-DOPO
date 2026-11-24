package presentacion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import dominio.HeadSoccer;
import dominio.ResumenJugador;
import dominio.excepciones.POOBSoccerException;

/**
 * Placa lateral del marcador con el escudo, el nombre y la barra de poder de un
 * jugador.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class PlayerPlateP extends JPanel {

    private final HeadSoccer modelo;
    private final int numero;
    private final Color color;
    private final TeamBadge escudo = new TeamBadge();
    private final JLabel nombre;
    private final JProgressBar poder;

    PlayerPlateP(HeadSoccer modelo, int numero, String ladoEscudo) {
        super(new BorderLayout(Estilo.MARGEN, 0));
        this.modelo = modelo;
        this.numero = numero;
        this.color = Estilo.colorJugador(numero);
        nombre = Estilo.etiqueta("", Estilo.FUENTE_SUBTITULO);
        poder = Estilo.barra(100, color);
        poder.setStringPainted(true);

        JPanel datos = new JPanel(new GridLayout(2, 1, 0, 4));
        datos.setOpaque(false);
        datos.add(nombre);
        datos.add(poder);

        setBackground(Estilo.FONDO);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 4, 0, color),
                BorderFactory.createEmptyBorder(6, Estilo.MARGEN, 6, Estilo.MARGEN)));
        add(escudo, ladoEscudo);
        add(datos, BorderLayout.CENTER);
    }

    void refrescar() throws POOBSoccerException {
        ResumenJugador jugador = modelo.obtenerResumenJugador(numero);
        escudo.establecerPais(jugador.obtenerPais());
        nombre.setText(jugador.obtenerNombre().toUpperCase());
        poder.setValue((int) Math.round(jugador.obtenerCargaPoder() * 100));
        if (jugador.estaPoderArmado()) {
            poder.setString(Texts.t("¡PODER LISTO!", "POWER READY!"));
            poder.setForeground(Estilo.RESALTE);
        } else {
            poder.setString(jugador.obtenerNombrePoder());
            poder.setForeground(color);
        }
    }
}
