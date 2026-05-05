package gui;

import java.awt.BorderLayout;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class GameWindow extends JInternalFrame {
    private final RobotModel model;
    private final GameVisualizer visualizer;
    private final RobotInfoWindow infoWindow;

    public GameWindow() {
        super("Игровое поле", true, true, true, true);
        model = new RobotModel();
        visualizer = new GameVisualizer(model);
        infoWindow = new RobotInfoWindow(model);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    public RobotInfoWindow getInfoWindow() { return infoWindow; }
}