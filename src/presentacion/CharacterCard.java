package presentacion;

import static presentacion.Texts.texto;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Panel con la lista de personajes, escudo, barras de estadísticas y poder del
 * personaje elegido por un jugador.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class CharacterCard extends JPanel {

    private static final int ESTADISTICA_MAX = 5;

    private final Resources recursos = Resources.obtenerInstancia();
    private final HeadSoccer modelo;
    private final int numero;
    private final JList<String> personajes;
    private final TeamBadge escudo = new TeamBadge();
    private final JLabel pais;
    private final JPanel estadisticas;
    private final JProgressBar velocidad;
    private final JProgressBar salto;
    private final JProgressBar fuerza;
    private final JProgressBar poder;
    private final JLabel especial;
    private final JLabel descripcion;
    private boolean recargando;
    private Runnable alSeleccionar;

    CharacterCard(HeadSoccer modelo, int numero, int inicial) {
        super(new BorderLayout(Resources.ESPACIO, Resources.ESPACIO));
        this.modelo = modelo;
        this.numero = numero;
        setBackground(Resources.CONCRETO);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(recursos.colorJugador(numero), 3),
                BorderFactory.createEmptyBorder(Resources.ESPACIO, Resources.ESPACIO, Resources.ESPACIO,
                        Resources.ESPACIO)));
        personajes = recursos.crearLista(modelo.obtenerNombresPersonajes());
        pais = recursos.crearEtiqueta("", Resources.FUENTE_TEXTO);
        estadisticas = new JPanel(new GridLayout(0, 1, 0, Resources.ESPACIO / 2));
        estadisticas.setOpaque(false);
        JPanel equipo = new JPanel(new FlowLayout(FlowLayout.CENTER, Resources.ESPACIO, 0));
        equipo.setOpaque(false);
        equipo.add(escudo);
        equipo.add(pais);
        estadisticas.add(equipo);
        velocidad = agregarEstadistica("card.speed");
        salto = agregarEstadistica("card.jump");
        fuerza = agregarEstadistica("card.strength");
        poder = agregarEstadistica("card.power");
        especial = recursos.crearEtiqueta("", Resources.FUENTE_TEXTO);
        descripcion = recursos.crearEtiqueta("", Resources.FUENTE_TEXTO);
        estadisticas.add(especial);
        estadisticas.add(descripcion);
        add(recursos.crearEtiqueta(texto("select.player") + " " + numero, Resources.FUENTE_ENCABEZADO),
                BorderLayout.NORTH);
        add(new JScrollPane(personajes), BorderLayout.WEST);
        add(estadisticas, BorderLayout.CENTER);
        personajes.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                refrescar();
                if (!recargando && alSeleccionar != null) {
                    alSeleccionar.run();
                }
            }
        });
        personajes.setSelectedIndex(inicial);
        refrescar();
    }

    int obtenerIndiceSeleccionado() {
        return personajes.getSelectedIndex();
    }

    void alSeleccionar(Runnable accion) {
        this.alSeleccionar = accion;
    }

    void recargar(int indicePreferido) {
        recargando = true;
        String[] nombres = modelo.obtenerNombresPersonajes();
        personajes.setListData(nombres);
        int indice = indicePreferido;
        if (indice < 0) {
            indice = 0;
        } else if (indice >= nombres.length) {
            indice = nombres.length - 1;
        }
        personajes.setSelectedIndex(indice);
        refrescar();
        recargando = false;
    }

    private JProgressBar agregarEstadistica(String clave) {
        JProgressBar barra = recursos.crearBarra(ESTADISTICA_MAX, recursos.colorJugador(numero));
        JPanel fila = new JPanel(new GridLayout(1, 2, Resources.ESPACIO, 0));
        fila.setOpaque(false);
        fila.add(recursos.crearEtiqueta(texto(clave), Resources.FUENTE_TEXTO));
        fila.add(barra);
        estadisticas.add(fila);
        return barra;
    }

    private void refrescar() {
        int indice = personajes.getSelectedIndex();
        if (indice < 0) {
            return;
        }
        try {
            String equipo = modelo.obtenerPaisPersonaje(indice);
            escudo.establecerPais(equipo);
            pais.setText(equipo);
            velocidad.setValue(modelo.obtenerVelocidadPersonaje(indice));
            salto.setValue(modelo.obtenerSaltoPersonaje(indice));
            fuerza.setValue(modelo.obtenerFuerzaPersonaje(indice));
            poder.setValue(modelo.obtenerNivelPoderPersonaje(indice));
            especial.setText(texto("card.special") + " " + modelo.obtenerNombrePoderPersonaje(indice));
            descripcion.setText(modelo.obtenerDescripcionPoderPersonaje(indice));
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.reportarError(this, e);
        }
    }
}
