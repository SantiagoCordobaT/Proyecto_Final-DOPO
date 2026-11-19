package presentacion;

import static presentacion.Texts.text;

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
        super(ventana, text("setup.title"));
        this.ventana = ventana;
        this.modelo = modelo;
        modos = recursos.list(modelo.getModeNames());
        tiposPartido = recursos.list(modelo.getMatchTypeNames());
        JPanel listas = recursos.panel(new GridLayout(1, 2, Resources.ESPACIO, 0));
        listas.add(column("setup.mode", modos));
        listas.add(column("setup.type", tiposPartido));
        setBody(listas);
        seleccionar = addButton(text("common.select"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                select();
            }
        });
        addButton(text("common.back"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
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

    private JPanel column(String claveTitulo, JList<String> lista) {
        JPanel columna = recursos.panel(new BorderLayout(0, Resources.ESPACIO / 2));
        columna.add(recursos.label(text(claveTitulo), Resources.FUENTE_TEXTO), BorderLayout.NORTH);
        columna.add(new JScrollPane(lista), BorderLayout.CENTER);
        return columna;
    }

    private void select() {
        try {
            modelo.selectMode(modos.getSelectedIndex());
            modelo.selectMatchType(tiposPartido.getSelectedIndex());
            close();
            ventana.openCharacterSelect();
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.report(this, e);
        }
    }
}
