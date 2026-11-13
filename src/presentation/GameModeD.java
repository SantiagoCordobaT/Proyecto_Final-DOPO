package presentation;

import static presentation.Texts.text;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Dialogo que elige la modalidad y el tipo de partido. Solo reenvia la seleccion
 * a la fachada; las combinaciones invalidas las rechaza el dominio.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class GameModeD extends StreetDialog {

    private final HeadSoccerGUI gui;
    private final HeadSoccer model;
    private final JList<String> modes;
    private final JList<String> matchTypes;
    private final JButton select;

    /**
     * @param gui Ventana principal.
     * @param model Fachada del dominio.
     */
    GameModeD(HeadSoccerGUI gui, HeadSoccer model) {
        super(gui, text("setup.title"));
        this.gui = gui;
        this.model = model;
        modes = resources.list(model.getModeNames());
        matchTypes = resources.list(model.getMatchTypeNames());
        JPanel lists = resources.panel(new GridLayout(1, 2, Resources.GAP, 0));
        lists.add(column("setup.mode", modes));
        lists.add(column("setup.type", matchTypes));
        setBody(lists);
        select = addButton(text("common.select"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                select();
            }
        });
        addButton(text("common.back"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        ListSelectionListener complete = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                select.setEnabled(!modes.isSelectionEmpty() && !matchTypes.isSelectionEmpty());
            }
        };
        modes.addListSelectionListener(complete);
        matchTypes.addListSelectionListener(complete);
    }

    private JPanel column(String titleKey, JList<String> list) {
        JPanel column = resources.panel(new BorderLayout(0, Resources.GAP / 2));
        column.add(resources.label(text(titleKey), Resources.TEXT_FONT), BorderLayout.NORTH);
        column.add(new JScrollPane(list), BorderLayout.CENTER);
        return column;
    }

    private void select() {
        try {
            model.selectMode(modes.getSelectedIndex());
            model.selectMatchType(matchTypes.getSelectedIndex());
            close();
            gui.openCharacterSelect();
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.report(this, e);
        }
    }
}
