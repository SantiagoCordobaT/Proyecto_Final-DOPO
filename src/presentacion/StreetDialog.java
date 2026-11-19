package presentacion;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * Dialogo modal con el armazon comun del juego: titulo al norte, cuerpo al
 * centro
 * y barra de botones al sur. Cerrar la ventana pasa siempre por
 * {@link #close()},
 * que cada dialogo puede extender para dejar el juego en un estado seguro.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
abstract class StreetDialog extends JDialog {

    protected final Resources recursos = Resources.getInstance();
    private final JPanel contenido;
    private final JPanel botones;

    /**
     * @param dueno  Ventana principal.
     * @param titulo Titulo de la ventana y del encabezado.
     */
    StreetDialog(Frame dueno, String titulo) {
        super(dueno, titulo, true);
        contenido = recursos.panel(new BorderLayout(Resources.ESPACIO, Resources.ESPACIO));
        botones = recursos.panel(new FlowLayout(FlowLayout.CENTER, Resources.ESPACIO, 0));
        contenido.setBorder(BorderFactory.createEmptyBorder(Resources.ESPACIO, Resources.ESPACIO * 2, Resources.ESPACIO,
                Resources.ESPACIO * 2));
        contenido.add(recursos.label(titulo.toUpperCase(), Resources.FUENTE_ENCABEZADO), BorderLayout.NORTH);
        contenido.add(botones, BorderLayout.SOUTH);
        setContentPane(contenido);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
    }

    /** Ubica el cuerpo del dialogo en el centro. */
    protected final void setBody(JComponent cuerpo) {
        contenido.add(cuerpo, BorderLayout.CENTER);
    }

    /**
     * Agrega un boton a la barra inferior.
     *
     * @return El boton creado, por si el dialogo necesita habilitarlo o
     *         deshabilitarlo.
     */
    protected final JButton addButton(String texto, ActionListener escuchador) {
        JButton boton = recursos.button(texto, escuchador);
        botones.add(boton);
        return boton;
    }

    /**
     * Ajusta el tamano al contenido, centra el dialogo sobre su ventana y lo
     * muestra.
     */
    final void open() {
        pack();
        setLocationRelativeTo(getOwner());
        setVisible(true);
    }

    /** Cierra el dialogo liberando sus recursos. */
    protected void close() {
        dispose();
    }
}
