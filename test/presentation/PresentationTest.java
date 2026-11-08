package presentation;

import javax.swing.SwingUtilities;

public class PresentationTest {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("Iniciando pruebas visuales de la interfaz de Head Soccer...");
            
            new HeadSoccerGUI();
        });
    }
}