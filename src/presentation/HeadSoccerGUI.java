package presentation;

import dominio.HeadSoccer;
import dominio.excepciones.POOBSoccerException;
import javax.swing.*;

public class HeadSoccerGUI extends JFrame {
    private HeadSoccer model;
    private MainMenuP mainMenu;
    private GameModeD gameModeDialog;
    private CharacterSelectD characterSelectDialog;
    private SettingsD settingsDialog;

    public HeadSoccerGUI() {
        try {
            model = new HeadSoccer();
        } catch (POOBSoccerException e) {
            JOptionPane.showMessageDialog(this, "Error al inicializar el juego: " + e.getMessage());
        }

        setTitle("Head Soccer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Inicializar el menú principal
        mainMenu = new MainMenuP(this);
        add(mainMenu);
        
        setVisible(true);
    }

    public void openGamesModes() {
        gameModeDialog = new GameModeD(this);
        gameModeDialog.setVisible(true);
    }

    public void selectMode(int index) {
        try {
            model.selectMode(index);
        } catch (POOBSoccerException e) {
            JOptionPane.showMessageDialog(this, "Error al seleccionar modalidad: " + e.getMessage());
        }
    }

    public void openCharacterSelect() {
        characterSelectDialog = new CharacterSelectD(this);
        characterSelectDialog.setVisible(true);
    }

    public void selectCharacter(int playerIndex, int charIndex) {
        try {
            if (playerIndex == 1) {
                model.selectPlayer1(charIndex);
            } else {
                model.selectPlayer2(charIndex);
            }
        } catch (POOBSoccerException e) {
            JOptionPane.showMessageDialog(this, "Error al seleccionar personaje: " + e.getMessage());
        }
    }

    public void openSettings() {
        settingsDialog = new SettingsD(this);
        settingsDialog.setVisible(true);
    }

    public HeadSoccer getModel() {
        return model;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HeadSoccerGUI());
    }
}