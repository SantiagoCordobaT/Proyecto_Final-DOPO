package presentacion;

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

import dominio.FichaPersonaje;
import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Formulario para crear un personaje nuevo o editar uno existente del catálogo.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class CharacterFormD extends DialogoBase {

    private static final int NUEVO = -1;

    private final HeadSoccer modelo;
    private final int indiceEdicion;
    private final JTextField nombre = new JTextField(14);
    private final JTextField pais = new JTextField(14);
    private final JSpinner velocidad = selectorStat();
    private final JSpinner salto = selectorStat();
    private final JSpinner fuerza = selectorStat();
    private final JSpinner nivelPoder = selectorStat();
    private final JComboBox<String> poderes;

    private int indiceResultado = NUEVO;

    CharacterFormD(Frame dueno, HeadSoccer modelo) {
        this(dueno, modelo, Texts.t("Crear personaje", "Create character"), NUEVO);
    }

    CharacterFormD(Frame dueno, HeadSoccer modelo, int indiceEdicion) throws POOBSoccerException {
        this(dueno, modelo, Texts.t("Editar personaje", "Edit character"), indiceEdicion);
        FichaPersonaje ficha = modelo.obtenerFichaPersonaje(indiceEdicion);
        nombre.setText(ficha.obtenerNombre());
        pais.setText(ficha.obtenerPais());
        velocidad.setValue(ficha.obtenerVelocidad());
        salto.setValue(ficha.obtenerSalto());
        fuerza.setValue(ficha.obtenerFuerza());
        nivelPoder.setValue(ficha.obtenerNivelPoder());
        poderes.setSelectedItem(ficha.obtenerNombrePoder());
    }

    private CharacterFormD(Frame dueno, HeadSoccer modelo, String titulo, int indiceEdicion) {
        super(dueno, titulo);
        this.modelo = modelo;
        this.indiceEdicion = indiceEdicion;
        this.poderes = new JComboBox<>(modelo.obtenerNombresPoderes());

        JPanel formulario = new JPanel(new GridLayout(0, 2, Estilo.MARGEN, 6));
        formulario.setBackground(Estilo.FONDO);
        agregarCampo(formulario, Texts.t("Nombre", "Name"), nombre);
        agregarCampo(formulario, Texts.t("País", "Country"), pais);
        agregarCampo(formulario, Texts.t("Velocidad", "Speed"), velocidad);
        agregarCampo(formulario, Texts.t("Salto", "Jump"), salto);
        agregarCampo(formulario, Texts.t("Fuerza", "Strength"), fuerza);
        agregarCampo(formulario, Texts.t("Poder Único", "Unique Power"), nivelPoder);
        agregarCampo(formulario, Texts.t("Poder", "Power"), poderes);
        ponerCentro(formulario);

        agregarBoton(Estilo.boton(Texts.t("Guardar", "Save"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardar();
            }
        }));
        agregarBoton(Estilo.boton(Texts.t("Volver", "Back"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alCerrar();
            }
        }));
    }

    int obtenerIndiceResultado() {
        return indiceResultado;
    }

    private void agregarCampo(JPanel panel, String etiqueta, JComponent campo) {
        panel.add(Estilo.etiqueta(etiqueta, Estilo.FUENTE_NORMAL));
        panel.add(campo);
    }

    private static JSpinner selectorStat() {
        return new JSpinner(new SpinnerNumberModel(3, 1, 5, 1));
    }

    private void guardar() {
        try {
            int vel = (Integer) velocidad.getValue();
            int sal = (Integer) salto.getValue();
            int fue = (Integer) fuerza.getValue();
            int pod = (Integer) nivelPoder.getValue();
            if (indiceEdicion == NUEVO) {
                indiceResultado = modelo.crearPersonaje(nombre.getText(), pais.getText(), vel, sal, fue, pod,
                        poderes.getSelectedIndex());
            } else {
                modelo.editarPersonaje(indiceEdicion, nombre.getText(), pais.getText(), vel, sal, fue, pod,
                        poderes.getSelectedIndex());
                indiceResultado = indiceEdicion;
            }
            alCerrar();
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.reportarError(this, e);
        }
    }
}
