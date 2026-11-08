package presentation;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class CharacterSelectD extends JDialog {
    private JList<String> characterList;
    private JButton selectButton;
    private JButton cancelButton;

    public CharacterSelectD(HeadSoccerGUI gui) {
        super(gui, "Escoge tu personaje", true);
        setSize(400, 300);
        setLocationRelativeTo(gui);
        setLayout(new BorderLayout());

        // Obtener los nombres de los personajes usando el método del dominio
        String[] charNames = gui.getModel().getCharacterNames();

        characterList = new JList<>(charNames);
        add(new JScrollPane(characterList), BorderLayout.CENTER);

        JPanel panelButtons = new JPanel();
        selectButton = new JButton("Seleccionar");
        cancelButton = new JButton("Volver");
        panelButtons.add(selectButton);
        panelButtons.add(cancelButton);
        add(panelButtons, BorderLayout.SOUTH);

        selectButton.addActionListener(e -> {
            int index = characterList.getSelectedIndex();
            if (index != -1) {
                gui.selectCharacter(1, index);
                dispose();
                JOptionPane.showMessageDialog(this, "¡Iniciando partido VS!");
            }
        });

        cancelButton.addActionListener(e -> dispose());
    }

    public void showCharacters(ArrayList<String> names) {
        characterList.setListData(names.toArray(new String[0]));
    }
}