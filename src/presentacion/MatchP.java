package presentacion;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.Timer;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Pantalla donde se juega el partido, con el marcador arriba, la cancha en el
 * centro, el temporizador de cuadros y el manejo de teclado.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class MatchP extends JPanel {

    private static final int MILIS_CUADRO = 16;
    private static final double NANOS_POR_SEGUNDO = 1_000_000_000.0;
    private static final double MAX_SEGUNDOS_CUADRO = 0.1;
    private static final int RANGO_UBICACION = 8;

    private final HeadSoccerGUI ventana;
    private final HeadSoccer modelo;
    private final HudP marcador;
    private final CourtP cancha;
    private final Timer temporizador;
    private final Map<Integer, PlayerKey> teclas = new HashMap<>();
    private long ultimoNano;

    MatchP(HeadSoccerGUI ventana, HeadSoccer modelo) {
        super(new BorderLayout());
        this.ventana = ventana;
        this.modelo = modelo;
        marcador = new HudP(modelo);
        cancha = new CourtP(modelo);
        add(marcador, BorderLayout.NORTH);
        add(cancha, BorderLayout.CENTER);
        temporizador = new Timer(MILIS_CUADRO, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                avanzar();
            }
        });
        registrarTecla(1, KeyEvent.VK_A, KeyEvent.KEY_LOCATION_STANDARD, "LEFT");
        registrarTecla(1, KeyEvent.VK_D, KeyEvent.KEY_LOCATION_STANDARD, "RIGHT");
        registrarTecla(1, KeyEvent.VK_W, KeyEvent.KEY_LOCATION_STANDARD, "JUMP");
        registrarTecla(1, KeyEvent.VK_S, KeyEvent.KEY_LOCATION_STANDARD, "KICK");
        registrarTecla(1, KeyEvent.VK_Q, KeyEvent.KEY_LOCATION_STANDARD, "POWER");

        registrarTecla(2, KeyEvent.VK_LEFT, KeyEvent.KEY_LOCATION_STANDARD, "LEFT");
        registrarTecla(2, KeyEvent.VK_RIGHT, KeyEvent.KEY_LOCATION_STANDARD, "RIGHT");
        registrarTecla(2, KeyEvent.VK_UP, KeyEvent.KEY_LOCATION_STANDARD, "JUMP");
        registrarTecla(2, KeyEvent.VK_DOWN, KeyEvent.KEY_LOCATION_STANDARD, "KICK");
        registrarTecla(2, KeyEvent.VK_CONTROL, KeyEvent.KEY_LOCATION_RIGHT, "POWER");

        setFocusable(true);
        addKeyListener(new Keyboard());
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                soltarTeclas();
            }
        });
    }

    void iniciar() {
        marcador.rotular();
        cancha.entrar();
        ultimoNano = System.nanoTime();
        temporizador.start();
        requestFocusInWindow();
    }

    void detener() {
        temporizador.stop();
    }

    void reanudarPartido() {
        try {
            modelo.reanudar();
            requestFocusInWindow();
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.reportarError(this, e);
        }
    }

    void abandonar() {
        temporizador.stop();
        ventana.mostrarMenu();
    }

    private void avanzar() {
        long ahora = System.nanoTime();
        double segundos = (ahora - ultimoNano) / NANOS_POR_SEGUNDO;
        if (segundos > MAX_SEGUNDOS_CUADRO) {
            segundos = MAX_SEGUNDOS_CUADRO;
        }
        ultimoNano = ahora;
        try {
            modelo.actualizarTick(segundos);
            marcador.refrescar(segundos);
            cancha.animar(segundos);
            if (modelo.estaPartidoTerminado()) {
                temporizador.stop();
                ventana.mostrarResultado();
            }
        } catch (POOBSoccerException e) {
            temporizador.stop();
            HeadSoccerGUI.reportarError(this, e);
            ventana.mostrarMenu();
        }
    }

    private void pausarPartido() {
        try {
            modelo.pausar();
            new PauseD(ventana, this).abrir();
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.reportarError(this, e);
        }
    }

    private void soltarTeclas() {
        try {
            modelo.soltarTodasLasAcciones();
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.reportarError(this, e);
        }
    }

    private void registrarTecla(int jugador, int codigoTecla, int ubicacion, String accion) {
        teclas.put(ranuraTecla(codigoTecla, ubicacion), new PlayerKey(jugador, accion));
    }

    // Combina código y ubicación para distinguir Ctrl derecho del izquierdo
    private static int ranuraTecla(int codigoTecla, int ubicacion) {
        return codigoTecla * RANGO_UBICACION + ubicacion;
    }

    private final class Keyboard extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_P || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                pausarPartido();
            } else {
                PlayerKey tecla = teclas.get(ranuraTecla(e.getKeyCode(), e.getKeyLocation()));
                if (tecla != null) {
                    try {
                        tecla.presionar(modelo);
                    } catch (POOBSoccerException ex) {
                        HeadSoccerGUI.reportarError(MatchP.this, ex);
                    }
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            PlayerKey tecla = teclas.get(ranuraTecla(e.getKeyCode(), e.getKeyLocation()));
            if (tecla != null) {
                try {
                    tecla.soltar(modelo);
                } catch (POOBSoccerException ex) {
                    HeadSoccerGUI.reportarError(MatchP.this, ex);
                }
            }
        }
    }
}
