package presentacion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

import dominio.HeadSoccer;
import dominio.PlayerSummary;
import dominio.excepciones.POOBSoccerException;

/**
 * Pantalla completa del partido: bucle de simulación, lectura de teclado, HUD
 * superior y dibujo 2D de la cancha, jugadores, efectos, balón y arcos.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
@SuppressWarnings("serial")
final class MatchPanel extends JPanel {

    private static final int MILIS_CUADRO = 16;
    private static final double MAX_SEGUNDOS = 0.1;
    private static final int ALTO_CANCHA = 490;
    private static final double SEGUNDOS_AVISO_GOL = 1.4;
    private static final double ANCHO_BASE_JUGADOR = 60.0;

    private static final Color PIEL = new Color(246, 205, 160);
    private static final Color CABELLO = new Color(40, 28, 22);
    private static final Color ZAPATO = new Color(28, 28, 32);

    private final HeadSoccerGUI ventana;
    private final HeadSoccer modelo;
    private final Plate placa1 = new Plate(1, BorderLayout.WEST);
    private final Plate placa2 = new Plate(2, BorderLayout.EAST);
    private final JLabel estado = UI.etiqueta("", UI.FUENTE_NORMAL);
    private final JLabel marcador = UI.etiqueta("", UI.FUENTE_MARCADOR);
    private final JLabel ayuda = UI.etiqueta("", UI.FUENTE_PEQUENA);
    private final Canvas lienzo;
    private final Timer temporizador;

    private long ultimoTiempo;
    // datos cacheados del partido (nombreBalon, nombreCancha, items)
    private PlayerSummary jugador1;
    private PlayerSummary jugador2;
    private double[] posBalon;
    private double[][] arcos = new double[0][];
    private int totalGoles = -1;
    private double tiempoAvisoGol;

    MatchPanel(HeadSoccerGUI ventana, HeadSoccer modelo) {
        super(new BorderLayout());
        this.ventana = ventana;
        this.modelo = modelo;
        this.lienzo = new Canvas();

        add(construirHud(), BorderLayout.NORTH);
        add(lienzo, BorderLayout.CENTER);

        temporizador = new Timer(MILIS_CUADRO, e -> avanzar());
        configurarTeclado();
    }

    private JPanel construirHud() {
        estado.setForeground(UI.RESALTE);
        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(UI.FONDO);
        centro.add(estado, BorderLayout.NORTH);
        centro.add(marcador, BorderLayout.CENTER);
        centro.add(ayuda, BorderLayout.SOUTH);

        JPanel hud = new JPanel(new GridLayout(1, 3));
        hud.add(placa1);
        hud.add(centro);
        hud.add(placa2);
        return hud;
    }

    private void configurarTeclado() {
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

    // Ciclo de vida del partido

    void iniciar() {
        // datos fijos al entrar (nombreCancha, nombreBalon)
        ayuda.setText(UI.t("P / Esc: pausa", "P / Esc: pause"));
        totalGoles = -1;
        tiempoAvisoGol = 0.0;
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
            temporizador.start();
            requestFocusInWindow();
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.reportarError(this, e);
        }
    }

    void abandonar() {
        temporizador.stop();
        ventana.mostrarMenu();
    }

    private void pausarPartido() {
        try {
            temporizador.stop();
            modelo.pausar();
            ventana.abrirPausa();
        } catch (POOBSoccerException e) {
            HeadSoccerGUI.reportarError(this, e);
        }
    }

    private void soltarTeclasActivas() {
        if (!temporizador.isRunning()) {
            return;
        }
        try {
            modelo.soltarTodasLasAcciones();
        } catch (POOBSoccerException e) {
            // Si el partido ya terminó no hay acciones activas
        }
    }

    private void avanzar() {
        long ahora = System.currentTimeMillis();
        double segundos = Math.min((ahora - ultimoTiempo) / 1000.0, MAX_SEGUNDOS);
        ultimoTiempo = ahora;
        try {
            modelo.actualizarTick(segundos);
            refrescarEstado(segundos);
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

    private void refrescarEstado(double segundos) throws POOBSoccerException {
        // datos vivos del dominio antes de repaint()
        jugador1 = modelo.obtenerResumenJugador(1);
        jugador2 = modelo.obtenerResumenJugador(2);
        posBalon = modelo.obtenerPosicionBalon();
        arcos = modelo.obtenerLimitesArcos();

        placa1.refrescar(jugador1);
        placa2.refrescar(jugador2);
        marcador.setText(jugador1.obtenerGoles() + " - " + jugador2.obtenerGoles());
        estado.setText(modelo.obtenerEstadoPartido());

        int golesActuales = jugador1.obtenerGoles() + jugador2.obtenerGoles();
        if (totalGoles >= 0 && golesActuales != totalGoles) {
            tiempoAvisoGol = SEGUNDOS_AVISO_GOL;
        }
        totalGoles = golesActuales;
        if (tiempoAvisoGol > 0.0) {
            tiempoAvisoGol -= segundos;
        }
        lienzo.repaint();
    }

    // Controles

    // tecla: else if KeyEvent y Action
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

    // Placa lateral HUD

    private static final class Plate extends JPanel {

        private final Color color;
        private final UI.Badge escudo = new UI.Badge();
        private final JLabel nombre = UI.etiqueta("", UI.FUENTE_SUBTITULO);
        private final JProgressBar poder;

        Plate(int numero, String ladoEscudo) {
            super(new BorderLayout(UI.MARGEN, 0));
            this.color = UI.colorJugador(numero);
            this.poder = UI.barra(100, color);
            this.poder.setStringPainted(true);

            JPanel datos = new JPanel(new GridLayout(2, 1, 0, 4));
            datos.setOpaque(false);
            datos.add(nombre);
            datos.add(poder);

            setBackground(UI.FONDO);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 4, 0, color),
                    BorderFactory.createEmptyBorder(6, UI.MARGEN, 6, UI.MARGEN)));
            add(escudo, ladoEscudo);
            add(datos, BorderLayout.CENTER);
        }

        // dato extra HUD, panel datos y refrescar()
        void refrescar(PlayerSummary j) {
            escudo.establecerPais(j.obtenerPais());
            nombre.setText(j.obtenerNombre().toUpperCase());
            poder.setValue((int) Math.round(j.obtenerCargaPoder() * 100));
            if (j.estaPoderArmado()) {
                poder.setString(UI.t("¡PODER LISTO!", "POWER READY!"));
                poder.setForeground(UI.RESALTE);
            } else {
                poder.setString(j.obtenerNombrePoder());
                poder.setForeground(color);
            }
        }
    }

    // Lienzo 2D

    private final class Canvas extends JPanel {

        Canvas() {
            setPreferredSize(new Dimension((int) modelo.obtenerAnchoCancha(), ALTO_CANCHA));
            setBackground(UI.FONDO);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // capas: fondo -> jugadores -> (ítems) -> balón -> arcos -> gol
            // tipos cancha: sobrecarga UI.dibujarFondoCancha(..., nombreCancha)
            UI.dibujarFondoCancha(g2, getWidth(), getHeight(), (int) modelo.obtenerSueloY());
            if (jugador1 != null) {
                dibujarJugador(g2, jugador1, 1);
            }
            if (jugador2 != null) {
                dibujarJugador(g2, jugador2, 2);
            }
            if (posBalon != null) {
                dibujarBalon(g2);
            }
            dibujarArcos(g2);
            if (tiempoAvisoGol > 0.0) {
                dibujarAvisoGol(g2);
            }
        }

        private void dibujarJugador(Graphics2D g, PlayerSummary j, int numero) {
            double centroX = j.obtenerX() + j.obtenerAncho() / 2.0;
            double baseY = j.obtenerY() + j.obtenerAlto();
            double escala = j.obtenerAncho() / ANCHO_BASE_JUGADOR;
            int orientacion = j.obtenerOrientacion();
            Color[] equipo = UI.coloresPais(j.obtenerPais());

            AffineTransform guardado = g.getTransform();
            g.translate(centroX, baseY);
            g.scale(escala, escala);

            g.setColor(UI.colorJugador(numero));
            g.drawOval(-25, -6, 50, 12);

            if (j.estaPoderArmado()) {
                g.setColor(UI.RESALTE);
                g.drawOval(-35, -95, 70, 95);
            }

            g.setColor(equipo[0]);
            g.fillRect(-17, -38, 34, 30);
            g.setColor(equipo[1]);
            g.fillRect(-5, -38, 10, 30);
            g.setColor(Color.BLACK);
            g.drawRect(-17, -38, 34, 30);

            int patada = j.estaPateando() ? orientacion * 18 : 0;
            g.setColor(ZAPATO);
            g.fillRect(-16, -10, 14, 10);
            g.fillRect(2 + patada, -10, 14, 10);
            g.setColor(Color.BLACK);
            g.drawRect(-16, -10, 14, 10);
            g.drawRect(2 + patada, -10, 14, 10);

            g.setColor(PIEL);
            g.fillOval(-30, -90, 60, 60);
            g.setColor(CABELLO);
            g.fillArc(-30, -90, 60, 60, 20, 140);
            g.setColor(equipo[2]);
            g.fillRect(-26, -73, 52, 6);
            g.setColor(Color.BLACK);
            g.drawOval(-30, -90, 60, 60);

            int cara = orientacion * 6;
            g.setColor(Color.WHITE);
            g.fillOval(cara - 14, -68, 10, 12);
            g.fillOval(cara + 4, -68, 10, 12);
            g.setColor(Color.BLACK);
            g.drawOval(cara - 14, -68, 10, 12);
            g.drawOval(cara + 4, -68, 10, 12);
            int pupila = orientacion > 0 ? 4 : 1;
            g.fillOval(cara - 14 + pupila, -64, 5, 5);
            g.fillOval(cara + 4 + pupila, -64, 5, 5);
            g.drawArc(cara - 6, -50, 12, 8, 200, 140);

            dibujarEfectos(g, j.obtenerEfectos());
            g.setTransform(guardado);
        }

        // Effect nuevo, else if visual
        private void dibujarEfectos(Graphics2D g, String[] efectos) {
            for (String efecto : efectos) {
                if ("Congelado".equals(efecto)) {
                    g.setColor(new Color(150, 220, 255, 130));
                    g.fillRect(-35, -95, 70, 95);
                    g.setColor(new Color(200, 240, 255));
                    g.drawRect(-35, -95, 70, 95);
                } else if ("Aturdido".equals(efecto)) {
                    g.setColor(new Color(255, 230, 50));
                    g.fillOval(-20, -105, 10, 10);
                    g.fillOval(0, -110, 10, 10);
                    g.fillOval(15, -105, 10, 10);
                } else if ("Confundido".equals(efecto)) {
                    g.setColor(new Color(220, 150, 255));
                    g.setFont(UI.FUENTE_SUBTITULO);
                    g.drawString("?", -15, -95);
                    g.drawString("?", 5, -100);
                } else if ("Reducido".equals(efecto)) {
                    g.setColor(Color.CYAN);
                    g.drawString("↓", -5, -95);
                } else {
                    g.setColor(Color.WHITE);
                    g.drawString(efecto, -20, -100);
                }
            }
        }

        private void dibujarBalon(Graphics2D g) {
            double radio = modelo.obtenerRadioBalon();
            double x = posBalon[0];
            double y = posBalon[1];
            int diametro = (int) (radio * 2);
            int esquinaX = (int) (x - radio);
            int esquinaY = (int) (y - radio);

            g.setColor(new Color(0, 0, 0, 70));
            g.fillOval(esquinaX, (int) modelo.obtenerSueloY() - 4, diametro, 8);

            // tipos balón, colorBalon() según obtenerNombreBalon()
            g.setColor(Color.WHITE);
            g.fillOval(esquinaX, esquinaY, diametro, diametro);
            g.setColor(Color.BLACK);
            g.drawOval(esquinaX, esquinaY, diametro, diametro);
            g.fillOval((int) (x - radio / 3), (int) (y - radio / 3), (int) (radio * 2 / 3), (int) (radio * 2 / 3));
            g.drawLine(esquinaX + 4, (int) y, esquinaX + diametro - 4, (int) y);
            g.drawLine((int) x, esquinaY + 4, (int) x, esquinaY + diametro - 4);
        }

        // arcos dinámicos desde obtenerLimitesArcos()
        private void dibujarArcos(Graphics2D g) {
            double centro = modelo.obtenerAnchoCancha() / 2.0;
            int grosor = (int) modelo.obtenerGrosorTravesano();
            for (double[] arco : arcos) {
                int izq = (int) arco[0];
                int arriba = (int) arco[1];
                int ancho = (int) arco[2];
                int alto = (int) arco[3];

                g.setColor(new Color(255, 255, 255, 90));
                for (int x = izq; x < izq + ancho; x += 15) {
                    g.drawLine(x, arriba, x, arriba + alto);
                }
                for (int y = arriba; y < arriba + alto; y += 15) {
                    g.drawLine(izq, y, izq + ancho, y);
                }

                g.setColor(Color.WHITE);
                g.fillRect(izq, arriba, ancho, grosor);
                int posteX = izq < centro ? izq + ancho - grosor : izq;
                g.fillRect(posteX, arriba, grosor, alto);

                g.setColor(Color.GRAY);
                g.drawRect(izq, arriba, ancho, grosor);
                g.drawRect(posteX, arriba, grosor, alto);
            }
        }

        private void dibujarAvisoGol(Graphics2D g) {
            g.setFont(UI.FUENTE_TITULO);
            String texto = UI.t("¡GOL!", "GOAL!");
            int x = (getWidth() - g.getFontMetrics().stringWidth(texto)) / 2;
            int y = (int) (modelo.obtenerSueloY() * 0.42);
            g.setColor(Color.BLACK);
            g.drawString(texto, x + 3, y + 3);
            g.setColor(UI.RESALTE);
            g.drawString(texto, x, y);
        }
    }
}
