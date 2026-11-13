package presentation;

import static presentation.Texts.text;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Dialogo que enfrenta las fichas de los dos jugadores y, al aceptar, inicia el partido.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class CharacterSelectD extends StreetDialog {

    private static final String VERSUS = "VS";
    private static final int RIVAL_INDEX = 1;

    private final HeadSoccerGUI gui;
    private final HeadSoccer model;
    private final CharacterCard first;
    private final CharacterCard second;

    /**
     * @param gui Ventana principal.
     * @param model Fachada del dominio.
     */
    CharacterSelectD(HeadSoccerGUI gui, HeadSoccer model) {
        super(gui, text("select.title"));
        this.gui = gui;
        this.model = model;
        first = new CharacterCard(model, 1, 0);
        second = new CharacterCard(model, 2, RIVAL_INDEX);
        JPanel cards = resources.panel(new BorderLayout(Resources.GAP, 0));
        cards.add(first, BorderLayout.WEST);
        cards.add(resources.label(VERSUS, Resources.TITLE_FONT), BorderLayout.CENTER);
        cards.add(second, BorderLayout.EAST);
        setBody(cards);
        addButton(text("select.play"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                play();
            }
        });
        addButton(text("common.back"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
                gui.openGameModes();
            }
        });
    }

    private void play() {
        try {
            model.selectPlayer(1, first.getSelectedIndex());
            model.selectPlayer(2, second.getSelectedIndex());
            model.startMatch();
            close();
            gui.showMatch();
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.report(this, e);
        }
    }
}
