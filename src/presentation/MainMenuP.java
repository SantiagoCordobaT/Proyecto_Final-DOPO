package presentation;

import static presentation.Texts.text;

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

    private static final String TITLE = "POOBSOCCER";

    private final Resources resources = Resources.getInstance();
    private final JLabel subtitle;
    private final JButton play;
    private final JButton settings;
    private final JButton exit;

    /** @param gui Ventana principal. */
    MainMenuP(HeadSoccerGUI gui) {
        super(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(Resources.GAP * 2, Resources.GAP, Resources.GAP * 2, Resources.GAP));
        JLabel title = resources.label(TITLE, Resources.TITLE_FONT);
        title.setForeground(Resources.ACCENT);
        subtitle = resources.label("", Resources.HEADING_FONT);
        play = resources.button("", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.openGameModes();
            }
        });
        settings = resources.button("", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.openSettings();
            }
        });
        exit = resources.button("", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.close();
            }
        });
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setOpaque(false);
        header.add(title);
        header.add(subtitle);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, Resources.GAP * 2, 0));
        buttons.setOpaque(false);
        buttons.add(play);
        buttons.add(settings);
        buttons.add(exit);
        add(header, BorderLayout.NORTH);
        add(buttons, BorderLayout.SOUTH);
        refresh();
    }

    /** Rotula el menu en el idioma actual. */
    void refresh() {
        subtitle.setText(text("menu.subtitle"));
        play.setText(text("menu.play").toUpperCase());
        settings.setText(text("menu.settings").toUpperCase());
        exit.setText(text("menu.exit").toUpperCase());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        resources.paintBackdrop(g, getWidth(), getHeight());
    }
}
