package presentation;

import static presentation.Texts.text;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Configuracion del partido: la modalidad y el tipo de partido. La fachada decide
 * si el tipo admite la modalidad (Supervivencia solo en PvsM).
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class MatchSetupP extends StepScreen {

    private final JLabel modeLabel = new JLabel();
    private final JLabel typeLabel = new JLabel();
    private final JComboBox<String> modes;
    private final JComboBox<String> types;

    /**
     * @param gui Ventana que contiene la pantalla.
     * @param game Fachada del juego: da los nombres de modalidades y tipos de partido.
     */
    MatchSetupP(HeadSoccerGUI gui, HeadSoccer game) {
        super(gui, game, "setup.title");
        modes = new JComboBox<String>(game.getModeNames());
        types = new JComboBox<String>(game.getMatchTypeNames());

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints at = new GridBagConstraints();
        at.insets = new Insets(8, 8, 8, 8);
        at.anchor = GridBagConstraints.WEST;
        form.add(modeLabel, at);
        form.add(modes, at);
        at.gridy = 1;
        form.add(typeLabel, at);
        form.add(types, at);
        add(form, BorderLayout.CENTER);
    }

    @Override
    protected void refresh() {
        modeLabel.setText(text("setup.mode"));
        typeLabel.setText(text("setup.type"));
    }

    @Override
    protected void select() {
        try {
            game.selectMode(modes.getSelectedIndex());
            game.selectMatchType(types.getSelectedIndex());
            gui.showCharacterSelect();
        } catch (POOBSoccerException e) {
            report(this, e);
        }
    }

    @Override
    protected void back() {
        gui.showMenu();
    }
}
