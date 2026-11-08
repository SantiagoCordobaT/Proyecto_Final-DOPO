package presentation;

import javax.swing.*;
import java.awt.*;

public class MainMenuP extends JPanel {
    private JButton playButton;
    private JButton settingsButton;

    public MainMenuP(HeadSoccerGUI gui) {
        setLayout(new GridBagLayout());
        setBackground(new Color(30, 144, 255)); 

        playButton = new JButton("Jugar");
        settingsButton = new JButton("Configuración");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(playButton, gbc);

        gbc.gridy = 1;
        add(settingsButton, gbc);

        // Eventos de los botones según tus wireframes
        playButton.addActionListener(e -> gui.openGamesModes());
        settingsButton.addActionListener(e -> gui.openSettings());
    }
}