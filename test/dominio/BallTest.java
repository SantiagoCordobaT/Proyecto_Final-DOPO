package dominio;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import dominio.excepciones.POOBSoccerException;
import dominio.personajes.Character;
import dominio.poderes.FreezePower;

/**
 * Pruebas de movimiento, rebotes y goles del balón en la cancha.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class BallTest {

    @Test
    public void elBalonCaeHastaElSueloYNoPasaDelTecho() {
        Ball balon = new Ball();
        balon.ubicarEn(450.0, 60.0);
        for (int i = 0; i < 120; i++) {
            balon.actualizarFisica(Field.SUELO_Y, 0.0);
        }
        assertEquals(Field.SUELO_Y - Ball.RADIO, balon.obtenerY(), 0.001);

        balon.ubicarEn(450.0, 20.0);
        balon.patearArriba(5, 1);
        for (int i = 0; i < 10; i++) {
            balon.actualizarFisica(Field.SUELO_Y, 0.0);
            assertTrue(balon.obtenerY() >= Ball.RADIO);
        }
    }

    @Test
    public void elBalonRebotaEnLasParedesLaterales() {
        Field cancha = new Field();
        Ball balon = new Ball();
        balon.ubicarEn(Field.ANCHO - 60.0, 120.0);
        balon.patear(5, 1);

        double maxX = 0.0;
        for (int i = 0; i < 120; i++) {
            cancha.actualizarBalon(balon);
            cancha.mantenerBalonDentro(balon);
            if (balon.obtenerX() > maxX) {
                maxX = balon.obtenerX();
            }
        }
        assertEquals(Field.ANCHO - Ball.RADIO, maxX, 0.001);
        assertTrue(balon.obtenerX() < Field.ANCHO - 200.0);
    }

    @Test
    public void patadaNormalVsPatadaAlta() {
        Field cancha = new Field();
        Ball normal = new Ball();
        normal.ubicarEn(200.0, 400.0);
        normal.patear(3, 1);

        Ball alta = new Ball();
        alta.ubicarEn(200.0, 400.0);
        alta.patearArriba(3, 1);

        double alturaNormal = 400.0;
        double alturaAlta = 400.0;
        for (int i = 0; i < 60; i++) {
            cancha.actualizarBalon(normal);
            cancha.actualizarBalon(alta);
            alturaNormal = Math.min(alturaNormal, normal.obtenerY());
            alturaAlta = Math.min(alturaAlta, alta.obtenerY());
        }
        assertTrue(alturaAlta < alturaNormal);
        assertTrue(alta.obtenerX() < normal.obtenerX());
    }

    @Test
    public void golSoloSiElBalonEstaCompletamenteDentroDelArco() {
        Goal arcoIzquierdo = new Goal(0.0, Goal.ANCHO, 2);
        Ball enLaLinea = new Ball();
        enLaLinea.ubicarEn(Goal.ANCHO, Field.SUELO_Y - Ball.RADIO - 5.0);
        assertFalse(arcoIzquierdo.contieneBalon(enLaLinea));

        Ball adentro = new Ball();
        adentro.ubicarEn(Goal.ANCHO - Ball.RADIO, Field.SUELO_Y - Ball.RADIO - 5.0);
        assertTrue(arcoIzquierdo.contieneBalon(adentro));
    }

    @Test
    public void canchaAcreditaGolAlAtacanteCorrecto() {
        Field cancha = new Field();
        Ball balon = new Ball();
        balon.ubicarEn(Goal.ANCHO / 2.0, Field.SUELO_Y - Ball.RADIO);
        assertEquals(2, cancha.anotadorDe(balon));

        balon.ubicarEn(Field.ANCHO - Goal.ANCHO / 2.0, Field.SUELO_Y - Ball.RADIO);
        assertEquals(1, cancha.anotadorDe(balon));

        cancha.soltarBalon(balon);
        assertEquals(Field.SIN_GOL, cancha.anotadorDe(balon));
        assertEquals(Field.ANCHO / 2.0, balon.obtenerX(), 0.001);
    }

    @Test
    public void reboteEncimaDelTravesano() {
        Goal arco = new Goal(0.0, Goal.ANCHO, 2);
        double arribaTravesano = Field.SUELO_Y - Goal.ALTO;
        Ball balon = new Ball();
        balon.ubicarEn(Goal.ANCHO / 2.0, arribaTravesano - Ball.RADIO + 3.0);
        arco.resolverBorde(balon);
        assertEquals(arribaTravesano - Ball.RADIO, balon.obtenerY(), 0.001);
    }

    @Test
    public void reboteDeFrenteEnLaPuntaDelTravesano() {
        Goal arco = new Goal(0.0, Goal.ANCHO, 2);
        double mitadTravesanoY = Field.SUELO_Y - Goal.ALTO + Goal.GROSOR_BORDE / 2.0;
        Ball balon = new Ball();
        balon.ubicarEn(Goal.ANCHO + 10.0, mitadTravesanoY);
        arco.resolverBorde(balon);
        assertEquals(Goal.ANCHO + Ball.RADIO, balon.obtenerX(), 0.001);
        assertEquals(mitadTravesanoY, balon.obtenerY(), 0.001);
    }

    @Test
    public void rebotePorDebajoDelTravesano() {
        Field cancha = new Field();
        Goal arco = new Goal(0.0, Goal.ANCHO, 2);
        double abajoTravesano = Field.SUELO_Y - Goal.ALTO + Goal.GROSOR_BORDE;
        Ball balon = new Ball();
        balon.ubicarEn(Goal.ANCHO / 2.0, abajoTravesano + Ball.RADIO - 3.0);
        arco.resolverBorde(balon);
        assertEquals(abajoTravesano + Ball.RADIO, balon.obtenerY(), 0.001);
        cancha.actualizarBalon(balon);
        assertTrue(balon.obtenerY() > abajoTravesano + Ball.RADIO);
    }

    @Test
    public void empujeContraParedMantieneBalonAdentro() throws POOBSoccerException {
        Field cancha = new Field();
        Character jugador = new Character("A", "Canadá", 3, 3, 3, 3, new FreezePower());
        jugador.entrarEn(30.0, 1);
        Ball balon = new Ball();
        balon.ubicarEn(Ball.RADIO, Field.SUELO_Y - Ball.RADIO);
        assertTrue(jugador.toca(balon));
        jugador.empujar(balon);
        cancha.mantenerBalonDentro(balon);
        assertEquals(Ball.RADIO, balon.obtenerX(), 0.001);
    }

    @Test
    public void frenarReduceImpulsoDeGolpeFuerte() {
        Ball balon = new Ball();
        balon.ubicarEn(300.0, 300.0);
        balon.patear(4, 1);
        assertTrue(balon.esGolpeFuerte());
        balon.frenar();
        assertFalse(balon.esGolpeFuerte());
    }
}
