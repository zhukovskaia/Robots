package gui;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class RobotInfoWindow extends JInternalFrame implements RobotModel.Observer {
    private final RobotModel model;
    private final JLabel lblPos;
    private final JLabel lblTarget;
    private final JLabel lblDir;

    public static final String CONFIG_KEY = "robot_info";

    public static int getDefaultWidth() { return 250; }
    public static int getDefaultHeight() { return 130; }
    public static int getDefaultX() { return 20; }
    public static int getDefaultY() { return 490; }

    public RobotInfoWindow(RobotModel model) {
        super("Координаты робота", true, true, true, true);
        this.model = model;
        model.addObserver(this);

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        lblPos = new JLabel("Позиция: (0, 0)");
        lblTarget = new JLabel("Цель: (0, 0)");
        lblDir = new JLabel("Направление: 0°");

        panel.add(lblPos, BorderLayout.NORTH);
        panel.add(lblTarget, BorderLayout.CENTER);
        panel.add(lblDir, BorderLayout.SOUTH);
        getContentPane().add(panel);
        pack();
        setSize(getDefaultWidth(), getDefaultHeight());
        updateLabels();
    }

    private void updateLabels() {
        lblPos.setText(String.format("Позиция: (%.1f, %.1f)", model.getX(), model.getY()));
        lblTarget.setText(String.format("Цель: (%d, %d)", model.getTargetX(), model.getTargetY()));
        lblDir.setText(String.format("Направление: %.1f°", Math.toDegrees(model.getDir())));
    }

    @Override public void onStateChanged(double x, double y, double dir, int tx, int ty) {
        EventQueue.invokeLater(this::updateLabels);
    }

    public void close() { model.removeObserver(this); dispose(); }
}