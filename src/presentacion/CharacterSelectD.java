package presentacion;

import static presentacion.Texts.texto;

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
        super(ventana, texto("select.title"));
        this.ventana = ventana;
        this.modelo = modelo;
        primero = new CharacterCard(modelo, 1, 0);
        segundo = new CharacterCard(modelo, 2, INDICE_RIVAL);
        JPanel fichas = recursos.crearPanel(new BorderLayout(Resources.ESPACIO, 0));
        fichas.add(primero, BorderLayout.WEST);
        fichas.add(recursos.crearEtiqueta(CONTRA, Resources.FUENTE_TITULO), BorderLayout.CENTER);
        fichas.add(segundo, BorderLayout.EAST);
        establecerCuerpo(fichas);
        agregarBoton(texto("select.play"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jugar();
            }
        });
        agregarBoton(texto("common.back"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cerrar();
                ventana.abrirModosJuego();
            }
        });
    }

    private void jugar() {
        try {
            modelo.seleccionarJugador(1, primero.obtenerIndiceSeleccionado());
            modelo.seleccionarJugador(2, segundo.obtenerIndiceSeleccionado());
            modelo.iniciarPartido();
            cerrar();
            ventana.mostrarPartido();
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.reportarError(this, e);
        }
    }
}
