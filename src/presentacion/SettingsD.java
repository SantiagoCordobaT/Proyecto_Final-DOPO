package presentacion;

import static presentacion.Texts.text;

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

    private final Texts textos = Texts.getInstance();
    private final JList<String> idiomas;

    /** @param dueno Ventana principal. */
    SettingsD(Frame dueno) {
        super(dueno, text("settings.title"));
        idiomas = recursos.list(textos.getLanguages());
        idiomas.setSelectedIndex(textos.getLanguage());
        JPanel cuerpo = recursos.panel(new BorderLayout(0, Resources.ESPACIO / 2));
        cuerpo.add(recursos.label(text("settings.language"), Resources.FUENTE_TEXTO), BorderLayout.NORTH);
        cuerpo.add(new JScrollPane(idiomas), BorderLayout.CENTER);
        setBody(cuerpo);
        addButton(text("settings.save"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textos.setLanguage(idiomas.getSelectedIndex());
                close();
            }
        });
    }
}
