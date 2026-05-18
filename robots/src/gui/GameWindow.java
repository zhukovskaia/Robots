package gui;
import java.awt.BorderLayout;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import controller.RobotController;
import model.RobotModel;

public class GameWindow extends JInternalFrame {
    private final RobotModel model;
    private final GameVisualizer visualizer;
    private final RobotInfoWindow infoWindow;
    private final RobotController controller;

    public static final String CONFIG_KEY = "game";

    public static int getDefaultWidth() { return 600; }
    public static int getDefaultHeight() { return 450; }
    public static int getDefaultX() { return 20; }
    public static int getDefaultY() { return 20; }


    public GameWindow(RobotModel model) {
        super("Игровое поле", true, true, true, true);
        this.model = model;

        this.visualizer = new GameVisualizer(model);
        this.infoWindow = new RobotInfoWindow(model);
        this.controller = new RobotController(model, visualizer);
        controller.start();

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    public RobotInfoWindow getInfoWindow() {
        return infoWindow;
    }

    public void shutdown() {
        controller.stop();
        infoWindow.close();
    }
}