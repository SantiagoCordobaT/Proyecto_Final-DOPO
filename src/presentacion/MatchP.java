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
 * Pantalla del partido: marcador al norte y lienzo de la cancha al
 * centro. Su temporizador entrega el tiempo real a la fachada y repinta; su
 * escuchador de teclado traduce las teclas de los dos jugadores en acciones de
 * la fachada. No contiene reglas del juego.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class MatchP extends JPanel {

    private static final int MILIS_CUADRO = 8;
    private static final double NANOS_POR_SEGUNDO = 1e9;
    // Tope de tiempo por cuadro: una pausa de la ventana no hace saltar la fisica
    private static final double MAX_SEGUNDOS_CUADRO = 0.1;
    private static final int RANGO_UBICACION = 8;
    private static final String[] ACCIONES = { "LEFT", "RIGHT", "JUMP", "KICK", "POWER" };

    private final HeadSoccerGUI ventana;
    private final HeadSoccer modelo;
    private final HudP marcador;
    private final CourtP cancha;
    private final Timer temporizador;
    private final Map<Integer, PlayerKey> teclas = new HashMap<Integer, PlayerKey>();
    private long ultimoNano;

    /**
     * @param ventana Ventana principal.
     * @param modelo  Fachada del dominio.
     */
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
        asignarJugador(1, teclaEstandar(KeyEvent.VK_A), teclaEstandar(KeyEvent.VK_D), teclaEstandar(KeyEvent.VK_W),
                teclaEstandar(KeyEvent.VK_S), teclaEstandar(KeyEvent.VK_Q));
        asignarJugador(2, teclaEstandar(KeyEvent.VK_LEFT), teclaEstandar(KeyEvent.VK_RIGHT),
                teclaEstandar(KeyEvent.VK_UP), teclaEstandar(KeyEvent.VK_DOWN),
                ranuraTecla(KeyEvent.VK_CONTROL, KeyEvent.KEY_LOCATION_RIGHT));
        setFocusable(true);
        addKeyListener(new Keyboard());
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                soltarTeclas();
            }
        });
    }

    /** Arranca el ciclo del partido ya iniciado en la fachada y toma el teclado. */
    void iniciar() {
        marcador.rotular();
        cancha.entrar();
        ultimoNano = System.nanoTime();
        temporizador.start();
        requestFocusInWindow();
    }

    /** Detiene el ciclo del partido. */
    void detener() {
        temporizador.stop();
    }

    /** Reanuda el partido pausado y recupera el teclado. */
    void reanudarPartido() {
        try {
            modelo.reanudar();
            requestFocusInWindow();
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.reportarError(this, e);
        }
    }

    /** Abandona el partido en curso y vuelve al menu. */
    void abandonar() {
        temporizador.stop();
        ventana.mostrarMenu();
    }

    /**
     * Un cuadro: avanza el dominio el tiempo real transcurrido, refresca y detecta
     * el final.
     */
    private void avanzar() {
        long ahora = System.nanoTime();
        double segundos = Math.min((ahora - ultimoNano) / NANOS_POR_SEGUNDO, MAX_SEGUNDOS_CUADRO);
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

    /**
     * Asigna a un jugador, en el orden de {@link #ACCIONES}, las teclas indicadas.
     */
    private void asignarJugador(int jugador, int... codigos) {
        for (int i = 0; i < ACCIONES.length; i++) {
            teclas.put(codigos[i], new PlayerKey(jugador, ACCIONES[i]));
        }
    }

    private static int teclaEstandar(int codigoTecla) {
        return ranuraTecla(codigoTecla, KeyEvent.KEY_LOCATION_STANDARD);
    }

    /** Combina tecla y ubicacion: distingue el Ctrl derecho del izquierdo. */
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
