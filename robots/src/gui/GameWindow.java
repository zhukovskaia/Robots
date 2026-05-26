package gui;

import controller.RobotController;
import model.RobotModel;
import utils.i18n.LocaleManager;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class GameWindow extends JInternalFrame implements LocaleManager.LocaleChangeListener {
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
        super("", true, true, true, true);
        this.model = model;
        this.visualizer = new GameVisualizer(model);
        this.infoWindow = new RobotInfoWindow(model);
        this.controller = new RobotController(model, visualizer);

        LocaleManager.addListener(this);


        setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();

        updateTitle();
        setVisible(true);
        controller.start();
    }

    private void updateTitle() {
        setTitle(LocaleManager.get("game.title"));
    }

    @Override
    public void onLocaleChanged() {
        EventQueue.invokeLater(() -> {
            updateTitle();
            setVisible(true);
            try {
                setSelected(true);
                toFront();
            } catch (Exception e) {

            }
        });
    }

    public RobotInfoWindow getInfoWindow() {
        return infoWindow;
    }

    public void shutdown() {
        LocaleManager.removeListener(this);
        controller.stop();
        infoWindow.close();
    }
}