package presentation;

import static presentation.Texts.text;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;

import dominio.HeadSoccer;

/**
 * Menu principal: empezar a jugar o abrir la configuracion.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class MainMenuP extends Screen {

    private static final String TITLE = "HEAD SOCCER";
    private static final Color BACKGROUND = new Color(30, 144, 255);
    private static final Font TITLE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 56);

    private final JButton play = new JButton();
    private final JButton settings = new JButton();

    /**
     * @param gui Ventana que contiene el menu.
     * @param game Fachada del juego.
     */
    MainMenuP(HeadSoccerGUI gui, HeadSoccer game) {
        super(gui, game);
        setLayout(new GridBagLayout());
        setBackground(BACKGROUND);
        JLabel title = new JLabel(TITLE);
        title.setFont(TITLE_FONT);
        title.setForeground(Color.WHITE);

        GridBagConstraints at = new GridBagConstraints();
        at.insets = new Insets(10, 10, 10, 10);
        at.gridwidth = 2;
        add(title, at);
        at.gridy = 1;
        at.gridwidth = 1;
        add(play, at);
        add(settings, at);

        play.addActionListener(e -> startNewGame());
        settings.addActionListener(e -> openSettings());
    }

    @Override
    protected void onShow() {
        play.setText(text("menu.play"));
        settings.setText(text("menu.settings"));
    }

    private void startNewGame() {
        game.newGame();
        gui.showSetup();
    }

    /** Abre la configuracion (modal) y, al cerrarla, vuelve a rotular el menu en el idioma elegido. */
    private void openSettings() {
        new SettingsD(gui).setVisible(true);
        onShow();
    }
}
