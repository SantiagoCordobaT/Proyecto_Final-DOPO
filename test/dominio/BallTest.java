package dominio;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import dominio.excepciones.POOBSoccerException;
import dominio.personajes.Character;
import dominio.poderes.FreezePower;

/**
 * Pruebas de la física continua del balón, arcos y límites de la cancha.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class BallTest {

    private static final double DT = 1.0 / 60.0;

    private static void simularBalon(Field cancha, Ball balon, double segundos) {
        int pasos = (int) Math.round(segundos / DT);
        for (int i = 0; i < pasos; i++) {
            cancha.actualizarBalon(balon, DT);
            cancha.mantenerBalonDentro(balon);
        }
    }

    private static Ball balonEn(double x, double y) {
        Ball b = new Ball();
        b.ubicarEn(x, y);
        return b;
    }

    @Test
    public void fronteraExactaDeGolEnAmbosArcos() {
        Field cancha = new Field();
        double yDentro = Field.SUELO_Y - 40.0;

        // A 1 píxel de entrar por completo en el arco izquierdo: todavía no es gol
        assertEquals(Field.SIN_GOL, cancha.anotadorDe(balonEn(Goal.ANCHO - Ball.RADIO + 1.0, yDentro)));
        // Justo completamente dentro del arco izquierdo: gol para jugador 2
        assertEquals(2, cancha.anotadorDe(balonEn(Goal.ANCHO - Ball.RADIO, yDentro)));

        // Lo mismo en el arco derecho (gol para jugador 1)
        double bocaDerecha = Field.ANCHO - Goal.ANCHO;
        assertEquals(Field.SIN_GOL, cancha.anotadorDe(balonEn(bocaDerecha + Ball.RADIO - 1.0, yDentro)));
        assertEquals(1, cancha.anotadorDe(balonEn(bocaDerecha + Ball.RADIO, yDentro)));

        // Encima del travesaño aunque esté en la zona horizontal del arco: no es gol
        double encimaTravesano = Field.SUELO_Y - Goal.ALTO - Ball.RADIO;
        assertEquals(Field.SIN_GOL, cancha.anotadorDe(balonEn(40.0, encimaTravesano)));
    }

    @Test
    public void reboteEnLasTresCarasDelTravesano() {
        double arriba = Field.SUELO_Y - Goal.ALTO;
        double abajo = arriba + Goal.GROSOR_BORDE;
        Goal arcoDerecho = new Goal(Field.ANCHO, Field.ANCHO - Goal.ANCHO, 1);

        // Cara superior: queda encima del travesaño
        Ball porEncima = balonEn(Field.ANCHO - 30.0, arriba - Ball.RADIO + 2.0);
        arcoDerecho.resolverBorde(porEncima);
        assertEquals(arriba - Ball.RADIO, porEncima.obtenerY(), 0.001);

        // Cara inferior: queda debajo del travesaño
        Ball porDebajo = balonEn(Field.ANCHO - 30.0, abajo + Ball.RADIO - 2.0);
        arcoDerecho.resolverBorde(porDebajo);
        assertEquals(abajo + Ball.RADIO, porDebajo.obtenerY(), 0.001);

        // Punta frontal del arco derecho: rebota hacia la izquierda (boca - radio)
        double boca = Field.ANCHO - Goal.ANCHO;
        Ball frontal = balonEn(boca - Ball.RADIO + 4.0, arriba + Goal.GROSOR_BORDE / 2.0);
        arcoDerecho.resolverBorde(frontal);
        assertEquals(boca - Ball.RADIO, frontal.obtenerX(), 0.001);
    }

    @Test
    public void patadasConDistintaFuerzaYAlturaRespetanLimitesDeCancha() {
        Field cancha = new Field();
        for (int fuerza = 1; fuerza <= 5; fuerza++) {
            Ball raso = balonEn(150.0, Field.SUELO_Y - Ball.RADIO);
            Ball elevado = balonEn(150.0, Field.SUELO_Y - Ball.RADIO);
            raso.patear(fuerza, 1);
            elevado.patearArriba(fuerza, 1);

            // Al cabo de 0.2 s el tiro elevado va más alto y menos adelantado
            simularBalon(cancha, raso, 0.2);
            simularBalon(cancha, elevado, 0.2);
            assertTrue(elevado.obtenerY() < raso.obtenerY());
            assertTrue(elevado.obtenerX() < raso.obtenerX());

            // Después de 2 s ninguno se sale de la cancha
            simularBalon(cancha, raso, 2.0);
            simularBalon(cancha, elevado, 2.0);
            assertTrue(raso.obtenerX() >= Ball.RADIO && raso.obtenerX() <= Field.ANCHO - Ball.RADIO);
            assertTrue(raso.obtenerY() >= Ball.RADIO && raso.obtenerY() <= Field.SUELO_Y - Ball.RADIO);
            assertTrue(elevado.obtenerY() >= Ball.RADIO && elevado.obtenerY() <= Field.SUELO_Y - Ball.RADIO);
        }
    }

    @Test
    public void reboteContraParedesInvierteSentidoHorizontal() {
        Field cancha = new Field();
        // Contra pared izquierda
        Ball haciaIzquierda = balonEn(40.0, 200.0);
        haciaIzquierda.patear(4, -1);
        simularBalon(cancha, haciaIzquierda, 0.5);
        assertTrue(haciaIzquierda.obtenerX() > 40.0);

        // Contra pared derecha
        Ball haciaDerecha = balonEn(Field.ANCHO - 40.0, 200.0);
        haciaDerecha.patear(4, 1);
        simularBalon(cancha, haciaDerecha, 0.5);
        assertTrue(haciaDerecha.obtenerX() < Field.ANCHO - 40.0);
    }

    @Test
    public void gravedadAsientaElBalonEnElSueloYTechoFrenaSubida() {
        Field cancha = new Field();
        Ball balon = new Ball();
        cancha.soltarBalon(balon);
        assertEquals(Field.ANCHO / 2.0, balon.obtenerX(), 0.001);

        simularBalon(cancha, balon, 4.0);
        assertEquals(Field.SUELO_Y - Ball.RADIO, balon.obtenerY(), 0.001);

        // Tiro fuerte hacia arriba pegado al techo nunca cruza y < RADIO
        balon.ubicarEn(450.0, 30.0);
        balon.patearArriba(5, 1);
        for (int i = 0; i < 15; i++) {
            cancha.actualizarBalon(balon, DT);
            assertTrue(balon.obtenerY() >= Ball.RADIO);
        }
    }

    @Test
    public void golpeFuerteAturdeAlRivalYElBalonSeFrena() throws POOBSoccerException {
        ContactResolver contactos = new ContactResolver();
        Character pateador = new Character("Jacob", "Canadá", 3, 3, 5, 3, new FreezePower());
        Character receptor = new Character("Kenji", "Japón", 5, 4, 2, 2, new FreezePower());
        pateador.entrarEn(200.0, 1);
        receptor.entrarEn(500.0, -1);

        Ball balon = balonEn(240.0, pateador.obtenerCentroY());
        pateador.solicitarPatada();
        contactos.resolver(balon, pateador, receptor);
        assertTrue(balon.esGolpeFuerte());

        // El balón llega al cuerpo del rival
        balon.ubicarEn(500.0, receptor.obtenerCentroY());
        balon.patear(5, 1);
        contactos.resolver(balon, pateador, receptor);
        assertEquals(1, receptor.obtenerNombresEfectos().length);
        assertEquals("Aturdido", receptor.obtenerNombresEfectos()[0]);
        assertFalse(balon.esGolpeFuerte());
    }
}
