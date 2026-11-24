package dominio;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import dominio.excepciones.POOBSoccerException;
import dominio.modalidades.GameModeCatalog;
import dominio.partidos.MatchType;
import dominio.partidos.MatchTypeCatalog;
import dominio.personajes.CharacterCatalog;

/**
 * Pruebas de los tipos de partido: Supervivencia, Por Goles y el bucle del
 * encuentro.
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
    public void supervivenciaSoloAdmiteModoPvsM() throws POOBSoccerException {
        HeadSoccer juego = HeadSoccer.obtenerInstancia();
        juego.nuevoJuego();

        juego.seleccionarModo(PVSP);
        POOBSoccerException ex = assertThrows(POOBSoccerException.class,
                () -> juego.seleccionarTipoPartido(SUPERVIVENCIA));
        assertEquals(POOBSoccerException.SUPERVIVENCIA_SOLO_PVSM, ex.getMessage());

        juego.seleccionarModo(MVSM);
        assertThrows(POOBSoccerException.class, () -> juego.seleccionarTipoPartido(SUPERVIVENCIA));

        juego.seleccionarModo(PVSM);
        juego.seleccionarTipoPartido(SUPERVIVENCIA);
        juego.seleccionarJugador(1, 0);
        juego.seleccionarJugador(2, 1);
        juego.iniciarPartido();
        assertEquals("Balones 3-3 | Racha 0", juego.obtenerEstadoPartido());
        assertFalse(juego.estaPartidoTerminado());
    }

    @Test
    public void pierdeCuandoJugadorUnoSeQuedaSinBalones() throws POOBSoccerException {
        CharacterCatalog catalogo = new CharacterCatalog();
        Match partido = new Match(new GameModeCatalog().obtener(PVSM),
                new MatchTypeCatalog().obtener(SUPERVIVENCIA),
                catalogo.obtener(0).copiar(), catalogo.obtener(1).copiar(), catalogo);

        partido.anotarGol(2);
        partido.anotarGol(2);
        assertFalse(partido.estaTerminado());
        partido.anotarGol(2);

        assertTrue(partido.estaTerminado());
        assertEquals("Balones 0-3 | Racha 0", partido.obtenerEstado());
        assertEquals(partido.obtenerResumenJugador(2).obtenerNombre(), partido.obtenerNombreGanador());
    }

    @Test
    public void continuaSerieYRotacionCuandoCaeLaMaquina() throws POOBSoccerException {
        CharacterCatalog catalogo = new CharacterCatalog();
        Match partido = new Match(new GameModeCatalog().obtener(PVSM),
                new MatchTypeCatalog().obtener(SUPERVIVENCIA),
                catalogo.obtener(0).copiar(), catalogo.obtener(0).copiar(), catalogo);

        String primerRival = partido.obtenerResumenJugador(2).obtenerNombre();
        partido.actualizarTick(1.0 / 60.0);
        assertTrue(partido.obtenerResumenJugador(2).obtenerCargaPoder() > 0.0);

        partido.anotarGol(1);
        partido.anotarGol(1);
        partido.anotarGol(1);

        PlayerSummary nuevoRival = partido.obtenerResumenJugador(2);
        assertFalse(partido.estaTerminado());
        assertEquals("Balones 3-3 | Racha 1", partido.obtenerEstado());
        assertNotEquals(primerRival, nuevoRival.obtenerNombre());
        assertEquals(0, nuevoRival.obtenerGoles());
        assertEquals(0.0, nuevoRival.obtenerCargaPoder(), 0.001);
        assertEquals(700.0, nuevoRival.obtenerX() + nuevoRival.obtenerAncho() / 2.0, 0.001);
    }

    @Test
    public void marcadorSeReiniciaPeroJugadorUnoConservaPoder() throws POOBSoccerException {
        CharacterCatalog catalogo = new CharacterCatalog();
        Match partido = new Match(new GameModeCatalog().obtener(PVSM),
                new MatchTypeCatalog().obtener(SUPERVIVENCIA),
                catalogo.obtener(0).copiar(), catalogo.obtener(1).copiar(), catalogo);

        partido.anotarGol(2);
        partido.actualizarTick(1.0 / 60.0);
        double poderJugadorUno = partido.obtenerResumenJugador(1).obtenerCargaPoder();

        partido.anotarGol(1);
        partido.anotarGol(1);
        partido.anotarGol(1);

        assertEquals(0, partido.obtenerResumenJugador(1).obtenerGoles());
        assertEquals(0, partido.obtenerResumenJugador(2).obtenerGoles());
        assertEquals("Balones 2-3 | Racha 1", partido.obtenerEstado());
        assertEquals(poderJugadorUno, partido.obtenerResumenJugador(1).obtenerCargaPoder(), 0.001);
    }

    @Test
    public void supervivenciaConSoloDosPersonajesEnCatalogo() throws POOBSoccerException {
        CharacterCatalog catalogo = new CharacterCatalog();
        while (catalogo.cantidad() > 2) {
            catalogo.eliminarPersonaje(catalogo.cantidad() - 1);
        }
        assertEquals(2, catalogo.cantidad());
        String nombre0 = catalogo.obtenerNombres()[0];
        String nombre1 = catalogo.obtenerNombres()[1];

        Match partido = new Match(new GameModeCatalog().obtener(PVSM),
                new MatchTypeCatalog().obtener(SUPERVIVENCIA),
                catalogo.obtener(0).copiar(), catalogo.obtener(1).copiar(), catalogo);

        // Derrotar al primer rival (nombre1) -> entra nombre0
        for (int i = 0; i < 3; i++) {
            partido.anotarGol(1);
        }
        assertEquals(nombre0, partido.obtenerResumenJugador(2).obtenerNombre());
        assertEquals("Balones 3-3 | Racha 1", partido.obtenerEstado());

        // Derrotar al segundo rival (nombre0) -> vuelve a rotar a nombre1
        for (int i = 0; i < 3; i++) {
            partido.anotarGol(1);
        }
        assertEquals(nombre1, partido.obtenerResumenJugador(2).obtenerNombre());
        assertEquals("Balones 3-3 | Racha 2", partido.obtenerEstado());
        assertFalse(partido.estaTerminado());
    }

    @Test
    public void cambioDeRivalSeConsumeUnaSolaVez() throws POOBSoccerException {
        MatchType tipo = new MatchTypeCatalog().obtener(SUPERVIVENCIA);
        tipo.iniciar();
        tipo.golAnotado(1, 1);
        assertFalse(tipo.consumirCambioRival());
        tipo.golAnotado(1, 2);
        assertFalse(tipo.consumirCambioRival());
        tipo.golAnotado(1, 3);
        assertTrue(tipo.consumirCambioRival());
        assertFalse(tipo.consumirCambioRival());
    }

    @Test
    public void partidoPorGolesTerminaAlAlcanzarLaMeta() throws POOBSoccerException {
        CharacterCatalog catalogo = new CharacterCatalog();
        Match partido = new Match(new GameModeCatalog().obtener(PVSP),
                new MatchTypeCatalog().obtener(GOLES_3),
                catalogo.obtener(0).copiar(), catalogo.obtener(1).copiar(), catalogo);

        partido.anotarGol(1);
        partido.anotarGol(1);
        assertFalse(partido.estaTerminado());
        partido.anotarGol(1);
        assertTrue(partido.estaTerminado());
        assertEquals(partido.obtenerResumenJugador(1).obtenerNombre(), partido.obtenerNombreGanador());
    }

    @Test
    public void golesDespuesDeTerminarNoCambianGanador() throws POOBSoccerException {
        MatchType tipo = new MatchTypeCatalog().obtener(GOLES_3);
        tipo.iniciar();
        tipo.golAnotado(1, 3);
        tipo.golAnotado(2, 3);
        assertTrue(tipo.estaTerminado());
        assertEquals(1, tipo.obtenerGanador());

        tipo.iniciar();
        assertFalse(tipo.estaTerminado());
    }

    @Test
    public void balonAvanzaIgualConTicksPequenosYNoSaleDeLaCancha() throws POOBSoccerException {
        CharacterCatalog catalogo = new CharacterCatalog();
        Match rapido = new Match(new GameModeCatalog().obtener(PVSP),
                new MatchTypeCatalog().obtener(GOLES_3),
                catalogo.obtener(0).copiar(), catalogo.obtener(0).copiar(), catalogo);
        Match normal = new Match(new GameModeCatalog().obtener(PVSP),
                new MatchTypeCatalog().obtener(GOLES_3),
                catalogo.obtener(0).copiar(), catalogo.obtener(0).copiar(), catalogo);

        for (int i = 0; i < 12; i++) {
            rapido.actualizarTick(1.0 / 120.0);
        }
        for (int i = 0; i < 6; i++) {
            normal.actualizarTick(1.0 / 60.0);
        }
        assertArrayEquals(normal.obtenerPosicionBalon(), rapido.obtenerPosicionBalon(), 0.001);

        double[] pos = normal.obtenerPosicionBalon();
        assertTrue(pos[0] >= Ball.RADIO && pos[0] <= Field.ANCHO - Ball.RADIO);
        assertTrue(pos[1] >= Ball.RADIO && pos[1] <= Field.SUELO_Y - Ball.RADIO);
    }
}
