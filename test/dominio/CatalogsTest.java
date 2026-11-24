package dominio;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import dominio.excepciones.POOBSoccerException;
import dominio.modalidades.Action;
import dominio.modalidades.GameModeCatalog;
import dominio.partidos.MatchTypeCatalog;
import dominio.personajes.CharacterCatalog;
import dominio.poderes.PowerCatalog;

/**
 * Pruebas de los catálogos de modos, tipos de partido, poderes, personajes y
 * acciones.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
public class CatalogsTest {

    @Test
    public void catalogosTraenOpcionesInicialesEsperadas() throws POOBSoccerException {
        GameModeCatalog modos = new GameModeCatalog();
        MatchTypeCatalog tipos = new MatchTypeCatalog();
        PowerCatalog poderes = new PowerCatalog();
        CharacterCatalog personajes = new CharacterCatalog();

        assertEquals(3, modos.obtenerNombres().length);
        assertEquals("Jugador vs Máquina", modos.obtener(1).obtenerNombre());
        assertEquals(4, tipos.obtenerNombres().length);
        assertEquals("Por Goles (3)", tipos.obtenerNombres()[0]);
        assertEquals("Supervivencia", tipos.obtener(3).obtenerNombre());
        assertEquals("Congelamiento", poderes.obtener(0).obtenerNombre());
        assertEquals("Jacob", personajes.obtenerNombres()[0]);
    }

    @Test
    public void indicesInvalidosEnTodosLosCatalogos() throws POOBSoccerException {
        GameModeCatalog modos = new GameModeCatalog();
        MatchTypeCatalog tipos = new MatchTypeCatalog();
        PowerCatalog poderes = new PowerCatalog();
        CharacterCatalog personajes = new CharacterCatalog();

        assertEquals(POOBSoccerException.MODO_INVALIDO,
                assertThrows(POOBSoccerException.class, () -> modos.obtener(-1)).getMessage());
        assertEquals(POOBSoccerException.MODO_INVALIDO,
                assertThrows(POOBSoccerException.class, () -> tipos.obtener(99)).getMessage());
        assertEquals(POOBSoccerException.PODER_INVALIDO,
                assertThrows(POOBSoccerException.class, () -> poderes.obtener(99)).getMessage());
        assertEquals(POOBSoccerException.PERSONAJE_INVALIDO,
                assertThrows(POOBSoccerException.class, () -> personajes.obtener(99)).getMessage());
    }

    @Test
    public void cadaPersonajeInicialTienePoderDistinto() throws POOBSoccerException {
        CharacterCatalog personajes = new CharacterCatalog();
        Set<String> poderes = new HashSet<>();
        for (int i = 0; i < personajes.cantidad(); i++) {
            assertTrue(poderes.add(personajes.obtenerNombrePoder(i)));
        }
        assertEquals(personajes.cantidad(), poderes.size());
    }

    @Test
    public void crearYEditarPersonajeEnCatalogo() throws POOBSoccerException {
        CharacterCatalog catalogo = new CharacterCatalog();
        PowerCatalog poderes = new PowerCatalog();
        int antes = catalogo.cantidad();

        catalogo.crear("  Diego ", " Argentina ", 4, 5, 3, 2, poderes.obtener(0));
        assertEquals(antes + 1, catalogo.cantidad());
        assertEquals("Diego", catalogo.obtenerNombres()[antes]);
        assertEquals("Argentina", catalogo.obtenerPais(antes));

        catalogo.editar(antes, "Leo", "Uruguay", 5, 4, 4, 5, poderes.obtener(1));
        assertEquals("Leo", catalogo.obtenerNombres()[antes]);
        assertEquals("Uruguay", catalogo.obtenerPais(antes));
        assertEquals(5, catalogo.obtenerVelocidad(antes));
        assertEquals(poderes.obtener(1).obtenerNombre(), catalogo.obtenerNombrePoder(antes));
    }

    @Test
    public void rechazaNombreRepetidoEnMayusculas() throws POOBSoccerException {
        CharacterCatalog catalogo = new CharacterCatalog();
        PowerCatalog poderes = new PowerCatalog();

        POOBSoccerException exCrear = assertThrows(POOBSoccerException.class,
                () -> catalogo.crear("JACOB", "Chile", 3, 3, 3, 3, poderes.obtener(0)));
        assertEquals(POOBSoccerException.PERSONAJE_REPETIDO, exCrear.getMessage());

        POOBSoccerException exEditar = assertThrows(POOBSoccerException.class,
                () -> catalogo.editar(0, "KENJI", "Canadá", 3, 3, 3, 3, poderes.obtener(0)));
        assertEquals(POOBSoccerException.PERSONAJE_REPETIDO, exEditar.getMessage());
    }

    @Test
    public void eliminarHastaElMinimoDeDosPersonajes() throws POOBSoccerException {
        CharacterCatalog catalogo = new CharacterCatalog();
        while (catalogo.cantidad() > 2) {
            catalogo.eliminarPersonaje(catalogo.cantidad() - 1);
        }
        POOBSoccerException ex = assertThrows(POOBSoccerException.class, () -> catalogo.eliminarPersonaje(0));
        assertEquals(POOBSoccerException.MINIMO_PERSONAJES, ex.getMessage());
    }

    @Test
    public void accionesPorNombreValidasEInvalidas() throws POOBSoccerException {
        assertEquals(Action.LEFT, Action.porNombre("LEFT"));
        assertEquals(Action.RIGHT, Action.porNombre("RIGHT"));
        assertEquals(Action.JUMP, Action.porNombre("JUMP"));
        assertEquals(Action.KICK, Action.porNombre("KICK"));
        assertEquals(Action.POWER, Action.porNombre("POWER"));

        assertEquals(POOBSoccerException.ACCION_INVALIDA,
                assertThrows(POOBSoccerException.class, () -> Action.porNombre("")).getMessage());
        assertEquals(POOBSoccerException.ACCION_INVALIDA,
                assertThrows(POOBSoccerException.class, () -> Action.porNombre("SALTAR")).getMessage());
        assertEquals(POOBSoccerException.ACCION_INVALIDA,
                assertThrows(POOBSoccerException.class, () -> Action.porNombre(null)).getMessage());
    }

    @Test
    public void fichaPersonajeDesdeFachada() throws POOBSoccerException {
        HeadSoccer juego = new HeadSoccer();
        CharacterInfo ficha = juego.obtenerFichaPersonaje(0);
        assertEquals("Jacob", ficha.obtenerNombre());
        assertEquals("Canadá", ficha.obtenerPais());
        assertEquals(3, ficha.obtenerVelocidad());
        assertEquals(3, ficha.obtenerSalto());
        assertEquals(4, ficha.obtenerFuerza());
        assertEquals(3, ficha.obtenerNivelPoder());
        assertFalse(ficha.obtenerDescripcionPoder().isEmpty());

        POOBSoccerException ex = assertThrows(POOBSoccerException.class, () -> juego.obtenerFichaPersonaje(-1));
        assertEquals(POOBSoccerException.PERSONAJE_INVALIDO, ex.getMessage());
    }
}
