package presentacion;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Diálogo de configuración para escoger el idioma de la interfaz.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class SettingsD extends DialogoBase {

    private final JList<String> idiomas;

    SettingsD(Frame dueno) {
        super(dueno, Texts.t("Configuración", "Settings"));

        idiomas = Estilo.lista(Texts.obtenerIdiomas());
        idiomas.setSelectedIndex(Texts.obtenerIdioma());

        JPanel cuerpo = new JPanel(new BorderLayout(0, 6));
        cuerpo.setBackground(Estilo.FONDO);
        cuerpo.add(Estilo.etiqueta(Texts.t("Idioma", "Language"), Estilo.FUENTE_NORMAL), BorderLayout.NORTH);
        cuerpo.add(new JScrollPane(idiomas), BorderLayout.CENTER);
        ponerCentro(cuerpo);

        agregarBoton(Estilo.boton(Texts.t("Guardar", "Save"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Texts.establecerIdioma(idiomas.getSelectedIndex());
                alCerrar();
            }
        }));
    }
}
