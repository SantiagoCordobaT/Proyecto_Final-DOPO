package presentacion;

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
 * Diálogo para escoger la modalidad y el tipo de partido antes de elegir
 * personajes.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class GameModeD extends BaseDialog {

    private final HeadSoccerGUI ventana;
    private final HeadSoccer modelo;
    private final JList<String> modos;
    private final JList<String> tiposPartido;
    private final JButton seleccionar;

    GameModeD(HeadSoccerGUI ventana, HeadSoccer modelo) {
        super(ventana, Texts.t("Modo de juego", "Game mode"));
        this.ventana = ventana;
        this.modelo = modelo;

        modos = Style.lista(modelo.obtenerNombresModos());
        tiposPartido = Style.lista(modelo.obtenerNombresTiposPartido());

        JPanel listas = new JPanel(new GridLayout(1, 2, Style.MARGEN, 0));
        listas.setBackground(Style.FONDO);
        listas.add(columna(Texts.t("Modalidad", "Mode"), modos));
        listas.add(columna(Texts.t("Tipo de partido", "Match type"), tiposPartido));
        ponerCentro(listas);

        seleccionar = Style.boton(Texts.t("Seleccionar", "Select"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seleccionar();
            }
        });
        agregarBoton(seleccionar);
        agregarBoton(Style.boton(Texts.t("Volver", "Back"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alCerrar();
            }
        }));

        ListSelectionListener cambio = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                seleccionar.setEnabled(!modos.isSelectionEmpty() && !tiposPartido.isSelectionEmpty());
            }
        };
        modos.addListSelectionListener(cambio);
        tiposPartido.addListSelectionListener(cambio);
    }

    private JPanel columna(String titulo, JList<String> lista) {
        JPanel columna = new JPanel(new BorderLayout(0, 6));
        columna.setBackground(Style.FONDO);
        columna.add(Style.etiqueta(titulo, Style.FUENTE_NORMAL), BorderLayout.NORTH);
        columna.add(new JScrollPane(lista), BorderLayout.CENTER);
        return columna;
    }

    private void seleccionar() {
        try {
            modelo.seleccionarModo(modos.getSelectedIndex());
            modelo.seleccionarTipoPartido(tiposPartido.getSelectedIndex());
            alCerrar();
            ventana.abrirSeleccionPersonajes();
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.reportarError(this, e);
        }
    }
}
