package presentacion;

import static presentacion.Texts.text;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Dialogo que enfrenta las fichas de los dos jugadores y, al aceptar, inicia el
 * partido.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class CharacterSelectD extends StreetDialog {

    private static final String CONTRA = "VS";
    private static final int INDICE_RIVAL = 1;

    private final HeadSoccerGUI ventana;
    private final HeadSoccer modelo;
    private final CharacterCard primero;
    private final CharacterCard segundo;

    /**
     * @param ventana Ventana principal.
     * @param modelo  Fachada del dominio.
     */
    CharacterSelectD(HeadSoccerGUI ventana, HeadSoccer modelo) {
        super(ventana, text("select.title"));
        this.ventana = ventana;
        this.modelo = modelo;
        primero = new CharacterCard(modelo, 1, 0);
        segundo = new CharacterCard(modelo, 2, INDICE_RIVAL);
        JPanel fichas = recursos.panel(new BorderLayout(Resources.ESPACIO, 0));
        fichas.add(primero, BorderLayout.WEST);
        fichas.add(recursos.label(CONTRA, Resources.FUENTE_TITULO), BorderLayout.CENTER);
        fichas.add(segundo, BorderLayout.EAST);
        setBody(fichas);
        addButton(text("select.play"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                play();
            }
        });
        addButton(text("common.back"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
                ventana.openGameModes();
            }
        });
    }

    private void play() {
        try {
            modelo.selectPlayer(1, primero.getSelectedIndex());
            modelo.selectPlayer(2, segundo.getSelectedIndex());
            modelo.startMatch();
            close();
            ventana.showMatch();
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.report(this, e);
        }
    }
}
