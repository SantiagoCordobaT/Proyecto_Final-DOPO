package dominio.modalidades;

/**
 * Modalidad donde ambos personajes son controlados por la máquina.
 *
 * @author Santiago Cordoba - Camilo Rivas
 * @version 1.0
 */
class MvsM extends GameMode {

    MvsM() {
        super("Máquina vs Máquina");
    }

    @Override
    public Controller crearControladorJugador1() {
        return new AIController();
    }

    @Override
    public Controller crearControladorJugador2() {
        return new AIController();
    }
}
