package presentacion;

import static presentacion.Texts.texto;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Panel del menú principal con el título del juego y los botones de inicio,
 * configuración y salida.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class MainMenuP extends JPanel {

    private static final String TITULO = "POOBSOCCER";

    private final Resources recursos = Resources.obtenerInstancia();
    private final JLabel subtitulo;
    private final JButton jugar;
    private final JButton configuracion;
    private final JButton salir;

    MainMenuP(HeadSoccerGUI ventana) {
        super(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(Resources.ESPACIO * 2, Resources.ESPACIO, Resources.ESPACIO * 2,
                Resources.ESPACIO));
        JLabel titulo = recursos.crearEtiqueta(TITULO, Resources.FUENTE_TITULO);
        titulo.setForeground(Resources.ACENTO);
        subtitulo = recursos.crearEtiqueta("", Resources.FUENTE_ENCABEZADO);
        jugar = recursos.crearBoton("", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ventana.abrirModosJuego();
            }
        });
        configuracion = recursos.crearBoton("", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ventana.abrirConfiguracion();
            }
        });
        salir = recursos.crearBoton("", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ventana.cerrar();
            }
        });
        JPanel encabezado = new JPanel(new GridLayout(2, 1));
        encabezado.setOpaque(false);
        encabezado.add(titulo);
        encabezado.add(subtitulo);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, Resources.ESPACIO * 2, 0));
        botones.setOpaque(false);
        botones.add(jugar);
        botones.add(configuracion);
        botones.add(salir);
        add(encabezado, BorderLayout.NORTH);
        add(botones, BorderLayout.SOUTH);
        refrescar();
    }

    void refrescar() {
        subtitulo.setText(texto("menu.subtitle"));
        jugar.setText(texto("menu.play").toUpperCase());
        configuracion.setText(texto("menu.settings").toUpperCase());
        salir.setText(texto("menu.exit").toUpperCase());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        recursos.dibujarFondo(g, getWidth(), getHeight());
    }
}
