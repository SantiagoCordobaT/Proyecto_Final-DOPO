package presentacion;

import static presentacion.Texts.texto;

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

    private final Texts textos = Texts.obtenerInstancia();
    private final JList<String> idiomas;

    /** @param dueno Ventana principal. */
    SettingsD(Frame dueno) {
        super(dueno, texto("settings.title"));
        idiomas = recursos.crearLista(textos.obtenerIdiomas());
        idiomas.setSelectedIndex(textos.obtenerIdioma());
        JPanel cuerpo = recursos.crearPanel(new BorderLayout(0, Resources.ESPACIO / 2));
        cuerpo.add(recursos.crearEtiqueta(texto("settings.language"), Resources.FUENTE_TEXTO), BorderLayout.NORTH);
        cuerpo.add(new JScrollPane(idiomas), BorderLayout.CENTER);
        establecerCuerpo(cuerpo);
        agregarBoton(texto("settings.save"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textos.establecerIdioma(idiomas.getSelectedIndex());
                cerrar();
            }
        });
    }
}
