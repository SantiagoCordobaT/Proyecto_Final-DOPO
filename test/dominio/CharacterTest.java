package dominio;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import dominio.efectos.ConfusionEffect;
import dominio.efectos.FreezeEffect;
import dominio.efectos.ShrinkEffect;
import dominio.efectos.StunEffect;
import dominio.excepciones.POOBSoccerException;
import dominio.personajes.Character;
import dominio.personajes.CharacterCatalog;
import dominio.poderes.FreezePower;

/**
 * Pruebas de reglas de personajes: estadísticas, saltos, patadas, cabezazos,
 * efectos combinados y poderes.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class CharacterTest {

    private static final double DT = 1.0 / 60.0;

    private static void avanzarPersonaje(Character p, double segundos) {
        int pasos = (int) Math.round(segundos / DT);
        for (int i = 0; i < pasos; i++) {
            p.actualizar(DT);
        }
    }

    @Test
    public void todosLosPersonajesDelCatalogoSaltanYAterrizanEnElSuelo() throws POOBSoccerException {
        CharacterCatalog catalogo = new CharacterCatalog();
        for (int i = 0; i < catalogo.cantidad(); i++) {
            Character p = catalogo.obtener(i).copiar();
            p.entrarEn(300.0, 1);
            double bordeEnSuelo = p.obtenerBordeSuperior();

            p.saltar();
            avanzarPersonaje(p, 0.2);
            assertTrue(p.obtenerBordeSuperior() < bordeEnSuelo);

            avanzarPersonaje(p, 1.5);
            assertEquals(bordeEnSuelo, p.obtenerBordeSuperior(), 0.001);
        }
    }

    @Test
    public void estadisticasInvalidasYNombreVacioSonRechazados() {
        assertEquals(POOBSoccerException.ESTADISTICAS_FUERA_DE_RANGO,
                assertThrows(POOBSoccerException.class,
                        () -> new Character("X", "CO", 0, 3, 3, 3, new FreezePower())).getMessage());
        assertThrows(POOBSoccerException.class, () -> new Character("X", "CO", 3, 6, 3, 3, new FreezePower()));
        assertThrows(POOBSoccerException.class, () -> new Character("X", "CO", 3, 3, 0, 3, new FreezePower()));
        assertThrows(POOBSoccerException.class, () -> new Character("X", "CO", 3, 3, 3, 7, new FreezePower()));
        assertEquals(POOBSoccerException.PERSONAJE_INVALIDO,
                assertThrows(POOBSoccerException.class,
                        () -> new Character("  ", "CO", 3, 3, 3, 3, new FreezePower())).getMessage());
        assertThrows(POOBSoccerException.class, () -> new Character("X", "CO", 3, 3, 3, 3, null));
    }

    @Test
    public void reduccionBajaLaZonaDeCabezaYCambiaElCabezazo() throws POOBSoccerException {
        Field cancha = new Field();
        Character p = new Character("Rafael", "Brasil", 4, 5, 3, 3, new FreezePower());
        p.entrarEn(450.0, 1);

        // A altura SUELO_Y - 60, en tamaño normal (alto 90) choca de costado, no por
        // encima de la cabeza
        Ball aMediaAltura = new Ball();
        aMediaAltura.ubicarEn(450.0, Field.SUELO_Y - 60.0);
        assertFalse(aMediaAltura.estaPorEncimaDe(p.obtenerBordeSuperior()));

        // Al encogerlo a la mitad (alto 45), esa misma altura queda por encima de su
        // cabeza
        p.agregarEfecto(new ShrinkEffect());
        assertEquals(30.0, p.obtenerLimites()[2], 0.001);
        assertEquals(45.0, p.obtenerLimites()[3], 0.001);
        assertTrue(aMediaAltura.estaPorEncimaDe(p.obtenerBordeSuperior()));

        p.empujar(aMediaAltura);
        cancha.actualizarBalon(aMediaAltura, 0.1);
        assertTrue(aMediaAltura.obtenerY() < p.obtenerBordeSuperior() - Ball.RADIO);
        assertTrue(aMediaAltura.obtenerX() > 450.0);
    }

    @Test
    public void confusionEnElAireInvierteDesplazamientoYSigueAterrizando() throws POOBSoccerException {
        Character p = new Character("Mateo", "Colombia", 4, 4, 3, 4, new FreezePower());
        p.entrarEn(450.0, 1);

        p.saltar();
        avanzarPersonaje(p, 0.15);
        double xAntesDeConfundir = p.obtenerX();
        assertTrue(p.obtenerBordeSuperior() < Field.SUELO_Y - 90.0);

        // Recibe confusión en pleno salto e intenta ir a la derecha (+1)
        p.agregarEfecto(new ConfusionEffect());
        p.establecerDireccionMovimiento(1);
        avanzarPersonaje(p, 0.25);
        assertTrue(p.obtenerX() < xAntesDeConfundir);

        // Termina de caer al suelo normalmente
        avanzarPersonaje(p, 1.2);
        assertEquals(Field.SUELO_Y, p.obtenerLimites()[1] + p.obtenerLimites()[3], 0.001);
    }

    @Test
    public void dosPoderesIgualesSeguidosReinicianLaDuracionSinDuplicarse() throws POOBSoccerException {
        Character victima = new Character("Kenji", "Japón", 5, 4, 2, 2, new FreezePower());
        victima.entrarEn(500.0, -1);

        victima.agregarEfecto(new StunEffect());
        avanzarPersonaje(victima, 2.5);
        assertEquals(1, victima.obtenerNombresEfectos().length);

        // Se vuelve a aplicar antes de expirar: debe reiniciar a 3 s completos y seguir
        // siendo uno solo
        victima.agregarEfecto(new StunEffect());
        assertEquals(1, victima.obtenerNombresEfectos().length);

        // 1 s después (3.5 s desde el primero) todavía debe seguir aturdido
        avanzarPersonaje(victima, 1.0);
        assertTrue(Arrays.asList(victima.obtenerNombresEfectos()).contains("Aturdido"));

        // 2.2 s más (3.2 s desde el segundo) ya expiró
        avanzarPersonaje(victima, 2.2);
        assertEquals(0, victima.obtenerNombresEfectos().length);
    }

    @Test
    public void cabezazoAturdidoRebotaArribaSinAvanzarHorizontalmente() throws POOBSoccerException {
        Field cancha = new Field();
        Character p = new Character("Jacob", "Canadá", 3, 3, 4, 3, new FreezePower());
        p.entrarEn(420.0, 1);
        p.aturdir();

        Ball balon = new Ball();
        balon.ubicarEn(420.0, p.obtenerBordeSuperior() - 3.0);
        p.empujar(balon);
        cancha.actualizarBalon(balon, 0.05);

        assertTrue(balon.obtenerY() < p.obtenerBordeSuperior() - Ball.RADIO);
        assertEquals(420.0, balon.obtenerX(), 0.001);
    }

    @Test
    public void congelamientoImpideMoverSaltarYPatearHastaExpirar() throws POOBSoccerException {
        Character p = new Character("Jacob", "Canadá", 3, 3, 4, 3, new FreezePower());
        Character rival = new Character("Kenji", "Japón", 5, 4, 2, 2, new FreezePower());
        p.entrarEn(350.0, 1);
        rival.entrarEn(700.0, -1);

        p.agregarEfecto(new FreezeEffect());
        p.establecerDireccionMovimiento(1);
        p.saltar();
        p.solicitarPatada();

        Ball cerca = new Ball();
        cerca.ubicarEn(390.0, p.obtenerCentroY());
        assertFalse(p.intentarPatear(cerca, rival));

        avanzarPersonaje(p, 2.8);
        assertEquals(350.0, p.obtenerX(), 0.001);
        assertEquals(Field.SUELO_Y, p.obtenerLimites()[1] + p.obtenerLimites()[3], 0.001);

        // Al pasar los 3 s vuelve a responder
        avanzarPersonaje(p, 0.4);
        assertEquals(0, p.obtenerNombresEfectos().length);
        assertTrue(p.obtenerX() > 350.0);
    }

    @Test
    public void patadaCuerpoACuerpoAturdeYActivaTiempoDePatada() throws POOBSoccerException {
        Character a = new Character("A", "Canadá", 3, 3, 3, 3, new FreezePower());
        Character b = new Character("B", "Japón", 3, 3, 3, 3, new FreezePower());
        a.entrarEn(400.0, 1);
        b.entrarEn(460.0, -1);

        Ball lejano = new Ball();
        lejano.ubicarEn(800.0, 100.0);

        a.solicitarPatada();
        assertFalse(a.intentarPatear(lejano, b));
        assertTrue(a.estaPateando());
        assertEquals("Aturdido", b.obtenerNombresEfectos()[0]);

        avanzarPersonaje(a, 0.5);
        assertFalse(a.estaPateando());
    }

    @Test
    public void poderArmadoDisparaSobreElRivalYVaciaLaBarra() throws POOBSoccerException {
        CharacterCatalog catalogo = new CharacterCatalog();
        Character j1 = catalogo.obtener(0).copiar(); // Jacob: Congelamiento
        Character j2 = catalogo.obtener(1).copiar();
        j1.entrarEn(300.0, 1);
        j2.entrarEn(700.0, -1);

        // Con la barra a medias no se arma
        j1.actualizar(5.0);
        j1.armarPoder();
        assertFalse(j1.estaPoderArmado());

        // Llena al 100 % se arma y se dispara al patear el balón
        j1.actualizar(15.0);
        j1.armarPoder();
        assertTrue(j1.estaPoderArmado());

        Ball balon = new Ball();
        balon.ubicarEn(335.0, j1.obtenerCentroY());
        j1.solicitarPatada();
        assertTrue(j1.intentarPatear(balon, j2));
        assertFalse(j1.estaPoderArmado());
        assertEquals(0.0, j1.obtenerCargaPoder(), 0.001);
        assertEquals("Congelado", j2.obtenerNombresEfectos()[0]);
    }

    @Test
    public void copiaIndependienteYReinicioConservaGolesYPoder() throws POOBSoccerException {
        Character original = new Character("Mateo", "Colombia", 4, 3, 3, 4, new FreezePower());
        Character copia = original.copiar();
        copia.entrarEn(250.0, 1);
        copia.sumarGol();
        copia.aturdir();
        copia.actualizar(1.0);

        assertEquals(0, original.obtenerGoles());
        assertEquals(0, original.obtenerNombresEfectos().length);

        copia.reiniciarJugada();
        assertEquals(250.0, copia.obtenerX(), 0.001);
        assertEquals(0, copia.obtenerNombresEfectos().length);
        assertEquals(1, copia.obtenerGoles());
        assertTrue(copia.obtenerCargaPoder() > 0.0);
    }
}
