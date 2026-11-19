package presentacion;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RadialGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;

/**
 * Balon animado: gira segun lo que avanza, deja estela cuando va rapido, se
 * aplasta al botar y proyecta una sombra que se encoge con la altura.
 *
 * Se dibuja en coordenadas nominales (radio 15) y se escala al radio real.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class BallSprite implements Actor {

    private static final double RADIO_NOMINAL = 15.0;
    private static final int ESTELA = 6;
    private static final double VELOCIDAD_ESTELA = 520.0;
    private static final double PIXELES_TELETRANSPORTE = 120.0;
    private static final double VELOCIDAD_BOTE = 160.0;
    private static final double CANTIDAD_APLASTAMIENTO = 0.22;
    private static final double RECUPERACION_APLASTAMIENTO = 14.0;
    private static final double ALTURA_DESVANECIMIENTO_SOMBRA = 380.0;
    private static final double ALTURA_POLVO = 60.0;
    private static final int CANTIDAD_POLVO = 5;
    private static final double VELOCIDAD_POLVO = 90.0;
    private static final int PARCHES = 5;

    private static final Color CUERO = new Color(248, 248, 244);
    private static final Color PARCHE = new Color(26, 26, 30);
    private static final Color SOMBRA = new Color(0, 0, 0, 95);
    private static final Color POLVO = new Color(176, 170, 160);
    private static final Color[] COLORES_ESTELA = { new Color(255, 255, 255, 70), new Color(255, 255, 255, 56),
            new Color(255, 255, 255, 42), new Color(255, 255, 255, 30), new Color(255, 255, 255, 20),
            new Color(255, 255, 255, 10) };
    private static final BasicStroke LINEA = new BasicStroke(1.4f);
    private static final Ellipse2D ESFERA = new Ellipse2D.Double(-15.0, -15.0, 30.0, 30.0);
    private static final Polygon PARCHE_CENTRAL = new Polygon(new int[] { 0, 5, 3, -3, -5 },
            new int[] { -5, -2, 4, 4, -2 }, 5);
    private static final Polygon PARCHE_BORDE=new Polygon(new int[]{-4,4,6,-6},new int[]{-10,-10,-14,-14},
            4);
    private static final RadialGradientPaint VOLUMEN = new RadialGradientPaint(new Point2D.Double(-5.0, -6.0), 22f,
            new float[] { 0f, 0.4f, 1f },
            new Color[] { new Color(255, 255, 255, 150), new Color(255, 255, 255, 0), new Color(0, 0, 0, 120) });

    private final HeadSoccer modelo;
    private final Particles particulas;
    private final double escala;
    private final double suelo;
    private final double[] estelaX = new double[ESTELA];
    private final double[] estelaY = new double[ESTELA];
    private final Ellipse2D.Double forma = new Ellipse2D.Double();

    private double x;
    private double y;
    private double velocidadY;
    private double rapidez;
    private double angulo;
    private double aplastamiento;

    /**
     * @param modelo     Fachada del dominio.
     * @param particulas Particulas de la cancha, para el polvo de los botes.
     */
    BallSprite(HeadSoccer modelo, Particles particulas) {
        this.modelo = modelo;
        this.particulas = particulas;
        this.escala = modelo.obtenerRadioBalon() / RADIO_NOMINAL;
        this.suelo = modelo.obtenerSueloY();
    }

    @Override
    public void actualizar(double segundos) throws POOBSoccerException {
        double[] posicion = modelo.obtenerPosicionBalon();
        double desplazamientoX = posicion[0] - x;
        double desplazamientoY = posicion[1] - y;
        boolean saltoBrusco = Math.hypot(desplazamientoX, desplazamientoY) > PIXELES_TELETRANSPORTE || segundos <= 0.0;
        double nuevaVelocidadY = saltoBrusco ? 0.0 : desplazamientoY / segundos;

        for (int i = ESTELA - 1; i > 0; i--) {
            estelaX[i] = saltoBrusco ? posicion[0] : estelaX[i - 1];
            estelaY[i] = saltoBrusco ? posicion[1] : estelaY[i - 1];
        }
        estelaX[0] = saltoBrusco ? posicion[0] : x;
        estelaY[0] = saltoBrusco ? posicion[1] : y;
        if (!saltoBrusco) {
            angulo += desplazamientoX / (RADIO_NOMINAL * escala);
        }
        if (velocidadY > VELOCIDAD_BOTE && nuevaVelocidadY < 0.0) {
            aplastamiento = 1.0;
            if (posicion[1] > suelo - ALTURA_POLVO) {
                particulas.lanzarRafaga(posicion[0], suelo, POLVO, CANTIDAD_POLVO, VELOCIDAD_POLVO);
            }
        }
        aplastamiento *= Math.exp(-RECUPERACION_APLASTAMIENTO * segundos);
        rapidez = saltoBrusco ? 0.0 : Math.hypot(desplazamientoX, desplazamientoY) / segundos;
        velocidadY = nuevaVelocidadY;
        x = posicion[0];
        y = posicion[1];
    }

    @Override
    public void dibujar(Graphics2D g) {
        double radio = RADIO_NOMINAL * escala;
        double atenuacion = Math.max(0.3, 1.0 - (suelo - radio - y) / ALTURA_DESVANECIMIENTO_SOMBRA);
        forma.setFrame(x - radio * atenuacion, suelo - 2.0, 2.0 * radio * atenuacion, 8.0);
        g.setColor(SOMBRA);
        g.fill(forma);
        if (rapidez > VELOCIDAD_ESTELA) {
            for (int i = ESTELA - 1; i >= 0; i--) {
                double tamano = radio * (1.0 - i / (double) (ESTELA + 1));
                forma.setFrame(estelaX[i] - tamano, estelaY[i] - tamano, 2.0 * tamano, 2.0 * tamano);
                g.setColor(COLORES_ESTELA[i]);
                g.fill(forma);
            }
        }

        AffineTransform mundo = g.getTransform();
        g.translate(x, y);
        g.scale(escala * (1.0 + aplastamiento * CANTIDAD_APLASTAMIENTO),
                escala * (1.0 - aplastamiento * CANTIDAD_APLASTAMIENTO));
        AffineTransform vertical = g.getTransform();
        g.rotate(angulo);
        g.setColor(CUERO);
        g.fill(ESFERA);
        g.setColor(PARCHE);
        g.fill(PARCHE_CENTRAL);
        for (int i = 0; i < PARCHES; i++) {
            g.fill(PARCHE_BORDE);
            g.rotate(2.0 * Math.PI / PARCHES);
        }
        g.setTransform(vertical);
        g.setPaint(VOLUMEN);
        g.fill(ESFERA);
        g.setColor(PARCHE);
        g.setStroke(LINEA);
        g.draw(ESFERA);
        g.setTransform(mundo);
    }
}
