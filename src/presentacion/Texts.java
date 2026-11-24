package presentacion;

/**
 * Maneja el idioma activo de la interfaz (español o inglés).
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
final class Texts {

    private static final String[] IDIOMAS = { "Español", "English" };
    private static int idioma = 0;

    private Texts() {
    }

    static String t(String espanol, String ingles) {
        if (idioma == 1) {
            return ingles;
        }
        return espanol;
    }

    static String[] obtenerIdiomas() {
        return new String[] { IDIOMAS[0], IDIOMAS[1] };
    }

    static int obtenerIdioma() {
        return idioma;
    }

    static void establecerIdioma(int nuevoIdioma) {
        if (nuevoIdioma == 1) {
            idioma = 1;
        } else {
            idioma = 0;
        }
    }
}
