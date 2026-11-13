package presentation;

import static presentation.Texts.text;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/**
 * Pausa del partido (ventana modal): reanudar, abandonar o ver los controles. No se
 * cierra con la X: hay que elegir entre reanudar y abandonar.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class PauseD extends JDialog {

    private static final int MARGIN = 15;
    private static final Font TITLE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 36);

    /**
     * @param owner Ventana sobre la que se abre la pausa.
     * @param panel Partido pausado, que reanuda o abandona.
     */
    PauseD(Frame owner, GamePanel panel) {
        super(owner, text("pause.title"), true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        JLabel title = new JLabel(text("pause.title"), SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        JButton resume = new JButton(text("pause.resume"));
        JButton giveUp = new JButton(text("pause.giveUp"));
        JButton help = new JButton(text("pause.help"));
        resume.addActionListener(e -> {
            dispose();
            panel.resumeGame();
        });
        giveUp.addActionListener(e -> {
            dispose();
            panel.giveUp();
        });
        help.addActionListener(e -> JOptionPane.showMessageDialog(this, text("help.controls"),
                text("pause.help"), JOptionPane.INFORMATION_MESSAGE));

        JPanel buttons = new JPanel(new GridLayout(3, 1, MARGIN, MARGIN));
        buttons.add(resume);
        buttons.add(giveUp);
        buttons.add(help);
        JPanel content = new JPanel(new BorderLayout(MARGIN, MARGIN));
        content.setBorder(BorderFactory.createEmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN));
        content.add(title, BorderLayout.NORTH);
        content.add(buttons, BorderLayout.CENTER);
        setContentPane(content);
        pack();
        setLocationRelativeTo(owner);
    }
}
