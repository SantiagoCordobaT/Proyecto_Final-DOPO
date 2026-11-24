package dominio;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dominio.excepciones.POOBSoccerException;

/**
 * Pruebas de integración de la fachada HeadSoccer: configuración de partido,
 * controles, pausa, edición de personajes e IA.
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

    private HeadSoccer juego;

    @BeforeEach
    public void limpiar() throws POOBSoccerException {
        juego = HeadSoccer.obtenerInstancia();
        juego.nuevoJuego();
    }

    private void arrancar(int modo, int tipo, int j1, int j2) throws POOBSoccerException {
        juego.seleccionarModo(modo);
        juego.seleccionarTipoPartido(tipo);
        juego.seleccionarJugador(1, j1);
        juego.seleccionarJugador(2, j2);
        juego.iniciarPartido();
    }

    @Test
    public void instanciaUnicaYNuevoJuegoLimpiaTodo() throws POOBSoccerException {
        assertSame(juego, HeadSoccer.obtenerInstancia());
        arrancar(PVSP, GOLES_3, 0, 1);
        juego.nuevoJuego();

        POOBSoccerException sinPartido = assertThrows(POOBSoccerException.class, () -> juego.obtenerEstadoPartido());
        assertEquals(POOBSoccerException.PARTIDO_NO_INICIADO, sinPartido.getMessage());
        POOBSoccerException sinModo = assertThrows(POOBSoccerException.class, () -> juego.iniciarPartido());
        assertEquals(POOBSoccerException.MODO_INVALIDO, sinModo.getMessage());
    }

    @Test
    public void cambiarModoBorraTipoPartidoYTipoRequiereModo() throws POOBSoccerException {
        POOBSoccerException sinModo = assertThrows(POOBSoccerException.class,
                () -> juego.seleccionarTipoPartido(GOLES_3));
        assertEquals(POOBSoccerException.MODO_INVALIDO, sinModo.getMessage());

        juego.seleccionarModo(PVSM);
        juego.seleccionarTipoPartido(SUPERVIVENCIA);
        juego.seleccionarModo(PVSP);
        juego.seleccionarJugador(1, 0);
        juego.seleccionarJugador(2, 1);
        POOBSoccerException borrado = assertThrows(POOBSoccerException.class, () -> juego.iniciarPartido());
        assertEquals(POOBSoccerException.MODO_INVALIDO, borrado.getMessage());
    }

    @Test
    public void noIniciaSinAmbosJugadoresOConSeleccionesInvalidas() throws POOBSoccerException {
        assertThrows(POOBSoccerException.class, () -> juego.seleccionarModo(-1));
        assertThrows(POOBSoccerException.class, () -> juego.seleccionarJugador(1, 99));
        assertThrows(POOBSoccerException.class, () -> juego.seleccionarJugador(3, 0));

        juego.seleccionarModo(PVSP);
        juego.seleccionarTipoPartido(GOLES_3);
        juego.seleccionarJugador(1, 0);
        POOBSoccerException faltaRival = assertThrows(POOBSoccerException.class, () -> juego.iniciarPartido());
        assertEquals(POOBSoccerException.JUGADORES_INCOMPLETOS, faltaRival.getMessage());
    }

    @Test
    public void editarPersonajeQueElRivalTeniaSeleccionado() throws POOBSoccerException {
        int indice = juego.crearPersonaje("Diego", "Argentina", 4, 4, 3, 2, 0);
        try {
            juego.seleccionarModo(PVSP);
            juego.seleccionarTipoPartido(GOLES_3);
            juego.seleccionarJugador(1, 0);
            juego.seleccionarJugador(2, indice);

            // Al editar el personaje que el rival ya tenía elegido, exige volver a elegir y
            // toma los cambios
            juego.editarPersonaje(indice, "Lucas", "Perú", 5, 4, 4, 3, 2);
            POOBSoccerException ex = assertThrows(POOBSoccerException.class, () -> juego.iniciarPartido());
            assertEquals(POOBSoccerException.JUGADORES_INCOMPLETOS, ex.getMessage());

            juego.seleccionarJugador(1, 0);
            juego.seleccionarJugador(2, indice);
            juego.iniciarPartido();

            ResumenJugador rival = juego.obtenerResumenJugador(2);
            assertEquals("Lucas", rival.nombre);
            assertEquals("Perú", rival.pais);
            assertEquals(juego.obtenerNombresPoderes()[2], rival.nombrePoder);
        } finally {
            juego.eliminarPersonaje(indice);
        }
    }

    @Test
    public void pausaDetieneElTiempoYSueltaTeclas() throws POOBSoccerException {
        arrancar(PVSP, GOLES_3, 0, 1);
        juego.presionarAccion(1, "RIGHT");
        juego.pausar();

        double balonYAntes = juego.obtenerPosicionBalon()[1];
        juego.actualizarTick(1.0);
        assertEquals(balonYAntes, juego.obtenerPosicionBalon()[1], 0.001);
        assertEquals("Meta: 3 goles | 00:00", juego.obtenerEstadoPartido());

        juego.reanudar();
        juego.actualizarTick(1.0 / 60.0);
        ResumenJugador j1 = juego.obtenerResumenJugador(1);
        assertEquals(200.0, j1.x + j1.ancho / 2.0, 0.001);

        juego.actualizarTick(1.0);
        assertEquals("Meta: 3 goles | 00:01", juego.obtenerEstadoPartido());
    }

    @Test
    public void soltarTodasLasAccionesSinPausarElReloj() throws POOBSoccerException {
        arrancar(PVSP, GOLES_3, 0, 1);
        juego.presionarAccion(1, "RIGHT");
        juego.presionarAccion(2, "LEFT");
        juego.soltarTodasLasAcciones();
        juego.actualizarTick(1.0);

        ResumenJugador j1 = juego.obtenerResumenJugador(1);
        ResumenJugador j2 = juego.obtenerResumenJugador(2);
        assertEquals(200.0, j1.x + j1.ancho / 2.0, 0.001);
        assertEquals(700.0, j2.x + j2.ancho / 2.0, 0.001);
        assertEquals("Meta: 3 goles | 00:01", juego.obtenerEstadoPartido());
    }

    @Test
    public void controlesHumanosYTeclasOpuestas() throws POOBSoccerException {
        arrancar(PVSP, GOLES_3, 0, 1);
        juego.presionarAccion(1, "RIGHT");
        juego.actualizarTick(1.0 / 60.0);
        double xDespues = juego.obtenerResumenJugador(1).x;
        assertTrue(xDespues > 170.0);

        juego.soltarAccion(1, "RIGHT");
        juego.actualizarTick(1.0 / 60.0);
        assertEquals(xDespues, juego.obtenerResumenJugador(1).x, 0.001);

        double yAntes = juego.obtenerResumenJugador(1).y;
        juego.presionarAccion(1, "JUMP");
        juego.actualizarTick(1.0 / 60.0);
        assertTrue(juego.obtenerResumenJugador(1).y < yAntes);

        // Teclas opuestas en jugador 2 se cancelan
        double x2Antes = juego.obtenerResumenJugador(2).x;
        juego.presionarAccion(2, "LEFT");
        juego.presionarAccion(2, "RIGHT");
        juego.actualizarTick(1.0 / 60.0);
        assertEquals(x2Antes, juego.obtenerResumenJugador(2).x, 0.001);
    }

    @Test
    public void tecladoNoControlaALaMaquina() throws POOBSoccerException {
        arrancar(PVSM, GOLES_3, 0, 1);
        double xAntes = juego.obtenerResumenJugador(2).x;
        juego.presionarAccion(2, "RIGHT");
        juego.presionarAccion(2, "POWER");
        juego.actualizarTick(1.0 / 60.0);
        // El balón cae en el centro (450), así que la máquina debe moverse hacia la izquierda
        assertTrue(juego.obtenerResumenJugador(2).x < xAntes);
    }

    
    @Test
    public void accionInvalidaYJugadorInvalido() throws POOBSoccerException {
        arrancar(PVSP, GOLES_3, 0, 1);
        assertEquals(POOBSoccerException.ACCION_INVALIDA,
                assertThrows(POOBSoccerException.class, () -> juego.presionarAccion(1, "VOLAR")).getMessage());
        assertEquals(POOBSoccerException.ACCION_INVALIDA,
                assertThrows(POOBSoccerException.class, () -> juego.soltarAccion(1, null)).getMessage());
        assertEquals(POOBSoccerException.JUGADOR_INVALIDO,
                assertThrows(POOBSoccerException.class, () -> juego.presionarAccion(3, "KICK")).getMessage());
    }

    @Test
    public void pedirGanadorAntesDeTerminarFalla() throws POOBSoccerException {
        arrancar(PVSP, GOLES_3, 0, 1);
        POOBSoccerException ex = assertThrows(POOBSoccerException.class, () -> juego.obtenerNombreGanador());
        assertEquals(POOBSoccerException.PARTIDO_EN_CURSO, ex.getMessage());
    }

    @Test
    public void barraDePoderCargaYSeArmaAlLlenarse() throws POOBSoccerException {
        arrancar(PVSP, GOLES_3, 0, 1);
        juego.presionarAccion(1, "POWER");
        juego.actualizarTick(1.0 / 60.0);
        assertFalse(juego.obtenerResumenJugador(1).poderArmado);

        juego.actualizarTick(17.0);
        assertEquals(1.0, juego.obtenerResumenJugador(1).cargaPoder, 0.001);
        juego.actualizarTick(1.0 / 60.0);
        assertTrue(juego.obtenerResumenJugador(1).poderArmado);
    }

    @Test
    public void revanchaIniciaEnCeroConMismosPersonajes() throws POOBSoccerException {
        arrancar(PVSP, GOLES_3, 0, 1);
        String j1Nombre = juego.obtenerResumenJugador(1).nombre;
        String j2Nombre = juego.obtenerResumenJugador(2).nombre;
        juego.actualizarTick(2.0);
        assertTrue(juego.obtenerResumenJugador(2).cargaPoder > 0.0);

        juego.iniciarPartido();
        assertEquals(j1Nombre, juego.obtenerResumenJugador(1).nombre);
        assertEquals(j2Nombre, juego.obtenerResumenJugador(2).nombre);
        assertEquals(0.0, juego.obtenerResumenJugador(2).cargaPoder, 0.001);
        assertEquals("Meta: 3 goles | 00:00", juego.obtenerEstadoPartido());
    }

    @Test
    public void maquinaVsMaquinaCorreSinErrores() throws POOBSoccerException {
        arrancar(MVSM, GOLES_3, 0, 1);
        for (int i = 0; i < 600; i++) {
            juego.actualizarTick(1.0 / 60.0);
        }
        double[] balon = juego.obtenerPosicionBalon();
        assertTrue(balon[0] >= 0.0 && balon[0] <= Field.ANCHO);
    }

    @Test
    public void fachadaExponeResumenJugadorYMedidasDeCancha() throws POOBSoccerException {
        arrancar(PVSP, GOLES_3, 0, 1);
        ResumenJugador j1 = juego.obtenerResumenJugador(1);
        ResumenJugador j2 = juego.obtenerResumenJugador(2);

        assertEquals(1, j1.orientacion);
        assertEquals(-1, j2.orientacion);
        assertFalse(j1.pateando);
        assertEquals(juego.obtenerFichaPersonaje(0).nombrePoder, j1.nombrePoder);

        assertEquals(Field.ANCHO, juego.obtenerAnchoCancha(), 0.001);
        assertEquals(Field.SUELO_Y, juego.obtenerSueloY(), 0.001);
        assertEquals(Ball.RADIO, juego.obtenerRadioBalon(), 0.001);
        assertEquals(Goal.GROSOR_BORDE, juego.obtenerGrosorTravesano(), 0.001);
        assertEquals(2, juego.obtenerLimitesArcos().length);
    }
}
