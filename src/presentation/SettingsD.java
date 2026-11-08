package presentation;

import javax.swing.*;
import java.awt.*;

public class SettingsD extends JDialog {
    private JCheckBox soundCheck;
    private JCheckBox musicCheck;
    private JList<String> languagelist;
    private JButton saveButton;

    public SettingsD(HeadSoccerGUI gui) {
        super(gui, "Configuración", true);
        setSize(350, 300);
        setLocationRelativeTo(gui);
        setLayout(new GridLayout(4, 1, 10, 10));

        soundCheck = new JCheckBox("Sonido", gui.getModel().getSettings().isSoundOn());
        musicCheck = new JCheckBox("Música", gui.getModel().getSettings().isMusicOn());
        
        String[] languages = {"Español", "Inglés"};
        languagelist = new JList<>(languages);
        languagelist.setSelectedIndex(0);

        saveButton = new JButton("Guardar / Volver");

        add(soundCheck);
        add(musicCheck);
        add(new JScrollPane(languagelist));
        add(saveButton);

        saveButton.addActionListener(e -> {
            gui.getModel().getSettings().setSoundOn(soundCheck.isSelected());
            gui.getModel().getSettings().setMusicOn(musicCheck.isSelected());
            gui.getModel().getSettings().setLanguage(languagelist.getSelectedValue());
            dispose();
        });
    }
}
