package presentacion;

import static presentacion.Texts.text;

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
    private static final double SEGUNDOS_PULSO = 0.5;
    private static final Color[] COLORES_PULSO = { Resources.LISTO, Resources.TEXTO };

    private final HeadSoccer modelo;
    private final int numero;
    private final Color color;
    private final TeamBadge escudo = new TeamBadge();
    private final JLabel nombre;
    private final JProgressBar poder;
    private double pulso;

    /**
     * @param modelo    Fachada del dominio.
     * @param numero    Jugador (1 o 2) que muestra la placa.
     * @param ladoEscudo Lado del escudo ({@link BorderLayout#WEST} o
     *                  {@link BorderLayout#EAST}).
     */
    PlayerPlateP(HeadSoccer modelo, int numero, String ladoEscudo) {
        super(new BorderLayout(Resources.ESPACIO, 0));
        Resources recursos = Resources.getInstance();
        this.modelo = modelo;
        this.numero = numero;
        this.color = recursos.playerColor(numero);
        nombre = recursos.label("", Resources.FUENTE_ENCABEZADO);
        poder = recursos.bar(PORCENTAJE, color);
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
    void refresh(double segundos) throws POOBSoccerException {
        boolean armado = modelo.isPlayerPowerArmed(numero);
        escudo.setCountry(modelo.getPlayerCountry(numero));
        nombre.setText(modelo.getPlayerName(numero).toUpperCase());
        poder.setValue((int) Math.round(modelo.getPlayerPowerBar(numero) * PORCENTAJE));
        poder.setString(armado ? text("hud.armed") : modelo.getPlayerPowerName(numero));
        pulso = (pulso + segundos) % SEGUNDOS_PULSO;
        poder.setForeground(armado ? COLORES_PULSO[(int) (pulso * COLORES_PULSO.length / SEGUNDOS_PULSO)] : color);
    }
}
