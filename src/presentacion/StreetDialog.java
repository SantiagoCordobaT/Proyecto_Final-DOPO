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
 * {@link #cerrar()},
 * que cada dialogo puede extender para dejar el juego en un estado seguro.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
abstract class StreetDialog extends JDialog {

    protected final Resources recursos = Resources.obtenerInstancia();
    private final JPanel contenido;
    private final JPanel botones;

    /**
     * @param dueno  Ventana principal.
     * @param titulo Titulo de la ventana y del encabezado.
     */
    StreetDialog(Frame dueno, String titulo) {
        super(dueno, titulo, true);
        contenido = recursos.crearPanel(new BorderLayout(Resources.ESPACIO, Resources.ESPACIO));
        botones = recursos.crearPanel(new FlowLayout(FlowLayout.CENTER, Resources.ESPACIO, 0));
        contenido.setBorder(BorderFactory.createEmptyBorder(Resources.ESPACIO, Resources.ESPACIO * 2, Resources.ESPACIO,
                Resources.ESPACIO * 2));
        contenido.add(recursos.crearEtiqueta(titulo.toUpperCase(), Resources.FUENTE_ENCABEZADO), BorderLayout.NORTH);
        contenido.add(botones, BorderLayout.SOUTH);
        setContentPane(contenido);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrar();
            }
        });
    }

    /** Ubica el cuerpo del dialogo en el centro. */
    protected final void establecerCuerpo(JComponent cuerpo) {
        contenido.add(cuerpo, BorderLayout.CENTER);
    }

    /**
     * Agrega un boton a la barra inferior.
     *
     * @return El boton creado, por si el dialogo necesita habilitarlo o
     *         deshabilitarlo.
     */
    protected final JButton agregarBoton(String texto, ActionListener escuchador) {
        JButton boton = recursos.crearBoton(texto, escuchador);
        botones.add(boton);
        return boton;
    }

    /**
     * Ajusta el tamano al contenido, centra el dialogo sobre su ventana y lo
     * muestra.
     */
    final void abrir() {
        pack();
        setLocationRelativeTo(getOwner());
        setVisible(true);
    }

    /** Cierra el dialogo liberando sus recursos. */
    protected void cerrar() {
        dispose();
    }
}
