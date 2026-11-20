package presentacion;

import java.util.HashMap;
import java.util.Map;

/**
 * Tabla de textos de la interfaz en los idiomas disponibles (Singleton): toda
 * la
 * vista comparte un unico idioma activo.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class Texts {

    private static final String[] IDIOMAS = { "Español", "English" };

    private static Texts instancia;

    private final Map<String, String[]> tabla = new HashMap<String, String[]>();
    private int idioma;

    private Texts() {
        registrar("menu.subtitle", "Fútbol callejero 1 contra 1", "1 v 1 street football");
        registrar("menu.play", "Jugar", "Play");
        registrar("menu.settings", "Configuración", "Settings");
        registrar("menu.exit", "Salir", "Exit");
        registrar("setup.title", "Modo de juego", "Game mode");
        registrar("setup.mode", "Modalidad", "Mode");
        registrar("setup.type", "Tipo de partido", "Match type");
        registrar("common.select", "Seleccionar", "Select");
        registrar("common.back", "Volver", "Back");
        registrar("select.title", "Escoge tu personaje", "Choose your character");
        registrar("select.player", "Jugador", "Player");
        registrar("select.play", "Jugar", "Play");
        registrar("select.create", "Crear", "Create");
        registrar("select.edit", "Editar", "Edit");
        registrar("select.delete", "Eliminar", "Delete");
        registrar("editor.createTitle", "Crear personaje", "Create character");
        registrar("editor.editTitle", "Editar personaje", "Edit character");
        registrar("editor.name", "Nombre", "Name");
        registrar("editor.country", "País", "Country");
        registrar("editor.powerType", "Poder", "Power");
        registrar("card.speed", "Velocidad", "Speed");
        registrar("card.jump", "Salto", "Jump");
        registrar("card.strength", "Fuerza", "Strength");
        registrar("card.power", "Poder Único", "Unique Power");
        registrar("card.special", "Poder especial:", "Special power:");
        registrar("settings.title", "Configuración", "Settings");
        registrar("settings.language", "Idioma", "Language");
        registrar("settings.save", "Guardar", "Save");
        registrar("match.hint", "P / Esc: pausa", "P / Esc: pause");
        registrar("match.goal", "¡GOL!", "GOAL!");
        registrar("hud.armed", "¡PODER LISTO!", "POWER READY!");
        registrar("pause.title", "Pausa", "Pause");
        registrar("pause.resume", "Reanudar", "Resume");
        registrar("pause.giveUp", "Abandonar", "Give up");
        registrar("help.controls",
                "Jugador 1: A y D mover, W saltar, S patear, Q poder.\n"
                        + "Jugador 2: flechas ← y → mover, ↑ saltar, ↓ patear, Ctrl derecho poder.\n"
                        + "La barra de poder se llena con el tiempo. Llena, el poder se arma\n"
                        + "y la siguiente patada al balón lo lanza contra el rival.",
                "Player 1: A and D move, W jump, S kick, Q power.\n"
                        + "Player 2: arrows ← and → move, ↑ jump, ↓ kick, right Ctrl power.\n"
                        + "The power bar fills over time. When full, the power is armed\n"
                        + "and the next kick of the ball fires it at the rival.");
        registrar("result.title", "Fin del partido", "Match over");
        registrar("result.winner", "Ganador:", "Winner:");
        registrar("result.rematch", "Revancha", "Rematch");
        registrar("result.menu", "Menú", "Menu");
        registrar("error.title", "Error", "Error");
    }

    /** @return La unica instancia de la tabla de textos. */
    static Texts obtenerInstancia() {
        if (instancia == null) {
            instancia = new Texts();
        }
        return instancia;
    }

    /**
     * @param clave Clave del texto.
     * @return El texto en el idioma activo.
     */
    static String texto(String clave) {
        return obtenerInstancia().traducir(clave);
    }

    /** @return Nombres de los idiomas disponibles. */
    String[] obtenerIdiomas() {
        return IDIOMAS.clone();
    }

    /** @return Indice del idioma activo. */
    int obtenerIdioma() {
        return idioma;
    }

    /** @param idioma Indice del idioma que pasa a estar activo. */
    void establecerIdioma(int idioma) {
        this.idioma = idioma;
    }

    private String traducir(String clave) {
        return tabla.get(clave)[idioma];
    }

    private void registrar(String clave, String espanol, String ingles) {
        tabla.put(clave, new String[] { espanol, ingles });
    }
}
