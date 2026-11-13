package presentation;

import static presentation.Texts.text;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import dominio.HeadSoccer;

/**
 * Paso de la configuracion de un partido: un titulo arriba, el contenido de la
 * subclase al centro y los botones "Seleccionar" y "Volver" abajo. Las subclases
 * solo definen su contenido y que hace cada boton (Template Method).
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
abstract class StepScreen extends Screen {

    private static final int MARGIN = 20;
    private static final Font TITLE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 32);

    private final String titleKey;
    private final JLabel title = new JLabel("", SwingConstants.CENTER);
    private final JButton select = new JButton();
    private final JButton back = new JButton();

    /**
     * @param gui Ventana que contiene la pantalla.
     * @param game Fachada del juego.
     * @param titleKey Clave en {@link Texts} del titulo de la pantalla.
     */
    StepScreen(HeadSoccerGUI gui, HeadSoccer game, String titleKey) {
        super(gui, game);
        this.titleKey = titleKey;
        setLayout(new BorderLayout(MARGIN, MARGIN));
        setBorder(BorderFactory.createEmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN));
        title.setFont(TITLE_FONT);
        JPanel buttons = new JPanel();
        buttons.add(select);
        buttons.add(back);
        add(title, BorderLayout.NORTH);
        add(buttons, BorderLayout.SOUTH);

        select.addActionListener(e -> select());
        back.addActionListener(e -> back());
    }

    @Override
    protected final void onShow() {
        title.setText(text(titleKey));
        select.setText(text("common.select"));
        back.setText(text("common.back"));
        refresh();
    }

    /** Pone el contenido en el idioma elegido y con datos actuales. */
    protected abstract void refresh();

    /** Confirma la eleccion del paso y avanza al siguiente. */
    protected abstract void select();

    /** Vuelve a la pantalla anterior. */
    protected abstract void back();
}
