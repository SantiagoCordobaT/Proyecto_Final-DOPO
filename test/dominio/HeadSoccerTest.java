package dominio;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import dominio.excepciones.POOBSoccerException;

/**
 * Pruebas de integración sobre la fachada HeadSoccer usando MatchSetup:
 * inicio, pausa, controles, IA, edición de personajes y simulación MvsM.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class HeadSoccerTest {

    private static final int PVSP = 0;
    private static final int PVSM = 1;
    private static final int MVSM = 2;
    private static final int GOLES_3 = 0;
    private static final int SUPERVIVENCIA = 3;
    private static final double DT = 1.0 / 60.0;

    private static void simularSegundos(HeadSoccer juego, double segundos) throws POOBSoccerException {
        int pasos = (int) Math.round(segundos / DT);
        for (int i = 0; i < pasos; i++) {
            juego.actualizarTick(DT);
        }
    }

    @Test
    public void instanciasIndependientesYEstadoAntesDeIniciar() throws POOBSoccerException {
        HeadSoccer juegoA = new HeadSoccer();
        HeadSoccer juegoB = new HeadSoccer();
        assertNotSame(juegoA, juegoB);
        assertFalse(juegoA.hayPartidoActivo());

        POOBSoccerException ex = assertThrows(POOBSoccerException.class, () -> juegoA.obtenerEstadoPartido());
        assertEquals(POOBSoccerException.PARTIDO_NO_INICIADO, ex.getMessage());
        assertThrows(POOBSoccerException.class, () -> juegoA.iniciarPartido(null));
    }

    @Test
    public void validarConfiguracionConMatchSetup() throws POOBSoccerException {
        HeadSoccer juego = new HeadSoccer();
        assertThrows(POOBSoccerException.class, () -> juego.iniciarPartido(new MatchSetup(-1, GOLES_3, 0, 1)));
        assertThrows(POOBSoccerException.class, () -> juego.iniciarPartido(new MatchSetup(PVSP, 99, 0, 1)));
        assertThrows(POOBSoccerException.class, () -> juego.iniciarPartido(new MatchSetup(PVSP, GOLES_3, 99, 1)));
        assertThrows(POOBSoccerException.class, () -> juego.validarModoYTipo(PVSP, SUPERVIVENCIA));

        juego.validarModoYTipo(PVSM, SUPERVIVENCIA);
        juego.iniciarPartido(new MatchSetup(PVSP, GOLES_3, 0, 1));
        assertTrue(juego.hayPartidoActivo());
        assertEquals("Meta: 3 goles | 00:00", juego.obtenerEstadoPartido());
    }

    @Test
    public void editarPersonajeAntesDeIniciarAplicaLosCambiosAlRival() throws POOBSoccerException {
        HeadSoccer juego = new HeadSoccer();
        int indice = juego.crearPersonaje("Diego", "Argentina", 4, 4, 3, 2, 0);
        juego.editarPersonaje(indice, "Lucas", "Perú", 5, 4, 4, 3, 2);

        juego.iniciarPartido(new MatchSetup(PVSP, GOLES_3, 0, indice));
        PlayerSummary rival = juego.obtenerResumenJugador(2);
        assertEquals("Lucas", rival.obtenerNombre());
        assertEquals("Perú", rival.obtenerPais());
        assertEquals(juego.obtenerNombresPoderes()[2], rival.obtenerNombrePoder());
    }

    @Test
    public void pausaCongelaElEncuentroYSoltarAccionesNoDetieneElReloj() throws POOBSoccerException {
        HeadSoccer juego = new HeadSoccer();
        juego.iniciarPartido(PVSP, GOLES_3, 0, 1);

        // Pausado: ni el balón ni el reloj se mueven, y las teclas activas se sueltan
        juego.presionarAccion(1, "RIGHT");
        juego.pausar();
        double[] balonAntes = juego.obtenerPosicionBalon();
        simularSegundos(juego, 1.0);
        assertArrayEquals(balonAntes, juego.obtenerPosicionBalon(), 0.001);
        assertEquals("Meta: 3 goles | 00:00", juego.obtenerEstadoPartido());

        // Al reanudar, el jugador 1 ya no avanza solo porque la tecla se soltó al pausar
        juego.reanudar();
        double xP1 = juego.obtenerResumenJugador(1).obtenerX();
        simularSegundos(juego, 0.2);
        assertEquals(xP1, juego.obtenerResumenJugador(1).obtenerX(), 0.001);

        // soltarTodasLasAcciones detiene a ambos pero el reloj sigue corriendo
        juego.presionarAccion(1, "RIGHT");
        juego.presionarAccion(2, "LEFT");
        juego.soltarTodasLasAcciones();
        double xP2 = juego.obtenerResumenJugador(2).obtenerX();
        simularSegundos(juego, 1.0);
        assertEquals(xP1, juego.obtenerResumenJugador(1).obtenerX(), 0.001);
        assertEquals(xP2, juego.obtenerResumenJugador(2).obtenerX(), 0.001);
        assertEquals("Meta: 3 goles | 00:01", juego.obtenerEstadoPartido());
    }

    @Test
    public void controlesHumanosYTeclasOpuestas() throws POOBSoccerException {
        HeadSoccer juego = new HeadSoccer();
        juego.iniciarPartido(PVSP, GOLES_3, 0, 1);

        double xInicial = juego.obtenerResumenJugador(1).obtenerX();
        juego.presionarAccion(1, "RIGHT");
        simularSegundos(juego, 0.1);
        double xMovido = juego.obtenerResumenJugador(1).obtenerX();
        assertTrue(xMovido > xInicial);

        juego.soltarAccion(1, "RIGHT");
        simularSegundos(juego, 0.1);
        assertEquals(xMovido, juego.obtenerResumenJugador(1).obtenerX(), 0.001);

        double ySuelo = juego.obtenerResumenJugador(1).obtenerY();
        juego.presionarAccion(1, "JUMP");
        simularSegundos(juego, 0.05);
        assertTrue(juego.obtenerResumenJugador(1).obtenerY() < ySuelo);

        double x2Antes = juego.obtenerResumenJugador(2).obtenerX();
        juego.presionarAccion(2, "LEFT");
        juego.presionarAccion(2, "RIGHT");
        simularSegundos(juego, 0.1);
        assertEquals(x2Antes, juego.obtenerResumenJugador(2).obtenerX(), 0.001);
    }

    @Test
    public void iaPersigueElBalonEIgnoraElTeclado() throws POOBSoccerException {
        HeadSoccer juego = new HeadSoccer();
        juego.iniciarPartido(PVSM, GOLES_3, 0, 1);

        double xInicialMaquina = juego.obtenerResumenJugador(2).obtenerX();
        // Aunque el usuario presione RIGHT para el jugador 2, la máquina persigue el balón hacia la izquierda
        juego.presionarAccion(2, "RIGHT");
        juego.presionarAccion(2, "POWER");
        simularSegundos(juego, 0.25);
        assertTrue(juego.obtenerResumenJugador(2).obtenerX() < xInicialMaquina);
    }

    @Test
    public void maquinaVsMaquinaAnotaAlMenosUnGolEnSesentaSegundos() throws POOBSoccerException {
        HeadSoccer juego = new HeadSoccer();
        juego.iniciarPartido(MVSM, GOLES_3, 0, 1);

        boolean huboGol = false;
        for (int i = 0; i < 60 * 60; i++) {
            juego.actualizarTick(DT);
            int total = juego.obtenerResumenJugador(1).obtenerGoles()
                    + juego.obtenerResumenJugador(2).obtenerGoles();
            if (total > 0 || juego.estaPartidoTerminado()) {
                huboGol = true;
                break;
            }
        }
        assertTrue(huboGol);
        double[] balon = juego.obtenerPosicionBalon();
        assertTrue(balon[0] >= 0.0 && balon[0] <= Field.ANCHO);
    }

    @Test
    public void barraDePoderYRevanchaDesdeFachada() throws POOBSoccerException {
        HeadSoccer juego = new HeadSoccer();
        MatchSetup config = new MatchSetup(PVSP, GOLES_3, 0, 1);
        juego.iniciarPartido(config);

        juego.presionarAccion(1, "POWER");
        juego.actualizarTick(DT);
        assertFalse(juego.obtenerResumenJugador(1).estaPoderArmado());

        juego.actualizarTick(17.0);
        juego.actualizarTick(DT);
        assertTrue(juego.obtenerResumenJugador(1).estaPoderArmado());
        assertEquals(1.0, juego.obtenerResumenJugador(1).obtenerCargaPoder(), 0.001);

        // Revancha con el mismo MatchSetup empieza en cero
        juego.iniciarPartido(config);
        assertEquals(0.0, juego.obtenerResumenJugador(1).obtenerCargaPoder(), 0.001);
        assertFalse(juego.obtenerResumenJugador(1).estaPoderArmado());
        assertEquals("Meta: 3 goles | 00:00", juego.obtenerEstadoPartido());
    }

    @Test
    public void accionesYConsultasInvalidasLanzanExcepcion() throws POOBSoccerException {
        HeadSoccer juego = new HeadSoccer();
        juego.iniciarPartido(PVSP, GOLES_3, 0, 1);

        assertEquals(POOBSoccerException.PARTIDO_EN_CURSO,
                assertThrows(POOBSoccerException.class, () -> juego.obtenerNombreGanador()).getMessage());
        assertEquals(POOBSoccerException.ACCION_INVALIDA,
                assertThrows(POOBSoccerException.class, () -> juego.presionarAccion(1, "VOLAR")).getMessage());
        assertEquals(POOBSoccerException.JUGADOR_INVALIDO,
                assertThrows(POOBSoccerException.class, () -> juego.presionarAccion(3, "KICK")).getMessage());
        assertEquals(POOBSoccerException.TIEMPO_INVALIDO,
                assertThrows(POOBSoccerException.class, () -> juego.actualizarTick(-0.5)).getMessage());
    }

    @Test
    public void medidasYResumenInicialDesdeFachada() throws POOBSoccerException {
        HeadSoccer juego = new HeadSoccer();
        juego.iniciarPartido(PVSP, GOLES_3, 0, 1);

        assertEquals(1, juego.obtenerResumenJugador(1).obtenerOrientacion());
        assertEquals(-1, juego.obtenerResumenJugador(2).obtenerOrientacion());
        assertFalse(juego.obtenerResumenJugador(1).estaPateando());
        assertEquals(Field.ANCHO, juego.obtenerAnchoCancha(), 0.001);
        assertEquals(Field.SUELO_Y, juego.obtenerSueloY(), 0.001);
        assertEquals(Ball.RADIO, juego.obtenerRadioBalon(), 0.001);
        assertEquals(Goal.GROSOR_BORDE, juego.obtenerGrosorTravesano(), 0.001);
        assertEquals(2, juego.obtenerLimitesArcos().length);
    }
}
