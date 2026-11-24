package presentacion;

import static presentacion.Texts.texto;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Diálogo para escoger los personajes de ambos jugadores y gestionar el
 * catálogo antes de iniciar el partido.
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
    private CharacterCard fichaActiva;

    CharacterSelectD(HeadSoccerGUI ventana, HeadSoccer modelo) {
        super(ventana, texto("select.title"));
        this.ventana = ventana;
        this.modelo = modelo;
        primero = new CharacterCard(modelo, 1, 0);
        segundo = new CharacterCard(modelo, 2, INDICE_RIVAL);
        fichaActiva = primero;
        primero.alSeleccionar(new Runnable() {
            @Override
            public void run() {
                fichaActiva = primero;
            }
        });
        segundo.alSeleccionar(new Runnable() {
            @Override
            public void run() {
                fichaActiva = segundo;
            }
        });

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
        agregarBoton(texto("select.create"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                crearPersonaje();
            }
        });
        agregarBoton(texto("select.edit"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editarPersonaje();
            }
        });
        agregarBoton(texto("select.delete"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarPersonaje();
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

    private void crearPersonaje() {
        CharacterFormD formulario = CharacterFormD.paraCrear(ventana, modelo);
        formulario.abrir();
        if (formulario.obtenerIndiceResultado() >= 0) {
            recargarFichas(formulario.obtenerIndiceResultado());
        }
    }

    private void editarPersonaje() {
        int indice = fichaActiva.obtenerIndiceSeleccionado();
        try {
            CharacterFormD formulario = CharacterFormD.paraEditar(ventana, modelo, indice);
            formulario.abrir();
            if (formulario.obtenerIndiceResultado() >= 0) {
                recargarFichas(formulario.obtenerIndiceResultado());
            }
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.reportarError(this, e);
        }
    }

    private void eliminarPersonaje() {
        int indice = fichaActiva.obtenerIndiceSeleccionado();
        try {
            modelo.eliminarPersonaje(indice);
            int nuevoIndice = indice - 1;
            if (nuevoIndice < 0) {
                nuevoIndice = 0;
            }
            recargarFichas(nuevoIndice);
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.reportarError(this, e);
        }
    }

    private void recargarFichas(int indiceEnActiva) {
        int indicePrimero = primero.obtenerIndiceSeleccionado();
        int indiceSegundo = segundo.obtenerIndiceSeleccionado();
        if (fichaActiva == primero) {
            indicePrimero = indiceEnActiva;
        } else {
            indiceSegundo = indiceEnActiva;
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
            cerrar();
            ventana.mostrarPartido();
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.reportarError(this, e);
        }
    }
}
