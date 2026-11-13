package presentation;

import static presentation.Texts.text;

import java.awt.Component;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Pantalla de la ventana principal. Cada pantalla solo traduce eventos de Swing a
 * llamadas de la fachada {@link HeadSoccer} (Controller) y pide a la ventana la
 * siguiente pantalla. Su nombre (el de su clase) la identifica en el CardLayout.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
abstract class Screen extends JPanel {

    /** Ventana que cambia de pantalla. */
    protected final HeadSoccerGUI gui;
    /** Fachada del juego. */
    protected final HeadSoccer game;

    /**
     * @param gui Ventana que contiene la pantalla.
     * @param game Fachada del juego.
     */
    Screen(HeadSoccerGUI gui, HeadSoccer game) {
        this.gui = gui;
        this.game = game;
        setName(getClass().getSimpleName());
    }

    /**
     * Muestra la pantalla (Template Method): la actualiza con {@link #onShow()} y
     * toma el foco del teclado.
     */
    final void enter() {
        onShow();
        requestFocusInWindow();
    }

    /** Pone los textos en el idioma elegido y refresca los datos de la pantalla. */
    protected abstract void onShow();

    /**
     * Muestra al usuario el error de una regla del juego.
     *
     * @param parent Componente sobre el que se centra el mensaje (null: el centro del monitor).
     * @param e Error del dominio.
     */
    static void report(Component parent, POOBSoccerException e) {
        JOptionPane.showMessageDialog(parent, e.getMessage(), text("error.title"), JOptionPane.ERROR_MESSAGE);
    }
}
