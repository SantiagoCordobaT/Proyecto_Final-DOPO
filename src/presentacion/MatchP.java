package presentacion;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JPanel;
import javax.swing.Timer;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Pantalla del partido con el marcador, la cancha, el reloj de juego y la
 * lectura de teclado de ambos jugadores.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class MatchP extends JPanel {

    private static final int MILIS_CUADRO = 16;
    private static final double MAX_SEGUNDOS = 0.1;

    private final HeadSoccerGUI ventana;
    private final HeadSoccer modelo;
    private final HudP marcador;
    private final CourtP cancha;
    private final Timer temporizador;
    private long ultimoTiempo;

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

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_P || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    pausarPartido();
                } else {
                    manejarTecla(e, true);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                manejarTecla(e, false);
            }
        });

        // Evita que las teclas queden pegadas si la ventana pierde el foco
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                soltarTeclasActivas();
            }
        });
        ventana.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                soltarTeclasActivas();
            }
        });
    }

    void iniciar() {
        marcador.rotular();
        cancha.entrar();
        ultimoTiempo = System.currentTimeMillis();
        temporizador.start();
        requestFocusInWindow();
    }

    void detener() {
        temporizador.stop();
    }

    void reanudarPartido() {
        try {
            modelo.reanudar();
            ultimoTiempo = System.currentTimeMillis();
            requestFocusInWindow();
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.reportarError(this, e);
        }
    }

    void abandonar() {
        temporizador.stop();
        ventana.mostrarMenu();
    }

    private void soltarTeclasActivas() {
        if (!temporizador.isRunning()) {
            return;
        }
        try {
            modelo.soltarTodasLasAcciones();
        } catch (POOBSoccerException e) {
            // Si el partido ya terminó no hay acciones que soltar
        }
    }

    private void avanzar() {
        long ahora = System.currentTimeMillis();
        double segundos = (ahora - ultimoTiempo) / 1000.0;
        ultimoTiempo = ahora;
        if (segundos > MAX_SEGUNDOS) {
            segundos = MAX_SEGUNDOS;
        }
        try {
            modelo.actualizarTick(segundos);
            marcador.refrescar();
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

    private void manejarTecla(KeyEvent e, boolean presionada) {
        int codigo = e.getKeyCode();
        int jugador = 0;
        String accion = null;

        if (codigo == KeyEvent.VK_A) {
            jugador = 1;
            accion = "LEFT";
        } else if (codigo == KeyEvent.VK_D) {
            jugador = 1;
            accion = "RIGHT";
        } else if (codigo == KeyEvent.VK_W) {
            jugador = 1;
            accion = "JUMP";
        } else if (codigo == KeyEvent.VK_S) {
            jugador = 1;
            accion = "KICK";
        } else if (codigo == KeyEvent.VK_Q) {
            jugador = 1;
            accion = "POWER";
        } else if (codigo == KeyEvent.VK_LEFT) {
            jugador = 2;
            accion = "LEFT";
        } else if (codigo == KeyEvent.VK_RIGHT) {
            jugador = 2;
            accion = "RIGHT";
        } else if (codigo == KeyEvent.VK_UP) {
            jugador = 2;
            accion = "JUMP";
        } else if (codigo == KeyEvent.VK_DOWN) {
            jugador = 2;
            accion = "KICK";
        } else if (codigo == KeyEvent.VK_CONTROL && e.getKeyLocation() == KeyEvent.KEY_LOCATION_RIGHT) {
            jugador = 2;
            accion = "POWER";
        }

        if (accion == null) {
            return;
        }
        try {
            if (presionada) {
                modelo.presionarAccion(jugador, accion);
            } else {
                modelo.soltarAccion(jugador, accion);
            }
        } catch (POOBSoccerException ex) {
            HeadSoccerGUI.reportarError(this, ex);
        }
    }
}
