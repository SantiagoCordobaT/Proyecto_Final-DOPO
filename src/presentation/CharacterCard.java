package presentation;

import static presentation.Texts.text;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Tarjeta de un jugador en la seleccion de personajes: elige el personaje y muestra
 * su pais, estadisticas y poder. Cada jugador tiene su propia tarjeta.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class CharacterCard extends JPanel {

    private static final int MARGIN = 10;
    private static final Font TITLE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 22);

    private final HeadSoccer game;
    private final String titleKey;
    private final JLabel title = new JLabel("", SwingConstants.CENTER);
    private final JComboBox<String> characters;
    private final JLabel stats = new JLabel();

    /**
     * @param game Fachada del juego: da el catalogo de personajes.
     * @param titleKey Clave en {@link Texts} del titulo de la tarjeta (el jugador).
     */
    CharacterCard(HeadSoccer game, String titleKey) {
        super(new BorderLayout(MARGIN, MARGIN));
        this.game = game;
        this.titleKey = titleKey;
        characters = new JComboBox<String>(game.getCharacterNames());
        title.setFont(TITLE_FONT);
        setBorder(BorderFactory.createEmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN));
        JPanel header = new JPanel(new GridLayout(2, 1, MARGIN, MARGIN));
        header.add(title);
        header.add(characters);
        add(header, BorderLayout.NORTH);
        add(stats, BorderLayout.CENTER);

        characters.addActionListener(e -> refresh());
    }

    /** @return Indice en el catalogo del personaje elegido. */
    int getSelectedIndex() {
        return characters.getSelectedIndex();
    }

    /** Pone el titulo en el idioma elegido y muestra los datos del personaje elegido. */
    void refresh() {
        title.setText(text(titleKey));
        int index = characters.getSelectedIndex();
        try {
            stats.setText("<html>"
                    + line("card.country", game.getCharacterCountry(index))
                    + line("card.speed", game.getCharacterSpeed(index))
                    + line("card.jump", game.getCharacterJump(index))
                    + line("card.strength", game.getCharacterStrength(index))
                    + line("card.power", game.getCharacterPowerLevel(index))
                    + line("card.special", game.getCharacterPowerName(index))
                    + "<i>" + game.getCharacterPowerDescription(index) + "</i></html>");
        } catch (POOBSoccerException e) {
            Screen.report(this, e);
        }
    }

    private static String line(String key, Object value) {
        return text(key) + " <b>" + value + "</b><br>";
    }
}
