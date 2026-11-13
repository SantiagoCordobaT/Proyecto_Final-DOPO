package presentation;

import static presentation.Texts.text;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Ficha de seleccion de un jugador: lista de personajes, escudo del pais,
 * estadisticas en barras y poder especial. Solo consulta a la fachada.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class CharacterCard extends JPanel {

    private static final int MAX_STAT = 5;

    private final Resources resources = Resources.getInstance();
    private final HeadSoccer model;
    private final int number;
    private final JList<String> characters;
    private final TeamBadge badge = new TeamBadge();
    private final JLabel country;
    private final JPanel stats;
    private final JProgressBar speed;
    private final JProgressBar jump;
    private final JProgressBar strength;
    private final JProgressBar power;
    private final JLabel special;
    private final JLabel description;

    /**
     * @param model Fachada del dominio.
     * @param number Jugador (1 o 2) al que pertenece la ficha.
     * @param initial Personaje seleccionado al abrir.
     */
    CharacterCard(HeadSoccer model, int number, int initial) {
        super(new BorderLayout(Resources.GAP, Resources.GAP));
        this.model = model;
        this.number = number;
        setBackground(Resources.CONCRETE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(resources.playerColor(number), 3),
                BorderFactory.createEmptyBorder(Resources.GAP, Resources.GAP, Resources.GAP, Resources.GAP)));
        characters = resources.list(model.getCharacterNames());
        country = resources.label("", Resources.TEXT_FONT);
        stats = new JPanel(new GridLayout(0, 1, 0, Resources.GAP / 2));
        stats.setOpaque(false);
        JPanel team = new JPanel(new FlowLayout(FlowLayout.CENTER, Resources.GAP, 0));
        team.setOpaque(false);
        team.add(badge);
        team.add(country);
        stats.add(team);
        speed = addStat("card.speed");
        jump = addStat("card.jump");
        strength = addStat("card.strength");
        power = addStat("card.power");
        special = resources.label("", Resources.TEXT_FONT);
        description = resources.label("", Resources.TEXT_FONT);
        stats.add(special);
        stats.add(description);
        add(resources.label(text("select.player") + " " + number, Resources.HEADING_FONT), BorderLayout.NORTH);
        add(new JScrollPane(characters), BorderLayout.WEST);
        add(stats, BorderLayout.CENTER);
        characters.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                refresh();
            }
        });
        characters.setSelectedIndex(initial);
        refresh();
    }

    /** @return Indice del personaje elegido en el catalogo. */
    int getSelectedIndex() {
        return characters.getSelectedIndex();
    }

    private JProgressBar addStat(String key) {
        JProgressBar bar = resources.bar(MAX_STAT, resources.playerColor(number));
        JPanel row = new JPanel(new GridLayout(1, 2, Resources.GAP, 0));
        row.setOpaque(false);
        row.add(resources.label(text(key), Resources.TEXT_FONT));
        row.add(bar);
        stats.add(row);
        return bar;
    }

    /** Muestra los datos del personaje seleccionado tal como los entrega el dominio. */
    private void refresh() {
        int index = characters.getSelectedIndex();
        try {
            String team = model.getCharacterCountry(index);
            badge.setCountry(team);
            country.setText(team);
            speed.setValue(model.getCharacterSpeed(index));
            jump.setValue(model.getCharacterJump(index));
            strength.setValue(model.getCharacterStrength(index));
            power.setValue(model.getCharacterPowerLevel(index));
            special.setText(text("card.special") + " " + model.getCharacterPowerName(index));
            description.setText(model.getCharacterPowerDescription(index));
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.report(this, e);
        }
    }
}
