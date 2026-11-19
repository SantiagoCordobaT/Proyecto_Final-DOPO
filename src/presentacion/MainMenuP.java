package presentacion;

import static presentacion.Texts.text;

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
 * Menu principal sobre el fondo de la cancha callejera: titulo al norte y
 * botones al sur. Cada boton solo avisa a la ventana principal.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class MainMenuP extends JPanel {

    private static final String TITULO = "POOBSOCCER";

    private final Resources recursos = Resources.getInstance();
    private final JLabel subtitulo;
    private final JButton jugar;
    private final JButton configuracion;
    private final JButton salir;

    /** @param ventana Ventana principal. */
    MainMenuP(HeadSoccerGUI ventana) {
        super(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(Resources.ESPACIO * 2, Resources.ESPACIO, Resources.ESPACIO * 2,
                Resources.ESPACIO));
        JLabel titulo = recursos.label(TITULO, Resources.FUENTE_TITULO);
        titulo.setForeground(Resources.ACENTO);
        subtitulo = recursos.label("", Resources.FUENTE_ENCABEZADO);
        jugar = recursos.button("", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ventana.openGameModes();
            }
        });
        configuracion = recursos.button("", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ventana.openSettings();
            }
        });
        salir = recursos.button("", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ventana.close();
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
        refresh();
    }

    /** Rotula el menu en el idioma actual. */
    void refresh() {
        subtitulo.setText(text("menu.subtitle"));
        jugar.setText(text("menu.play").toUpperCase());
        configuracion.setText(text("menu.settings").toUpperCase());
        salir.setText(text("menu.exit").toUpperCase());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        recursos.paintBackdrop(g, getWidth(), getHeight());
    }
}
