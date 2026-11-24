package dominio;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import dominio.excepciones.POOBSoccerException;
import dominio.personajes.Character;
import dominio.poderes.ConfusionPower;
import dominio.poderes.FreezePower;
import dominio.poderes.ShrinkPower;
import dominio.poderes.StunPower;

/**
 * Pruebas del comportamiento de Character: estadísticas, saltos, patadas,
 * cabezazos y efectos de estado.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class CharacterTest {

    private static boolean contiene(String[] arreglo, String texto) {
        return Arrays.asList(arreglo).contains(texto);
    }

    @Test
    public void estadisticasEnRangoValido() throws POOBSoccerException {
        Character p = new Character("Ana", "Colombia", 1, 3, 5, 2, new FreezePower());
        assertEquals(1, p.obtenerVelocidad());
        assertEquals(3, p.obtenerSalto());
        assertEquals(5, p.obtenerFuerza());
        assertEquals(2, p.obtenerNivelPoder());
    }

    @Test
    public void estadisticasFueraDeRangoLanzanExcepcion() {
        POOBSoccerException ex = assertThrows(POOBSoccerException.class,
                () -> new Character("Ana", "Colombia", 0, 3, 3, 3, new FreezePower()));
        assertEquals(POOBSoccerException.ESTADISTICAS_FUERA_DE_RANGO, ex.getMessage());
        assertThrows(POOBSoccerException.class, () -> new Character("Ana", "Colombia", 3, 6, 3, 3, new FreezePower()));
        assertThrows(POOBSoccerException.class, () -> new Character("Ana", "Colombia", 3, 3, -1, 3, new FreezePower()));
        assertThrows(POOBSoccerException.class, () -> new Character("Ana", "Colombia", 3, 3, 3, 6, new FreezePower()));
        assertThrows(POOBSoccerException.class, () -> new Character(" ", "Colombia", 3, 3, 3, 3, new FreezePower()));
    }

    @Test
    public void copiaNoComparteGolesEfectosNiCarga() throws POOBSoccerException {
        Character base = new Character("Jacob", "Canadá", 3, 3, 4, 3, new FreezePower());
        Character copia = base.copiar();
        assertNotSame(base, copia);

        copia.aturdir();
        copia.sumarGol();
        copia.actualizar(1.0);

        assertEquals(0, base.obtenerNombresEfectos().length);
        assertEquals(0, base.obtenerGoles());
        assertEquals(0.0, base.obtenerCargaPoder(), 0.001);
        assertTrue(copia.obtenerCargaPoder() > 0.0);
    }

    @Test
    public void saltoSubeSegunEstadisticaYVuelveAlSuelo() throws POOBSoccerException {
        Character bajo = new Character("Bajo", "Canadá", 3, 1, 3, 3, new FreezePower());
        Character alto = new Character("Alto", "Brasil", 3, 5, 3, 3, new ShrinkPower());
        bajo.entrarEn(200.0, 1);
        alto.entrarEn(700.0, -1);

        bajo.saltar();
        alto.saltar();
        double topeBajo = bajo.obtenerBordeSuperior();
        double topeAlto = alto.obtenerBordeSuperior();
        for (int i = 0; i < 90; i++) {
            bajo.actualizar(1.0 / 60.0);
            alto.actualizar(1.0 / 60.0);
            topeBajo = Math.min(topeBajo, bajo.obtenerBordeSuperior());
            topeAlto = Math.min(topeAlto, alto.obtenerBordeSuperior());
        }
        assertTrue(topeAlto < topeBajo);
        assertEquals(Field.SUELO_Y, bajo.obtenerLimites()[1] + bajo.obtenerLimites()[3], 0.001);
        assertEquals(Field.SUELO_Y, alto.obtenerLimites()[1] + alto.obtenerLimites()[3], 0.001);
    }

    @Test
    public void entrarEnYReiniciarJugada() throws POOBSoccerException {
        Character p = new Character("Mateo", "Colombia", 4, 3, 3, 4, new ConfusionPower());
        p.entrarEn(300.0, 1);
        p.establecerDireccionMovimiento(1);
        p.actualizar(0.5);
        p.aturdir();
        p.sumarGol();

        p.reiniciarJugada();
        assertEquals(300.0, p.obtenerX(), 0.001);
        assertEquals(0, p.obtenerNombresEfectos().length);
        assertEquals(1, p.obtenerGoles());
        assertTrue(p.obtenerCargaPoder() > 0.0);

        p.entrarEn(700.0, -1);
        assertEquals(700.0, p.obtenerX(), 0.001);
        assertEquals(-1, p.obtenerOrientacion());
        assertEquals(0, p.obtenerGoles());
        assertEquals(0.0, p.obtenerCargaPoder(), 0.001);
    }

    @Test
    public void patadaAlBalonYDuracionVisual() throws POOBSoccerException {
        Character pateador = new Character("A", "Canadá", 3, 3, 3, 3, new FreezePower());
        Character rival = new Character("B", "Japón", 3, 3, 3, 3, new StunPower());
        pateador.entrarEn(400.0, 1);
        rival.entrarEn(700.0, -1);

        Ball balon = new Ball();
        balon.ubicarEn(440.0, pateador.obtenerCentroY());

        assertFalse(pateador.estaPateando());
        pateador.solicitarPatada();
        assertTrue(pateador.intentarPatear(balon, rival));
        assertTrue(pateador.estaPateando());

        pateador.actualizar(0.5);
        assertFalse(pateador.estaPateando());
    }

    @Test
    public void patadaCuerpoACuerpoAturdeTresSegundos() throws POOBSoccerException {
        Character atacante = new Character("A", "Canadá", 3, 3, 3, 3, new FreezePower());
        Character rival = new Character("B", "Japón", 3, 3, 3, 3, new StunPower());
        atacante.entrarEn(400.0, 1);
        rival.entrarEn(465.0, -1);

        Ball balonLejos = new Ball();
        balonLejos.ubicarEn(800.0, 50.0);

        atacante.solicitarPatada();
        assertFalse(atacante.intentarPatear(balonLejos, rival));
        assertTrue(contiene(rival.obtenerNombresEfectos(), "Aturdido"));

        rival.actualizar(2.9);
        assertTrue(contiene(rival.obtenerNombresEfectos(), "Aturdido"));
        rival.actualizar(0.2);
        assertEquals(0, rival.obtenerNombresEfectos().length);
    }

    @Test
    public void cabezazoNormalImpulsaHaciaAdelante() throws POOBSoccerException {
        Field cancha = new Field();
        Character p = new Character("A", "Canadá", 3, 3, 3, 3, new FreezePower());
        p.entrarEn(400.0, 1);

        Ball balon = new Ball();
        balon.ubicarEn(400.0, p.obtenerBordeSuperior() - 2.0);
        assertTrue(p.toca(balon));
        p.empujar(balon);
        for (int i = 0; i < 10; i++) {
            cancha.actualizarBalon(balon);
        }
        assertTrue(balon.obtenerY() < p.obtenerBordeSuperior() - Ball.RADIO);
        assertTrue(balon.obtenerX() > 400.0);
    }

    @Test
    public void balonContraCabezaDeJugadorAturdidoNoPateaAdelante() throws POOBSoccerException {
        Field cancha = new Field();
        Character p = new Character("A", "Canadá", 3, 3, 3, 3, new FreezePower());
        p.entrarEn(400.0, 1);
        p.aturdir();

        Ball balon = new Ball();
        balon.ubicarEn(400.0, p.obtenerBordeSuperior() - 2.0);
        p.empujar(balon);
        cancha.actualizarBalon(balon);

        assertTrue(balon.obtenerY() < p.obtenerBordeSuperior() - Ball.RADIO);
        assertEquals(400.0, balon.obtenerX(), 0.001);
    }

    @Test
    public void efectoCongelamientoBloqueaAcciones() throws POOBSoccerException {
        Character victima = new Character("B", "Canadá", 3, 3, 3, 3, new FreezePower());
        Character rival = new Character("C", "Japón", 3, 3, 3, 3, new StunPower());
        victima.entrarEn(400.0, 1);
        rival.entrarEn(700.0, -1);
        double bordeAntes = victima.obtenerBordeSuperior();

        new FreezePower().aplicarEfecto(victima);
        assertTrue(contiene(victima.obtenerNombresEfectos(), "Congelado"));

        Ball balon = new Ball();
        balon.ubicarEn(440.0, victima.obtenerCentroY());
        victima.solicitarPatada();
        assertFalse(victima.intentarPatear(balon, rival));

        victima.saltar();
        victima.establecerDireccionMovimiento(1);
        victima.actualizar(0.5);
        assertEquals(400.0, victima.obtenerX(), 0.001);
        assertEquals(bordeAntes, victima.obtenerBordeSuperior(), 0.001);
    }

    @Test
    public void efectoReduccionMitadDeTamano() throws POOBSoccerException {
        Character victima = new Character("B", "Brasil", 3, 3, 3, 3, new ShrinkPower());
        double anchoOriginal = victima.obtenerLimites()[2];
        new ShrinkPower().aplicarEfecto(victima);
        assertEquals(anchoOriginal / 2.0, victima.obtenerLimites()[2], 0.001);
    }

    @Test
    public void efectoConfusionInvierteDireccion() throws POOBSoccerException {
        Character victima = new Character("B", "Colombia", 3, 3, 3, 3, new ConfusionPower());
        victima.entrarEn(400.0, 1);
        new ConfusionPower().aplicarEfecto(victima);
        victima.establecerDireccionMovimiento(1);
        victima.actualizar(0.5);
        assertTrue(victima.obtenerX() < 400.0);
    }

    @Test
    public void efectoAturdimientoExpiraDespuesDeTresSegundos() throws POOBSoccerException {
        Character victima = new Character("B", "Japón", 3, 3, 3, 3, new StunPower());
        victima.entrarEn(400.0, 1);
        new StunPower().aplicarEfecto(victima);
        assertTrue(contiene(victima.obtenerNombresEfectos(), "Aturdido"));

        victima.establecerDireccionMovimiento(1);
        victima.actualizar(2.5);
        assertEquals(400.0, victima.obtenerX(), 0.001);

        victima.actualizar(0.6);
        assertEquals(0, victima.obtenerNombresEfectos().length);
        victima.actualizar(0.2);
        assertTrue(victima.obtenerX() > 400.0);
    }

    @Test
    public void poderArmadoSeDisparaAlPatearElBalon() throws POOBSoccerException {
        Character pateador = new Character("A", "Canadá", 3, 3, 3, 3, new FreezePower());
        Character rival = new Character("B", "Japón", 3, 3, 3, 3, new StunPower());
        pateador.entrarEn(400.0, 1);
        rival.entrarEn(700.0, -1);

        pateador.actualizar(17.0);
        pateador.armarPoder();
        assertTrue(pateador.estaPoderArmado());

        Ball balon = new Ball();
        balon.ubicarEn(440.0, pateador.obtenerCentroY());
        pateador.solicitarPatada();
        assertTrue(pateador.intentarPatear(balon, rival));
        assertTrue(contiene(rival.obtenerNombresEfectos(), "Congelado"));
        assertFalse(pateador.estaPoderArmado());
        assertEquals(0.0, pateador.obtenerCargaPoder(), 0.001);
    }
}
