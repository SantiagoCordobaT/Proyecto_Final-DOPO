package presentacion;

import static presentacion.Texts.texto;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Dialogo sencillo para crear o editar un personaje personalizado; todas las
 * validaciones de reglas las realiza la fachada {@link HeadSoccer}.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class CharacterFormD extends StreetDialog {

    private static final int ESTADISTICA_MIN = 1;
    private static final int ESTADISTICA_MAX = 5;
    private static final int ESTADISTICA_INICIAL = 3;
    private static final int COLUMNAS_TEXTO = 14;
    private static final int NUEVO = -1;

    private final HeadSoccer modelo;
    private final int indiceEdicion;
    private final JTextField nombre = new JTextField(COLUMNAS_TEXTO);
    private final JTextField pais = new JTextField(COLUMNAS_TEXTO);
    private final JSpinner velocidad = crearSelector();
    private final JSpinner salto = crearSelector();
    private final JSpinner fuerza = crearSelector();
    private final JSpinner nivelPoder = crearSelector();
    private final JComboBox<String> poderes;

    private int indiceResultado = NUEVO;

    private CharacterFormD(Frame dueno, HeadSoccer modelo, String titulo, int indiceEdicion) {
        super(dueno, titulo);
        this.modelo = modelo;
        this.indiceEdicion = indiceEdicion;
        this.poderes = new JComboBox<String>(modelo.obtenerNombresPoderes());

        JPanel formulario = recursos.crearPanel(new GridLayout(0, 2, Resources.ESPACIO, Resources.ESPACIO / 2));
        agregarCampo(formulario, "editor.name", nombre);
        agregarCampo(formulario, "editor.country", pais);
        agregarCampo(formulario, "card.speed", velocidad);
        agregarCampo(formulario, "card.jump", salto);
        agregarCampo(formulario, "card.strength", fuerza);
        agregarCampo(formulario, "card.power", nivelPoder);
        agregarCampo(formulario, "editor.powerType", poderes);
        establecerCuerpo(formulario);

        agregarBoton(texto("settings.save"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardar();
            }
        });
        agregarBoton(texto("common.back"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cerrar();
            }
        });
    }

    /** Abre el formulario vacio para crear un personaje nuevo. */
    static CharacterFormD paraCrear(Frame dueno, HeadSoccer modelo) {
        return new CharacterFormD(dueno, modelo, texto("editor.createTitle"), NUEVO);
    }

    /** Abre el formulario precargado para editar un personaje existente. */
    static CharacterFormD paraEditar(Frame dueno, HeadSoccer modelo, int indice) throws POOBSoccerException {
        CharacterFormD dialogo = new CharacterFormD(dueno, modelo, texto("editor.editTitle"), indice);
        dialogo.nombre.setText(modelo.obtenerNombresPersonajes()[indice]);
        dialogo.pais.setText(modelo.obtenerPaisPersonaje(indice));
        dialogo.velocidad.setValue(modelo.obtenerVelocidadPersonaje(indice));
        dialogo.salto.setValue(modelo.obtenerSaltoPersonaje(indice));
        dialogo.fuerza.setValue(modelo.obtenerFuerzaPersonaje(indice));
        dialogo.nivelPoder.setValue(modelo.obtenerNivelPoderPersonaje(indice));
        dialogo.poderes.setSelectedItem(modelo.obtenerNombrePoderPersonaje(indice));
        return dialogo;
    }

    /** @return Indice creado/editado, o -1 si el usuario cancelo. */
    int obtenerIndiceResultado() {
        return indiceResultado;
    }

    private void agregarCampo(JPanel formulario, String clave, JComponent campo) {
        formulario.add(recursos.crearEtiqueta(texto(clave), Resources.FUENTE_TEXTO));
        formulario.add(campo);
    }

    private static JSpinner crearSelector() {
        return new JSpinner(new SpinnerNumberModel(ESTADISTICA_INICIAL, ESTADISTICA_MIN, ESTADISTICA_MAX, 1));
    }

    private static int valor(JSpinner selector) {
        return ((Integer) selector.getValue()).intValue();
    }

    private void guardar() {
        try {
            if (indiceEdicion == NUEVO) {
                indiceResultado = modelo.crearPersonaje(nombre.getText(), pais.getText(), valor(velocidad),
                        valor(salto), valor(fuerza), valor(nivelPoder), poderes.getSelectedIndex());
            } else {
                modelo.editarPersonaje(indiceEdicion, nombre.getText(), pais.getText(), valor(velocidad),
                        valor(salto), valor(fuerza), valor(nivelPoder), poderes.getSelectedIndex());
                indiceResultado = indiceEdicion;
            }
            cerrar();
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.reportarError(this, e);
        }
    }
}
