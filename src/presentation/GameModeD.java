package presentation;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GameModeD extends JDialog {
    private JList<String> modelList;
    private JButton selectButton;
    private JButton cancelButton;

    public GameModeD(HeadSoccerGUI gui) {
        super(gui, "Selecciona el modo de juego", true);
        setSize(400, 300);
        setLocationRelativeTo(gui);
        setLayout(new BorderLayout());

        // Obtener los nombres de los modos desde el modelo de dominio usando getModeNames()
        String[] modeNames = gui.getModel().getModeNames();

        modelList = new JList<>(modeNames);
        add(new JScrollPane(modelList), BorderLayout.CENTER);

        JPanel panelButtons = new JPanel();
        selectButton = new JButton("Seleccionar");
        cancelButton = new JButton("Volver");
        panelButtons.add(selectButton);
        panelButtons.add(cancelButton);
        add(panelButtons, BorderLayout.SOUTH);

        selectButton.addActionListener(e -> {
            int index = modelList.getSelectedIndex();
            if (index != -1) {
                gui.selectMode(index);
                dispose();
                gui.openCharacterSelect(); // Salta a escoger personaje
            }
        });

        cancelButton.addActionListener(e -> dispose());
    }

    public void showModes(ArrayList<String> names) {
        modelList.setListData(names.toArray(new String[0]));
    }
}