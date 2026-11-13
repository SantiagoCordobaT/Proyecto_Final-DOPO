package presentation;

import java.util.HashMap;
import java.util.Map;

/**
 * Tabla de textos de la interfaz en los idiomas disponibles (Singleton): toda la
 * vista comparte un unico idioma activo.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class Texts {

    private static final String[] LANGUAGES = { "Español", "English" };

    private static Texts instance;

    private final Map<String, String[]> table = new HashMap<String, String[]>();
    private int language;

    private Texts() {
        put("menu.subtitle", "Fútbol callejero 1 contra 1", "1 v 1 street football");
        put("menu.play", "Jugar", "Play");
        put("menu.settings", "Configuración", "Settings");
        put("menu.exit", "Salir", "Exit");
        put("setup.title", "Modo de juego", "Game mode");
        put("setup.mode", "Modalidad", "Mode");
        put("setup.type", "Tipo de partido", "Match type");
        put("common.select", "Seleccionar", "Select");
        put("common.back", "Volver", "Back");
        put("select.title", "Escoge tu personaje", "Choose your character");
        put("select.player", "Jugador", "Player");
        put("select.play", "Jugar", "Play");
        put("card.speed", "Velocidad", "Speed");
        put("card.jump", "Salto", "Jump");
        put("card.strength", "Fuerza", "Strength");
        put("card.power", "Poder Único", "Unique Power");
        put("card.special", "Poder especial:", "Special power:");
        put("settings.title", "Configuración", "Settings");
        put("settings.language", "Idioma", "Language");
        put("settings.save", "Guardar", "Save");
        put("match.hint", "P / Esc: pausa", "P / Esc: pause");
        put("hud.armed", "¡PODER LISTO!", "POWER READY!");
        put("pause.title", "Pausa", "Pause");
        put("pause.resume", "Reanudar", "Resume");
        put("pause.giveUp", "Abandonar", "Give up");
        put("help.controls",
                "Jugador 1: A y D mover, W saltar, S patear, Q poder.\n"
                        + "Jugador 2: flechas ← y → mover, ↑ saltar, ↓ patear, Ctrl derecho poder.\n"
                        + "La barra de poder se llena con el tiempo. Llena, el poder se arma\n"
                        + "y la siguiente patada al balón lo lanza contra el rival.",
                "Player 1: A and D move, W jump, S kick, Q power.\n"
                        + "Player 2: arrows ← and → move, ↑ jump, ↓ kick, right Ctrl power.\n"
                        + "The power bar fills over time. When full, the power is armed\n"
                        + "and the next kick of the ball fires it at the rival.");
        put("result.title", "Fin del partido", "Match over");
        put("result.winner", "Ganador:", "Winner:");
        put("result.rematch", "Revancha", "Rematch");
        put("result.menu", "Menú", "Menu");
        put("error.title", "Error", "Error");
    }

    /** @return La unica instancia de la tabla de textos. */
    static Texts getInstance() {
        if (instance == null) {
            instance = new Texts();
        }
        return instance;
    }

    /**
     * @param key Clave del texto.
     * @return El texto en el idioma activo.
     */
    static String text(String key) {
        return getInstance().translate(key);
    }

    /** @return Nombres de los idiomas disponibles. */
    String[] getLanguages() {
        return LANGUAGES.clone();
    }

    /** @return Indice del idioma activo. */
    int getLanguage() {
        return language;
    }

    /** @param language Indice del idioma que pasa a estar activo. */
    void setLanguage(int language) {
        this.language = language;
    }

    private String translate(String key) {
        return table.get(key)[language];
    }

    private void put(String key, String spanish, String english) {
        table.put(key, new String[] { spanish, english });
    }
}
