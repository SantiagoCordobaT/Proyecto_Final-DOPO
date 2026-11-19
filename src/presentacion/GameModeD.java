package presentacion;

import static presentacion.Texts.texto;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Dialogo que elige la modalidad y el tipo de partido. Solo reenvia la
 * seleccion
 * a la fachada; las combinaciones invalidas las rechaza el dominio.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class GameModeD extends StreetDialog {

    private final HeadSoccerGUI ventana;
    private final HeadSoccer modelo;
    private final JList<String> modos;
    private final JList<String> tiposPartido;
    private final JButton seleccionar;

    /**
     * @param ventana Ventana principal.
     * @param modelo  Fachada del dominio.
     */
    GameModeD(HeadSoccerGUI ventana, HeadSoccer modelo) {
        super(ventana, texto("setup.title"));
        this.ventana = ventana;
        this.modelo = modelo;
        modos = recursos.crearLista(modelo.obtenerNombresModos());
        tiposPartido = recursos.crearLista(modelo.obtenerNombresTiposPartido());
        JPanel listas = recursos.crearPanel(new GridLayout(1, 2, Resources.ESPACIO, 0));
        listas.add(columna("setup.mode", modos));
        listas.add(columna("setup.type", tiposPartido));
        establecerCuerpo(listas);
        seleccionar = agregarBoton(texto("common.select"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seleccionar();
            }
        });
        agregarBoton(texto("common.back"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cerrar();
            }
        });
        ListSelectionListener completo = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                seleccionar.setEnabled(!modos.isSelectionEmpty() && !tiposPartido.isSelectionEmpty());
            }
        };
        modos.addListSelectionListener(completo);
        tiposPartido.addListSelectionListener(completo);
    }

    private JPanel columna(String claveTitulo, JList<String> lista) {
        JPanel columna = recursos.crearPanel(new BorderLayout(0, Resources.ESPACIO / 2));
        columna.add(recursos.crearEtiqueta(texto(claveTitulo), Resources.FUENTE_TEXTO), BorderLayout.NORTH);
        columna.add(new JScrollPane(lista), BorderLayout.CENTER);
        return columna;
    }

    private void seleccionar() {
        try {
            modelo.seleccionarModo(modos.getSelectedIndex());
            modelo.seleccionarTipoPartido(tiposPartido.getSelectedIndex());
            cerrar();
            ventana.abrirSeleccionPersonajes();
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.reportarError(this, e);
        }
    }
}
