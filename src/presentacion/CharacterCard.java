package presentacion;

import static presentacion.Texts.text;

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
 * Ficha de seleccion de un jugador: lista de personajes, escudo del pais,
 * estadisticas en barras y poder especial. Solo consulta a la fachada.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class CharacterCard extends JPanel {

    private static final int ESTADISTICA_MAX = 5;

    private final Resources recursos = Resources.getInstance();
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

    /**
     * @param modelo  Fachada del dominio.
     * @param numero  Jugador (1 o 2) al que pertenece la ficha.
     * @param inicial Personaje seleccionado al abrir.
     */
    CharacterCard(HeadSoccer modelo, int numero, int inicial) {
        super(new BorderLayout(Resources.ESPACIO, Resources.ESPACIO));
        this.modelo = modelo;
        this.numero = numero;
        setBackground(Resources.CONCRETO);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(recursos.playerColor(numero), 3),
                BorderFactory.createEmptyBorder(Resources.ESPACIO, Resources.ESPACIO, Resources.ESPACIO,
                        Resources.ESPACIO)));
        personajes = recursos.list(modelo.getCharacterNames());
        pais = recursos.label("", Resources.FUENTE_TEXTO);
        estadisticas = new JPanel(new GridLayout(0, 1, 0, Resources.ESPACIO / 2));
        estadisticas.setOpaque(false);
        JPanel equipo = new JPanel(new FlowLayout(FlowLayout.CENTER, Resources.ESPACIO, 0));
        equipo.setOpaque(false);
        equipo.add(escudo);
        equipo.add(pais);
        estadisticas.add(equipo);
        velocidad = addStat("card.speed");
        salto = addStat("card.jump");
        fuerza = addStat("card.strength");
        poder = addStat("card.power");
        especial = recursos.label("", Resources.FUENTE_TEXTO);
        descripcion = recursos.label("", Resources.FUENTE_TEXTO);
        estadisticas.add(especial);
        estadisticas.add(descripcion);
        add(recursos.label(text("select.player") + " " + numero, Resources.FUENTE_ENCABEZADO), BorderLayout.NORTH);
        add(new JScrollPane(personajes), BorderLayout.WEST);
        add(estadisticas, BorderLayout.CENTER);
        personajes.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                refresh();
            }
        });
        personajes.setSelectedIndex(inicial);
        refresh();
    }

    /** @return Indice del personaje elegido en el catalogo. */
    int getSelectedIndex() {
        return personajes.getSelectedIndex();
    }

    private JProgressBar addStat(String clave) {
        JProgressBar barra = recursos.bar(ESTADISTICA_MAX, recursos.playerColor(numero));
        JPanel fila = new JPanel(new GridLayout(1, 2, Resources.ESPACIO, 0));
        fila.setOpaque(false);
        fila.add(recursos.label(text(clave), Resources.FUENTE_TEXTO));
        fila.add(barra);
        estadisticas.add(fila);
        return barra;
    }

    /**
     * Muestra los datos del personaje seleccionado tal como los entrega el dominio.
     */
    private void refresh() {
        int indice = personajes.getSelectedIndex();
        try {
            String equipo = modelo.getCharacterCountry(indice);
            escudo.setCountry(equipo);
            pais.setText(equipo);
            velocidad.setValue(modelo.getCharacterSpeed(indice));
            salto.setValue(modelo.getCharacterJump(indice));
            fuerza.setValue(modelo.getCharacterStrength(indice));
            poder.setValue(modelo.getCharacterPowerLevel(indice));
            especial.setText(text("card.special") + " " + modelo.getCharacterPowerName(indice));
            descripcion.setText(modelo.getCharacterPowerDescription(indice));
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.report(this, e);
        }
    }
}
