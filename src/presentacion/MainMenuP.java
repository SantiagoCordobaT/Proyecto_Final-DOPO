package presentacion;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Pantalla del menú principal con el título del juego y los botones de inicio,
 * configuración y salida.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class MainMenuP extends JPanel {

    private final JLabel subtitulo;
    private final JButton jugar;
    private final JButton configuracion;
    private final JButton salir;

    MainMenuP(HeadSoccerGUI ventana) {
        super(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(Estilo.MARGEN * 3, Estilo.MARGEN, Estilo.MARGEN * 3, Estilo.MARGEN));

        JLabel titulo = Estilo.etiqueta("POOBSOCCER", Estilo.FUENTE_TITULO);
        titulo.setForeground(Estilo.RESALTE);
        subtitulo = Estilo.etiqueta("", Estilo.FUENTE_SUBTITULO);

        jugar = Estilo.boton("", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ventana.abrirModosJuego();
            }
        });
        configuracion = Estilo.boton("", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ventana.abrirConfiguracion();
            }
        });
        salir = Estilo.boton("", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ventana.cerrar();
            }
        });

        JPanel encabezado = new JPanel(new GridLayout(2, 1, 0, 6));
        encabezado.setOpaque(false);
        encabezado.add(titulo);
        encabezado.add(subtitulo);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, Estilo.MARGEN * 2, 0));
        botones.setOpaque(false);
        botones.add(jugar);
        botones.add(configuracion);
        botones.add(salir);

        add(encabezado, BorderLayout.NORTH);
        add(botones, BorderLayout.SOUTH);
        refrescar();
    }

    void refrescar() {
        subtitulo.setText(Texts.t("Fútbol 1 vs 1", "1 vs 1 Soccer"));
        jugar.setText(Texts.t("Jugar", "Play"));
        configuracion.setText(Texts.t("Configuración", "Settings"));
        salir.setText(Texts.t("Salir", "Exit"));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Estilo.dibujarFondoCancha(g, getWidth(), getHeight(), (int) (getHeight() * 0.78));
    }
}
