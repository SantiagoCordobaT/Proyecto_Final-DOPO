package presentacion;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import dominio.FichaPersonaje;
import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Ficha de selección de un jugador: muestra los personajes disponibles, sus
 * estadísticas y botones propios para editar o eliminar el elegido.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class CharacterCard extends JPanel {

    private static final int MAX_STAT = 5;

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

    CharacterCard(CharacterSelectD padre, HeadSoccer modelo, int numero, int inicial) {
        super(new BorderLayout(Estilo.MARGEN, Estilo.MARGEN));
        this.modelo = modelo;
        this.numero = numero;

        setBackground(Estilo.TARJETA);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.colorJugador(numero), 3),
                BorderFactory.createEmptyBorder(Estilo.MARGEN, Estilo.MARGEN, Estilo.MARGEN, Estilo.MARGEN)));

        personajes = Estilo.lista(modelo.obtenerNombresPersonajes());
        pais = Estilo.etiqueta("", Estilo.FUENTE_NORMAL);
        estadisticas = new JPanel(new GridLayout(0, 1, 0, 6));
        estadisticas.setOpaque(false);

        JPanel equipo = new JPanel(new FlowLayout(FlowLayout.CENTER, Estilo.MARGEN, 0));
        equipo.setOpaque(false);
        equipo.add(escudo);
        equipo.add(pais);
        estadisticas.add(equipo);

        velocidad = agregarBarra(Texts.t("Velocidad", "Speed"));
        salto = agregarBarra(Texts.t("Salto", "Jump"));
        fuerza = agregarBarra(Texts.t("Fuerza", "Strength"));
        poder = agregarBarra(Texts.t("Poder Único", "Unique Power"));

        especial = Estilo.etiqueta("", Estilo.FUENTE_NORMAL);
        descripcion = Estilo.etiqueta("", Estilo.FUENTE_NORMAL);
        estadisticas.add(especial);
        estadisticas.add(descripcion);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        acciones.setOpaque(false);
        acciones.add(Estilo.boton(Texts.t("Editar", "Edit"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                padre.editarPersonaje(CharacterCard.this);
            }
        }));
        acciones.add(Estilo.boton(Texts.t("Eliminar", "Delete"), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                padre.eliminarPersonaje(CharacterCard.this);
            }
        }));

        add(Estilo.etiqueta(Texts.t("Jugador", "Player") + " " + numero, Estilo.FUENTE_SUBTITULO), BorderLayout.NORTH);
        add(new JScrollPane(personajes), BorderLayout.WEST);
        add(estadisticas, BorderLayout.CENTER);
        add(acciones, BorderLayout.SOUTH);

        personajes.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    refrescar();
                }
            }
        });
        personajes.setSelectedIndex(inicial);
        refrescar();
    }

    int obtenerIndiceSeleccionado() {
        return personajes.getSelectedIndex();
    }

    void recargar(int indicePreferido) {
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
    }

    private JProgressBar agregarBarra(String nombre) {
        JProgressBar barra = Estilo.barra(MAX_STAT, Estilo.colorJugador(numero));
        JPanel fila = new JPanel(new GridLayout(1, 2, Estilo.MARGEN, 0));
        fila.setOpaque(false);
        fila.add(Estilo.etiqueta(nombre, Estilo.FUENTE_NORMAL));
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
            FichaPersonaje ficha = modelo.obtenerFichaPersonaje(indice);
            escudo.establecerPais(ficha.pais);
            pais.setText(ficha.pais);
            velocidad.setValue(ficha.velocidad);
            salto.setValue(ficha.salto);
            fuerza.setValue(ficha.fuerza);
            poder.setValue(ficha.nivelPoder);
            especial.setText(Texts.t("Poder especial:", "Special power:") + " " + ficha.nombrePoder);
            descripcion.setText(ficha.descripcionPoder);
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.reportarError(this, e);
        }
    }
}
