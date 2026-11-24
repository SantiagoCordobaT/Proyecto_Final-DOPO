package dominio;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import dominio.excepciones.POOBSoccerException;
import dominio.modalidades.GameModeCatalog;
import dominio.partidos.MatchType;
import dominio.partidos.MatchTypeCatalog;
import dominio.personajes.Character;
import dominio.personajes.CharacterCatalog;

/**
 * Pruebas de reglas de partido: Supervivencia (incluyendo autogol y catálogo
 * mínimo), partidos por goles y reinicio tras cada anotación.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class SurvivalMatchTest {

    private static final int PVSP = 0;
    private static final int PVSM = 1;
    private static final int MVSM = 2;
    private static final int GOLES_3 = 0;
    private static final int SUPERVIVENCIA = 3;

    @Test
    public void supervivenciaSoloSePermiteEnJugadorVsMaquina() throws POOBSoccerException {
        HeadSoccer juego = new HeadSoccer();
        assertEquals(POOBSoccerException.SUPERVIVENCIA_SOLO_PVSM,
                assertThrows(POOBSoccerException.class,
                        () -> juego.iniciarPartido(PVSP, SUPERVIVENCIA, 0, 1)).getMessage());
        assertEquals(POOBSoccerException.SUPERVIVENCIA_SOLO_PVSM,
                assertThrows(POOBSoccerException.class,
                        () -> juego.iniciarPartido(MVSM, SUPERVIVENCIA, 0, 1)).getMessage());

        juego.iniciarPartido(PVSM, SUPERVIVENCIA, 0, 1);
        assertEquals("Balones 3-3 | Racha 0", juego.obtenerEstadoPartido());
        assertFalse(juego.estaPartidoTerminado());
    }

    @Test
    public void autogolEnSupervivenciaDescuentaBalonAlJugadorUno() throws POOBSoccerException {
        Field cancha = new Field();
        Ball balonEnArcoPropio = new Ball();
        // El jugador 1 defiende el arco izquierdo; si el balón entra ahí, cuenta para el jugador 2
        balonEnArcoPropio.ubicarEn(Goal.ANCHO / 2.0, Field.SUELO_Y - Ball.RADIO);
        int acreditado = cancha.anotadorDe(balonEnArcoPropio);
        assertEquals(2, acreditado);

        CharacterCatalog catalogo = new CharacterCatalog();
        Match partido = new Match(new GameModeCatalog().obtener(PVSM),
                new MatchTypeCatalog().obtener(SUPERVIVENCIA),
                catalogo.obtener(0).copiar(), catalogo.obtener(1).copiar(), catalogo);

        partido.registrarGol(acreditado);
        assertEquals("Balones 2-3 | Racha 0", partido.obtenerEstado());
        assertEquals(0, partido.obtenerResumenJugador(1).obtenerGoles());
        assertEquals(1, partido.obtenerResumenJugador(2).obtenerGoles());
    }

    @Test
    public void alCaerLaMaquinaSurvivalMatchEntregaSiguienteRivalYReiniciaPosiciones() throws POOBSoccerException {
        CharacterCatalog catalogo = new CharacterCatalog();
        Match partido = new Match(new GameModeCatalog().obtener(PVSM),
                new MatchTypeCatalog().obtener(SUPERVIVENCIA),
                catalogo.obtener(0).copiar(), catalogo.obtener(0).copiar(), catalogo);

        String rivalInicial = partido.obtenerResumenJugador(2).obtenerNombre();
        partido.actualizarTick(0.5);
        double cargaP1Antes = partido.obtenerResumenJugador(1).obtenerCargaPoder();

        partido.registrarGol(1);
        partido.registrarGol(1);
        partido.registrarGol(1);

        PlayerSummary p1 = partido.obtenerResumenJugador(1);
        PlayerSummary p2 = partido.obtenerResumenJugador(2);
        assertFalse(partido.estaTerminado());
        assertEquals("Balones 3-3 | Racha 1", partido.obtenerEstado());
        assertNotEquals(rivalInicial, p2.obtenerNombre());
        assertEquals(0, p1.obtenerGoles());
        assertEquals(0, p2.obtenerGoles());
        assertEquals(cargaP1Antes, p1.obtenerCargaPoder(), 0.001);
        assertEquals(0.0, p2.obtenerCargaPoder(), 0.001);
        assertEquals(200.0, p1.obtenerX() + p1.obtenerAncho() / 2.0, 0.001);
        assertEquals(700.0, p2.obtenerX() + p2.obtenerAncho() / 2.0, 0.001);
    }

    @Test
    public void supervivenciaTerminaCuandoJugadorUnoSeQuedaSinBalones() throws POOBSoccerException {
        CharacterCatalog catalogo = new CharacterCatalog();
        Match partido = new Match(new GameModeCatalog().obtener(PVSM),
                new MatchTypeCatalog().obtener(SUPERVIVENCIA),
                catalogo.obtener(0).copiar(), catalogo.obtener(1).copiar(), catalogo);

        partido.registrarGol(2);
        partido.registrarGol(2);
        assertFalse(partido.estaTerminado());
        partido.registrarGol(2);

        assertTrue(partido.estaTerminado());
        assertEquals("Balones 0-3 | Racha 0", partido.obtenerEstado());
        assertEquals(partido.obtenerResumenJugador(2).obtenerNombre(), partido.obtenerNombreGanador());
    }

    @Test
    public void supervivenciaConSoloDosPersonajesAlternaRivalesSinFallar() throws POOBSoccerException {
        CharacterCatalog catalogo = new CharacterCatalog();
        while (catalogo.cantidad() > 2) {
            catalogo.eliminarPersonaje(catalogo.cantidad() - 1);
        }
        String primero = catalogo.obtenerNombres()[0];
        String segundo = catalogo.obtenerNombres()[1];

        Match partido = new Match(new GameModeCatalog().obtener(PVSM),
                new MatchTypeCatalog().obtener(SUPERVIVENCIA),
                catalogo.obtener(0).copiar(), catalogo.obtener(1).copiar(), catalogo);

        for (int ronda = 0; ronda < 2; ronda++) {
            partido.registrarGol(1);
            partido.registrarGol(1);
            partido.registrarGol(1);
            String esperado = ronda == 0 ? primero : segundo;
            assertEquals(esperado, partido.obtenerResumenJugador(2).obtenerNombre());
        }
        assertEquals("Balones 3-3 | Racha 2", partido.obtenerEstado());
    }

    @Test
    public void tipoSupervivenciaSoloDevuelveRivalNuevoAlTercerGol() throws POOBSoccerException {
        CharacterCatalog catalogo = new CharacterCatalog();
        MatchType tipo = new MatchTypeCatalog().obtener(SUPERVIVENCIA);
        Character rival = catalogo.obtener(0).copiar();
        tipo.iniciar();

        assertNull(tipo.registrarGol(1, 1, rival, catalogo));
        assertNull(tipo.registrarGol(1, 2, rival, catalogo));
        Character nuevo = tipo.registrarGol(1, 3, rival, catalogo);
        assertNotNull(nuevo);
        assertNotEquals(rival.obtenerNombre(), nuevo.obtenerNombre());
        assertNull(tipo.registrarGol(1, 1, nuevo, catalogo));
    }

    @Test
    public void partidoPorGolesFinalizaEnLaMetaEIgnoraGolesPosteriores() throws POOBSoccerException {
        CharacterCatalog catalogo = new CharacterCatalog();
        Match partido = new Match(new GameModeCatalog().obtener(PVSP),
                new MatchTypeCatalog().obtener(GOLES_3),
                catalogo.obtener(0).copiar(), catalogo.obtener(1).copiar(), catalogo);

        partido.registrarGol(1);
        partido.registrarGol(1);
        assertFalse(partido.estaTerminado());
        partido.registrarGol(1);
        assertTrue(partido.estaTerminado());
        String ganador = partido.obtenerNombreGanador();
        assertEquals(partido.obtenerResumenJugador(1).obtenerNombre(), ganador);

        // Anotar después de terminar no cambia al ganador
        partido.registrarGol(2);
        partido.registrarGol(2);
        partido.registrarGol(2);
        assertEquals(ganador, partido.obtenerNombreGanador());
    }
}
