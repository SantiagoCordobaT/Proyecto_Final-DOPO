package presentacion;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import dominio.CharacterInfo;
import dominio.HeadSoccer;
import dominio.MatchSetup;
import dominio.excepciones.POOBSoccerException;

/**
 * Diálogo para seleccionar personajes, ver sus estadísticas y crear,
 * editar o eliminar personajes del catálogo antes de iniciar el partido.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class CharacterSelectDialog extends UI.Modal {

    private static final int MAX_STAT = 5;
    private static final int NUEVO = -1;

    private final HeadSoccerGUI ventana;
    private final HeadSoccer modelo;
    private final int indiceModo;
    private final int indiceTipo;
    private final Card primero;
    private final Card segundo;

    CharacterSelectDialog(HeadSoccerGUI ventana, HeadSoccer modelo, int indiceModo, int indiceTipo) {
        super(ventana, UI.t("Escoge tu personaje", "Choose your character"));
        this.ventana = ventana;
        this.modelo = modelo;
        this.indiceModo = indiceModo;
        this.indiceTipo = indiceTipo;

        primero = new Card(1, 0);
        segundo = new Card(2, 1);

        // selector extra en SOUTH de un panel contenedor
        JPanel fichas = new JPanel(new BorderLayout(UI.MARGEN, 0));
        fichas.setBackground(UI.FONDO);
        fichas.add(primero, BorderLayout.WEST);
        fichas.add(UI.etiqueta("VS", UI.FUENTE_TITULO), BorderLayout.CENTER);
        fichas.add(segundo, BorderLayout.EAST);
        ponerCentro(fichas);

        agregarBoton(UI.boton(UI.t("Jugar", "Play"), this::jugar));
        agregarBoton(UI.boton(UI.t("Crear personaje", "Create character"), () -> abrirFormulario(NUEVO, primero)));
        agregarBoton(UI.boton(UI.t("Volver", "Back"), () -> {
            dispose();
            ventana.abrirModosJuego();
        }));
    }

    // Crear / Editar / Eliminar personajes

    private void abrirFormulario(int indiceEdicion, Card origen) {
        String titulo = indiceEdicion == NUEVO
                ? UI.t("Crear personaje", "Create character")
                : UI.t("Editar personaje", "Edit character");
        UI.Modal d = new UI.Modal(ventana, titulo);

        JTextField nombre = new JTextField(14);
        JTextField pais = new JTextField(14);
        JSpinner velocidad = new JSpinner(new SpinnerNumberModel(3, 1, MAX_STAT, 1));
        JSpinner salto = new JSpinner(new SpinnerNumberModel(3, 1, MAX_STAT, 1));
        JSpinner fuerza = new JSpinner(new SpinnerNumberModel(3, 1, MAX_STAT, 1));
        JSpinner nivelPoder = new JSpinner(new SpinnerNumberModel(3, 1, MAX_STAT, 1));
        // atributo al personaje, JSpinner/JComboBox, precarg en if, agregarCampo() y
        // crear/editarPersonaje
        JComboBox<String> poderes = new JComboBox<>(modelo.obtenerNombresPoderes());

        if (indiceEdicion != NUEVO) {
            try {
                CharacterInfo ficha = modelo.obtenerFichaPersonaje(indiceEdicion);
                nombre.setText(ficha.obtenerNombre());
                pais.setText(ficha.obtenerPais());
                velocidad.setValue(ficha.obtenerVelocidad());
                salto.setValue(ficha.obtenerSalto());
                fuerza.setValue(ficha.obtenerFuerza());
                nivelPoder.setValue(ficha.obtenerNivelPoder());
                poderes.setSelectedItem(ficha.obtenerNombrePoder());
            } catch (POOBSoccerException e) {
                HeadSoccerGUI.reportarError(this, e);
                return;
            }
        }

        JPanel form = new JPanel(new GridLayout(0, 2, UI.MARGEN, 6));
        form.setBackground(UI.FONDO);
        agregarCampo(form, UI.t("Nombre", "Name"), nombre);
        agregarCampo(form, UI.t("País", "Country"), pais);
        agregarCampo(form, UI.t("Velocidad", "Speed"), velocidad);
        agregarCampo(form, UI.t("Salto", "Jump"), salto);
        agregarCampo(form, UI.t("Fuerza", "Strength"), fuerza);
        agregarCampo(form, UI.t("Poder Único", "Unique Power"), nivelPoder);
        agregarCampo(form, UI.t("Poder", "Power"), poderes);
        d.ponerCentro(form);

        d.agregarBoton(UI.boton(UI.t("Guardar", "Save"), () -> {
            try {
                int vel = (Integer) velocidad.getValue();
                int sal = (Integer) salto.getValue();
                int fue = (Integer) fuerza.getValue();
                int pod = (Integer) nivelPoder.getValue();
                int resultado;
                if (indiceEdicion == NUEVO) {
                    resultado = modelo.crearPersonaje(nombre.getText(), pais.getText(), vel, sal, fue, pod,
                            poderes.getSelectedIndex());
                } else {
                    modelo.editarPersonaje(indiceEdicion, nombre.getText(), pais.getText(), vel, sal, fue, pod,
                            poderes.getSelectedIndex());
                    resultado = indiceEdicion;
                }
                d.dispose();
                recargar(origen, resultado);
            } catch (POOBSoccerException e) {
                HeadSoccerGUI.reportarError(d, e);
            }
        }));
        d.agregarBoton(UI.boton(UI.t("Volver", "Back"), d::dispose));
        d.abrir();
    }

    private void eliminarPersonaje(Card origen) {
        int indice = origen.indice();
        try {
            modelo.eliminarPersonaje(indice);
            recargar(origen, indice > 0 ? indice - 1 : 0);
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.reportarError(this, e);
        }
    }

    private void recargar(Card origen, int indiceEnOrigen) {
        int i1 = origen == primero ? indiceEnOrigen : primero.indice();
        int i2 = origen == segundo ? indiceEnOrigen : segundo.indice();
        primero.recargar(i1);
        segundo.recargar(i2);
        pack();
    }

    private void jugar() {
        try {
            // índices nuevos, constructor de MatchSetup
            MatchSetup configuracion = new MatchSetup(indiceModo, indiceTipo, primero.indice(), segundo.indice());
            modelo.iniciarPartido(configuracion);
            dispose();
            ventana.mostrarPartido();
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.reportarError(this, e);
        }
    }

    private static void agregarCampo(JPanel panel, String etiqueta, JComponent campo) {
        panel.add(UI.etiqueta(etiqueta, UI.FUENTE_NORMAL));
        panel.add(campo);
    }

    // Tarjeta individual de cada jugador

    private final class Card extends JPanel {

        private final int numero;
        private final JList<String> lista;
        private final UI.Badge escudo = new UI.Badge();
        private final JLabel pais = UI.etiqueta("", UI.FUENTE_NORMAL);
        private final JPanel estadisticas = new JPanel(new GridLayout(0, 1, 0, 6));
        private final JProgressBar velocidad;
        private final JProgressBar salto;
        private final JProgressBar fuerza;
        private final JProgressBar poder;
        private final JLabel especial = UI.etiqueta("", UI.FUENTE_NORMAL);
        private final JLabel descripcion = UI.etiqueta("", UI.FUENTE_NORMAL);

        Card(int numero, int inicial) {
            super(new BorderLayout(UI.MARGEN, UI.MARGEN));
            this.numero = numero;
            setBackground(UI.TARJETA);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UI.colorJugador(numero), 3),
                    BorderFactory.createEmptyBorder(UI.MARGEN, UI.MARGEN, UI.MARGEN, UI.MARGEN)));

            lista = UI.lista(modelo.obtenerNombresPersonajes());
            estadisticas.setOpaque(false);

            JPanel equipo = new JPanel(new FlowLayout(FlowLayout.CENTER, UI.MARGEN, 0));
            equipo.setOpaque(false);
            equipo.add(escudo);
            equipo.add(pais);
            estadisticas.add(equipo);

            // stat al personaje, agregarBarra() y refrescar() CharacterInfo
            velocidad = agregarBarra(UI.t("Velocidad", "Speed"));
            salto = agregarBarra(UI.t("Salto", "Jump"));
            fuerza = agregarBarra(UI.t("Fuerza", "Strength"));
            poder = agregarBarra(UI.t("Poder Único", "Unique Power"));
            estadisticas.add(especial);
            estadisticas.add(descripcion);

            JPanel acciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
            acciones.setOpaque(false);
            acciones.add(UI.boton(UI.t("Editar", "Edit"), () -> abrirFormulario(indice(), this)));
            acciones.add(UI.boton(UI.t("Eliminar", "Delete"), () -> eliminarPersonaje(this)));

            add(UI.etiqueta(UI.t("Jugador", "Player") + " " + numero, UI.FUENTE_SUBTITULO), BorderLayout.NORTH);
            add(new JScrollPane(lista), BorderLayout.WEST);
            add(estadisticas, BorderLayout.CENTER);
            add(acciones, BorderLayout.SOUTH);

            lista.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    refrescar();
                }
            });
            lista.setSelectedIndex(inicial);
            refrescar();
        }

        int indice() {
            return lista.getSelectedIndex();
        }

        void recargar(int preferido) {
            String[] nombres = modelo.obtenerNombresPersonajes();
            lista.setListData(nombres);
            int idx = Math.max(0, Math.min(preferido, nombres.length - 1));
            lista.setSelectedIndex(idx);
            refrescar();
        }

        private JProgressBar agregarBarra(String nombre) {
            JProgressBar barra = UI.barra(MAX_STAT, UI.colorJugador(numero));
            JPanel fila = new JPanel(new GridLayout(1, 2, UI.MARGEN, 0));
            fila.setOpaque(false);
            fila.add(UI.etiqueta(nombre, UI.FUENTE_NORMAL));
            fila.add(barra);
            estadisticas.add(fila);
            return barra;
        }

        private void refrescar() {
            int idx = lista.getSelectedIndex();
            if (idx < 0) {
                return;
            }
            try {
                CharacterInfo ficha = modelo.obtenerFichaPersonaje(idx);
                escudo.establecerPais(ficha.obtenerPais());
                pais.setText(ficha.obtenerPais());
                velocidad.setValue(ficha.obtenerVelocidad());
                salto.setValue(ficha.obtenerSalto());
                fuerza.setValue(ficha.obtenerFuerza());
                poder.setValue(ficha.obtenerNivelPoder());
                especial.setText(UI.t("Poder especial:", "Special power:") + " " + ficha.obtenerNombrePoder());
                descripcion.setText(ficha.obtenerDescripcionPoder());
            } catch (POOBSoccerException e) {
                HeadSoccerGUI.reportarError(this, e);
            }
        }
    }
}
