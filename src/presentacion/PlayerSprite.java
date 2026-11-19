package presentacion;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Personaje de Head Soccer animado: cabeza grande, torso corto y zapatos
 * pegados
 * al cuerpo (sin piernas ni manos). Deduce su animacion de lo que cambia entre
 * cuadros (corre, salta, cae) y de lo que informa la fachada (patada, poder
 * armado, efectos). No decide ninguna regla.
 *
 * Se dibuja en coordenadas nominales (origen en los pies, 60 de ancho por 90 de
 * alto) y se escala al tamano real, asi el efecto de reduccion encoge todo.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class PlayerSprite implements Actor {

    private static final double ANCHO_NOMINAL = 60.0;
    private static final double RADIANES_CARRERA_POR_PIXEL = 0.13;
    private static final double PIXELES_TELETRANSPORTE = 120.0;
    private static final double VELOCIDAD_INCLINACION = 600.0;
    private static final double INCLINACION_MAXIMA = 0.10;
    private static final double SEGUNDOS_PATADA = 0.22;
    private static final double ALCANCE_PATADA = 26.0;
    private static final double ELEVACION_PATADA = 13.0;
    private static final double INCLINACION_PATADA = 0.14;
    private static final double APLASTAMIENTO_ATERRIZAJE = 0.9;
    private static final double ESTIRAMIENTO_SALTO = -0.7;
    private static final double CANTIDAD_APLASTAMIENTO = 0.14;
    private static final double RECUPERACION_APLASTAMIENTO = 9.0;
    private static final double PARPADEO_CADA = 3.1;
    private static final double SEGUNDOS_PARPADEO = 0.12;
    private static final double VELOCIDAD_AURA = 7.0;
    private static final double CHISPA_AURA_CADA = 0.07;
    private static final double ALTURA_DESVANECIMIENTO_SOMBRA = 260.0;
    private static final double VELOCIDAD_ZANCADA = 60.0;
    private static final double ALCANCE_PASO = 7.0;
    private static final double ELEVACION_PASO = 5.0;
    private static final double REBOTE_CABEZA = 1.3;
    private static final double MIRADA = 2.6;
    private static final double DESPLAZAMIENTO_CARA = 5.0;
    private static final int CANTIDAD_POLVO = 9;
    private static final double VELOCIDAD_POLVO = 130.0;
    private static final int CANTIDAD_CHISPAS = 7;
    private static final double VELOCIDAD_CHISPAS = 240.0;
    private static final double VELOCIDAD_CHISPA_AURA = 90.0;

    private static final Color PIEL = new Color(246, 205, 160);
    private static final Color SOMBRA_PIEL = new Color(214, 166, 124);
    private static final Color CABELLO = new Color(40, 28, 22);
    private static final Color CONTORNO = new Color(20, 16, 14);
    private static final Color ZAPATO = new Color(28, 28, 32);
    private static final Color BRILLO_ZAPATO = new Color(255, 255, 255, 120);
    private static final Color SOMBRA = new Color(0, 0, 0, 95);
    private static final Color POLVO = new Color(176, 170, 160);
    private static final Color[] AURA = { new Color(255, 232, 80, 38), new Color(255, 232, 80, 60),
            new Color(255, 244, 170, 85) };
    private static final BasicStroke LINEA = new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private static final BasicStroke ANILLO = new BasicStroke(3f);

    private static final Ellipse2D CABEZA = new Ellipse2D.Double(-30.0, -90.0, 60.0, 60.0);
    private static final Arc2D GORRA_CABELLO = new Arc2D.Double(-30.0, -90.0, 60.0, 60.0, 18.0, 144.0, Arc2D.CHORD);
    private static final Rectangle2D CINTA = new Rectangle2D.Double(-26.5, -73.0, 53.0, 5.5);
    private static final RoundRectangle2D TORSO = new RoundRectangle2D.Double(-17.0, -38.0, 34.0, 30.0, 12.0, 12.0);
    private static final Rectangle2D FRANJA = new Rectangle2D.Double(-4.0, -38.0, 8.0, 30.0);
    private static final RadialGradientPaint VOLUMEN_CABEZA = new RadialGradientPaint(new Point2D.Double(-9.0, -72.0),
            48f, new float[] { 0f, 0.35f, 1f },
            new Color[] { new Color(255, 255, 255, 80), new Color(255, 255, 255, 0), new Color(0, 0, 0, 75) });
    private static final GradientPaint VOLUMEN_TORSO = new GradientPaint(-17f, 0f, new Color(255, 255, 255, 40), 17f,
            0f, new Color(0, 0, 0, 80));

    private final HeadSoccer modelo;
    private final int numero;
    private final Particles particulas;
    private final EffectOverlay capaEfectos = new EffectOverlay();
    private final Color anillo;
    private final double suelo;
    private final Ellipse2D.Double forma = new Ellipse2D.Double();
    private final Line2D.Double trazo = new Line2D.Double();
    private final Arc2D.Double boca = new Arc2D.Double();

    private Color[] equipo;
    private String[] efectos = new String[0];
    private double centroX;
    private double baseY;
    private double escala = 1.0;
    private int orientacion = 1;
    private boolean armado;
    private boolean pateando;
    private boolean enAire;
    private double velocidadX;
    private double faseCarrera;
    private double aplastamiento;
    private double tiempoPatada;
    private double reloj;
    private double tiempoChispa;
    private double miradaX;
    private double miradaY;

    /**
     * @param modelo     Fachada del dominio.
     * @param numero     Jugador (1 o 2) que representa.
     * @param particulas Particulas de la cancha, para el polvo y las chispas.
     */
    PlayerSprite(HeadSoccer modelo, int numero, Particles particulas) {
        Resources recursos = Resources.getInstance();
        this.modelo = modelo;
        this.numero = numero;
        this.particulas = particulas;
        this.anillo = recursos.playerColor(numero);
        this.suelo = modelo.getGroundY();
        this.equipo = recursos.teamColors("");
        this.reloj = numero;
    }

    @Override
    public void update(double segundos) throws POOBSoccerException {
        double[] limites = modelo.getPlayerBounds(numero);
        double[] balon = modelo.getBallPosition();
        double nuevoCentroX = limites[0] + limites[2] / 2.0;
        double nuevaBaseY = limites[1] + limites[3];
        double desplazamiento = nuevoCentroX - centroX;
        boolean ahoraEnAire = nuevaBaseY < suelo - 1.0;
        boolean ahoraPateando = modelo.isPlayerKicking(numero);

        reloj += segundos;
        if (Math.abs(desplazamiento) < PIXELES_TELETRANSPORTE && segundos > 0.0) {
            velocidadX = desplazamiento / segundos;
            faseCarrera += Math.abs(desplazamiento) * RADIANES_CARRERA_POR_PIXEL;
        }
        if (enAire && !ahoraEnAire) {
            aplastamiento = APLASTAMIENTO_ATERRIZAJE;
            particulas.burst(nuevoCentroX, suelo, POLVO, CANTIDAD_POLVO, VELOCIDAD_POLVO);
        }
        if (!enAire && ahoraEnAire) {
            aplastamiento = ESTIRAMIENTO_SALTO;
        }
        aplastamiento *= Math.exp(-RECUPERACION_APLASTAMIENTO * segundos);
        if (ahoraPateando && !pateando) {
            tiempoPatada = SEGUNDOS_PATADA;
            particulas.burst(nuevoCentroX + orientacion * limites[2] / 2.0, nuevaBaseY, Resources.LISTO, CANTIDAD_CHISPAS,
                    VELOCIDAD_CHISPAS);
        }
        tiempoPatada = Math.max(0.0, tiempoPatada - segundos);

        armado = modelo.isPlayerPowerArmed(numero);
        tiempoChispa += segundos;
        if (armado && tiempoChispa >= CHISPA_AURA_CADA) {
            tiempoChispa = 0.0;
            particulas.burst(nuevoCentroX, nuevaBaseY - limites[3] / 2.0, Resources.LISTO, 1, VELOCIDAD_CHISPA_AURA);
        }

        double haciaX = balon[0] - nuevoCentroX;
        double haciaY = balon[1] - (limites[1] + limites[2] / 2.0);
        double distancia = Math.max(1.0, Math.hypot(haciaX, haciaY));
        miradaX = MIRADA * haciaX / distancia;
        miradaY = MIRADA * haciaY / distancia;

        equipo = Resources.getInstance().teamColors(modelo.getPlayerCountry(numero));
        efectos = modelo.getPlayerEffects(numero);
        orientacion = modelo.getPlayerFacing(numero);
        escala = limites[2] / ANCHO_NOMINAL;
        centroX = nuevoCentroX;
        baseY = nuevaBaseY;
        enAire = ahoraEnAire;
        pateando = ahoraPateando;
    }

    @Override
    public void paint(Graphics2D g) {
        double ancho = ANCHO_NOMINAL * escala;
        double atenuacion = Math.max(0.35, 1.0 - (suelo - baseY) / ALTURA_DESVANECIMIENTO_SOMBRA);
        forma.setFrame(centroX - ancho * atenuacion / 2.0, suelo - 5.0, ancho * atenuacion, 12.0);
        g.setColor(SOMBRA);
        g.fill(forma);
        forma.setFrame(centroX - ancho * 0.62, suelo - 7.0, ancho * 1.24, 16.0);
        g.setColor(anillo);
        g.setStroke(ANILLO);
        g.draw(forma);

        AffineTransform mundo = g.getTransform();
        g.translate(centroX, baseY);
        g.scale(escala * (1.0 + aplastamiento * CANTIDAD_APLASTAMIENTO),
                escala * (1.0 - aplastamiento * CANTIDAD_APLASTAMIENTO));
        g.setStroke(LINEA);
        paintAura(g);
        double impulsoPatada = Math.sin(Math.PI * (1.0 - tiempoPatada / SEGUNDOS_PATADA));
        double zancada = enAire ? 0.0 : Math.min(1.0, Math.abs(velocidadX) / VELOCIDAD_ZANCADA);
        paintShoe(g, -orientacion, -zancada * Math.sin(faseCarrera), -zancada * Math.cos(faseCarrera), 0.0);
        paintTorso(g);
        paintShoe(g, orientacion, zancada * Math.sin(faseCarrera), zancada * Math.cos(faseCarrera), impulsoPatada);
        paintHead(g, impulsoPatada);
        capaEfectos.paint(g, efectos, reloj);
        g.setTransform(mundo);
    }

    /** Halo que late alrededor del personaje mientras su poder esta armado. */
    private void paintAura(Graphics2D g) {
        if (armado) {
            double pulso = 3.0 * Math.sin(reloj * VELOCIDAD_AURA);
            for (int i = 0; i < AURA.length; i++) {
                double radio = 56.0 - 9.0 * i + pulso;
                forma.setFrame(-radio, -48.0 - radio, 2.0 * radio, 2.0 * radio);
                g.setColor(AURA[i]);
                g.fill(forma);
            }
        }
    }

    /**
     * Zapato de un lado: alterna con el otro al correr y, si es el delantero, sale
     * hacia adelante durante la patada.
     *
     * @param lado          Lado del cuerpo: 1 derecha, -1 izquierda.
     * @param alcance       Avance del paso (-1 a 1).
     * @param elevacion     Elevacion del paso (-1 a 1; solo se levanta con valores
     *                      positivos).
     * @param impulsoPatada Avance de la patada (0 a 1).
     */
    private void paintShoe(Graphics2D g, int lado, double alcance, double elevacion, double impulsoPatada) {
        double x = lado * 13.0 + alcance * ALCANCE_PASO + lado * impulsoPatada * ALCANCE_PATADA;
        double y = -6.0 - Math.max(0.0, elevacion) * ELEVACION_PASO - impulsoPatada * ELEVACION_PATADA;
        forma.setFrame(x - 12.0, y - 6.0, 24.0, 12.0);
        g.setColor(ZAPATO);
        g.fill(forma);
        g.setColor(CONTORNO);
        g.draw(forma);
        forma.setFrame(x - 2.0 + orientacion * 4.0, y - 4.5, 7.0, 3.5);
        g.setColor(BRILLO_ZAPATO);
        g.fill(forma);
    }

    /** Torso corto con la camiseta del pais. */
    private void paintTorso(Graphics2D g) {
        g.setColor(equipo[0]);
        g.fill(TORSO);
        g.setColor(equipo[1]);
        g.fill(FRANJA);
        g.setPaint(VOLUMEN_TORSO);
        g.fill(TORSO);
        g.setColor(CONTORNO);
        g.draw(TORSO);
    }

    /**
     * Cabeza con volumen: se inclina al correr y al patear, mira al balon y
     * parpadea.
     */
    private void paintHead(Graphics2D g, double impulsoPatada) {
        AffineTransform cuerpo = g.getTransform();
        double inclinacion = INCLINACION_MAXIMA * Math.max(-1.0, Math.min(1.0, velocidadX / VELOCIDAD_INCLINACION))
                - orientacion * INCLINACION_PATADA * impulsoPatada;
        g.rotate(inclinacion, 0.0, -36.0);
        g.translate(0.0, REBOTE_CABEZA * Math.sin(2.0 * faseCarrera));

        forma.setFrame(-orientacion * 27.0 - 5.0, -66.0, 10.0, 13.0);
        g.setColor(SOMBRA_PIEL);
        g.fill(forma);
        g.setColor(CONTORNO);
        g.draw(forma);
        g.setColor(PIEL);
        g.fill(CABEZA);
        g.setColor(CABELLO);
        g.fill(GORRA_CABELLO);
        g.setColor(equipo[2]);
        g.fill(CINTA);
        g.setPaint(VOLUMEN_CABEZA);
        g.fill(CABEZA);
        g.setColor(CONTORNO);
        g.draw(CABEZA);

        double cara = orientacion * DESPLAZAMIENTO_CARA;
        boolean parpadeando = reloj % PARPADEO_CADA < SEGUNDOS_PARPADEO;
        paintEye(g, cara - 10.0, parpadeando);
        paintEye(g, cara + 10.0, parpadeando);
        g.setColor(CONTORNO);
        boca.setArc(cara - 8.0 - 3.0 * impulsoPatada, -52.0 - 3.0 * impulsoPatada, 16.0 + 6.0 * impulsoPatada,
                9.0 + 6.0 * impulsoPatada, 200.0, 140.0, Arc2D.CHORD);
        g.fill(boca);
        g.setTransform(cuerpo);
    }

    /**
     * Ojo que sigue al balon; al parpadear queda como una linea, y con el poder
     * armado frunce la ceja.
     */
    private void paintEye(Graphics2D g, double x, boolean parpadeando) {
        g.setColor(CONTORNO);
        double ceja = armado ? 2.5 * Math.signum(x - orientacion * DESPLAZAMIENTO_CARA) : 0.0;
        trazo.setLine(x - 6.0, -72.0 + ceja, x + 6.0, -72.0 - ceja);
        g.draw(trazo);
        if (parpadeando) {
            trazo.setLine(x - 5.0, -62.0, x + 5.0, -62.0);
            g.draw(trazo);
        } else {
            forma.setFrame(x - 6.5, -69.5, 13.0, 15.0);
            g.setColor(Color.WHITE);
            g.fill(forma);
            g.setColor(CONTORNO);
            g.draw(forma);
            forma.setFrame(x - 3.0 + miradaX, -65.0 + miradaY, 6.0, 6.0);
            g.fill(forma);
        }
    }
}
