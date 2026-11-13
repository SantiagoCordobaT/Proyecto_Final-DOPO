package presentation;

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
 * Dialogo modal con el armazon comun del juego: titulo al norte, cuerpo al centro
 * y barra de botones al sur. Cerrar la ventana pasa siempre por {@link #close()},
 * que cada dialogo puede extender para dejar el juego en un estado seguro.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
abstract class StreetDialog extends JDialog {

    protected final Resources resources = Resources.getInstance();
    private final JPanel content;
    private final JPanel buttons;

    /**
     * @param owner Ventana principal.
     * @param title Titulo de la ventana y del encabezado.
     */
    StreetDialog(Frame owner, String title) {
        super(owner, title, true);
        content = resources.panel(new BorderLayout(Resources.GAP, Resources.GAP));
        buttons = resources.panel(new FlowLayout(FlowLayout.CENTER, Resources.GAP, 0));
        content.setBorder(BorderFactory.createEmptyBorder(Resources.GAP, Resources.GAP * 2, Resources.GAP,
                Resources.GAP * 2));
        content.add(resources.label(title.toUpperCase(), Resources.HEADING_FONT), BorderLayout.NORTH);
        content.add(buttons, BorderLayout.SOUTH);
        setContentPane(content);
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
    protected final void setBody(JComponent body) {
        content.add(body, BorderLayout.CENTER);
    }

    /**
     * Agrega un boton a la barra inferior.
     *
     * @return El boton creado, por si el dialogo necesita habilitarlo o deshabilitarlo.
     */
    protected final JButton addButton(String text, ActionListener listener) {
        JButton button = resources.button(text, listener);
        buttons.add(button);
        return button;
    }

    /** Ajusta el tamano al contenido, centra el dialogo sobre su ventana y lo muestra. */
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
