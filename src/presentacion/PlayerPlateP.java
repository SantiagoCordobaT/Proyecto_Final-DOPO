package presentacion;

import static presentacion.Texts.texto;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Placa de un jugador en el marcador: escudo del pais, nombre y barra de poder
 * (con el nombre del poder, o el aviso de poder listo cuando esta armado).
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class PlayerPlateP extends JPanel {

    private static final int PORCENTAJE = 100;
    private final HeadSoccer modelo;
    private final int numero;
    private final Color color;
    private final TeamBadge escudo = new TeamBadge();
    private final JLabel nombre;
    private final JProgressBar poder;

    /**
     * @param modelo    Fachada del dominio.
     * @param numero    Jugador (1 o 2) que muestra la placa.
     * @param ladoEscudo Lado del escudo ({@link BorderLayout#WEST} o
     *                  {@link BorderLayout#EAST}).
     */
    PlayerPlateP(HeadSoccer modelo, int numero, String ladoEscudo) {
        super(new BorderLayout(Resources.ESPACIO, 0));
        Resources recursos = Resources.obtenerInstancia();
        this.modelo = modelo;
        this.numero = numero;
        this.color = recursos.colorJugador(numero);
        nombre = recursos.crearEtiqueta("", Resources.FUENTE_ENCABEZADO);
        poder = recursos.crearBarra(PORCENTAJE, color);
        poder.setStringPainted(true);
        JPanel datos = new JPanel(new GridLayout(2, 1, 0, Resources.ESPACIO / 3));
        datos.setOpaque(false);
        datos.add(nombre);
        datos.add(poder);
        setBackground(Resources.ASFALTO);
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, color),
                BorderFactory.createEmptyBorder(Resources.ESPACIO / 2, Resources.ESPACIO, Resources.ESPACIO / 2,
                        Resources.ESPACIO)));
        add(escudo, ladoEscudo);
        add(datos, BorderLayout.CENTER);
    }

    /**
     * Actualiza la placa con el estado actual del jugador; la barra late mientras
     * el poder esta armado.
     *
     * @param segundos Segundos reales transcurridos desde el cuadro anterior.
     * @throws POOBSoccerException Si no hay un partido iniciado.
     */
    void refrescar(double segundos) throws POOBSoccerException {
        boolean armado = modelo.estaPoderJugadorArmado(numero);
        escudo.establecerPais(modelo.obtenerPaisJugador(numero));
        nombre.setText(modelo.obtenerNombreJugador(numero).toUpperCase());
        poder.setValue((int) Math.round(modelo.obtenerBarraPoderJugador(numero) * PORCENTAJE));
        poder.setString(armado ? texto("hud.armed") : modelo.obtenerNombrePoderJugador(numero));
        if (armado) {
            poder.setForeground(Resources.LISTO);
        } else {
            poder.setForeground(color);
        }
    }
}
