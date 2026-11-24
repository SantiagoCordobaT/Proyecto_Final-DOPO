package presentacion;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * Ventana modal base que comparten los diálogos del juego para mantener el
 * mismo encabezado, márgenes y barra inferior de botones.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
abstract class BaseDialog extends JDialog {

    private final JPanel contenido;
    private final JPanel botones;

    BaseDialog(Frame dueno, String titulo) {
        super(dueno, titulo, true);
        contenido = new JPanel(new BorderLayout(Style.MARGEN, Style.MARGEN));
        contenido.setBackground(Style.FONDO);
        contenido.setBorder(BorderFactory.createEmptyBorder(Style.MARGEN, Style.MARGEN * 2,
                Style.MARGEN, Style.MARGEN * 2));
        contenido.add(Style.etiqueta(titulo, Style.FUENTE_SUBTITULO), BorderLayout.NORTH);

        botones = new JPanel(new FlowLayout(FlowLayout.CENTER, Style.MARGEN, 0));
        botones.setBackground(Style.FONDO);
        contenido.add(botones, BorderLayout.SOUTH);

        setContentPane(contenido);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                alCerrar();
            }
        });
    }

    protected void ponerCentro(JComponent componente) {
        contenido.add(componente, BorderLayout.CENTER);
    }

    protected void agregarBoton(JButton boton) {
        botones.add(boton);
    }

    protected void alCerrar() {
        dispose();
    }

    void abrir() {
        pack();
        setLocationRelativeTo(getOwner());
        setVisible(true);
    }
}
