package presentacion;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Diálogo donde cada jugador escoge su personaje y se pueden crear personajes
 * nuevos antes de arrancar el partido.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class CharacterSelectD extends DialogoBase {

    private final HeadSoccerGUI ventana;
    private final HeadSoccer modelo;
    private final CharacterCard primero;
    private final CharacterCard segundo;

    CharacterSelectD(HeadSoccerGUI ventana, HeadSoccer modelo) {
        super(ventana, Texts.t("Escoge tu personaje", "Choose your character"));
        this.ventana = ventana;
        this.modelo = modelo;

        primero = new CharacterCard(this, modelo, 1, 0);
        segundo = new CharacterCard(this, modelo, 2, 1);

        JPanel fichas = new JPanel(new BorderLayout(Estilo.MARGEN, 0));
        fichas.setBackground(Estilo.FONDO);
        fichas.add(primero, BorderLayout.WEST);
        fichas.add(Estilo.etiqueta("VS", Estilo.FUENTE_TITULO), BorderLayout.CENTER);
        fichas.add(segundo, BorderLayout.EAST);
        ponerCentro(fichas);

        agregarBoton(Estilo.boton(Texts.t("Jugar", "Play"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jugar();
            }
        }));
        agregarBoton(Estilo.boton(Texts.t("Crear personaje", "Create character"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                crearPersonaje();
            }
        }));
        agregarBoton(Estilo.boton(Texts.t("Volver", "Back"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alCerrar();
                ventana.abrirModosJuego();
            }
        }));
    }

    private void crearPersonaje() {
        CharacterFormD formulario = new CharacterFormD(ventana, modelo);
        formulario.abrir();
        if (formulario.obtenerIndiceResultado() >= 0) {
            recargar(primero, formulario.obtenerIndiceResultado());
        }
    }

    void editarPersonaje(CharacterCard origen) {
        int indice = origen.obtenerIndiceSeleccionado();
        try {
            CharacterFormD formulario = new CharacterFormD(ventana, modelo, indice);
            formulario.abrir();
            if (formulario.obtenerIndiceResultado() >= 0) {
                recargar(origen, formulario.obtenerIndiceResultado());
            }
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.reportarError(this, e);
        }
    }

    void eliminarPersonaje(CharacterCard origen) {
        int indice = origen.obtenerIndiceSeleccionado();
        try {
            modelo.eliminarPersonaje(indice);
            int nuevo = indice > 0 ? indice - 1 : 0;
            recargar(origen, nuevo);
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.reportarError(this, e);
        }
    }

    private void recargar(CharacterCard origen, int indiceEnOrigen) {
        int indicePrimero = primero.obtenerIndiceSeleccionado();
        int indiceSegundo = segundo.obtenerIndiceSeleccionado();
        if (origen == primero) {
            indicePrimero = indiceEnOrigen;
        } else {
            indiceSegundo = indiceEnOrigen;
        }
        primero.recargar(indicePrimero);
        segundo.recargar(indiceSegundo);
        pack();
    }

    private void jugar() {
        try {
            modelo.seleccionarJugador(1, primero.obtenerIndiceSeleccionado());
            modelo.seleccionarJugador(2, segundo.obtenerIndiceSeleccionado());
            modelo.iniciarPartido();
            alCerrar();
            ventana.mostrarPartido();
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.reportarError(this, e);
        }
    }
}
