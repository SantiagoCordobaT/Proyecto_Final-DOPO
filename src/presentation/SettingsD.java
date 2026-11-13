package presentation;

import static presentation.Texts.text;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Dialogo de configuracion: elige el idioma de la interfaz.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class SettingsD extends StreetDialog {

    private final Texts texts = Texts.getInstance();
    private final JList<String> languages;

    /** @param owner Ventana principal. */
    SettingsD(Frame owner) {
        super(owner, text("settings.title"));
        languages = resources.list(texts.getLanguages());
        languages.setSelectedIndex(texts.getLanguage());
        JPanel body = resources.panel(new BorderLayout(0, Resources.GAP / 2));
        body.add(resources.label(text("settings.language"), Resources.TEXT_FONT), BorderLayout.NORTH);
        body.add(new JScrollPane(languages), BorderLayout.CENTER);
        setBody(body);
        addButton(text("settings.save"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                texts.setLanguage(languages.getSelectedIndex());
                close();
            }
        });
    }
}
