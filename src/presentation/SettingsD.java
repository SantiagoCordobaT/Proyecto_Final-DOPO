package presentation;

import static presentation.Texts.text;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Configuracion (ventana modal): el idioma de la interfaz. Guardar cambia el
 * idioma de {@link Texts}; cerrar la ventana lo deja como estaba.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class SettingsD extends JDialog {

    private static final int MARGIN = 15;

    /** @param owner Ventana sobre la que se abre la configuracion. */
    SettingsD(Frame owner) {
        super(owner, text("settings.title"), true);
        Texts texts = Texts.getInstance();
        JComboBox<String> languages = new JComboBox<String>(texts.getLanguages());
        languages.setSelectedIndex(texts.getLanguage());
        JButton save = new JButton(text("settings.save"));
        save.addActionListener(e -> {
            texts.setLanguage(languages.getSelectedIndex());
            dispose();
        });

        JPanel language = new JPanel(new FlowLayout());
        language.add(new JLabel(text("settings.language")));
        language.add(languages);
        JPanel content = new JPanel(new BorderLayout(MARGIN, MARGIN));
        content.setBorder(BorderFactory.createEmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN));
        content.add(language, BorderLayout.CENTER);
        content.add(save, BorderLayout.SOUTH);
        setContentPane(content);
        pack();
        setLocationRelativeTo(owner);
    }
}
