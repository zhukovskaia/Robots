package gui;

import model.RobotModel;
import utils.i18n.LocaleManager;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class RobotInfoWindow extends JInternalFrame implements RobotModel.Observer, LocaleManager.LocaleChangeListener {
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
        super("", true, true, true, true);
        this.model = model;
        model.addObserver(this);
        LocaleManager.addListener(this);


        setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        lblPos = new JLabel();
        lblTarget = new JLabel();
        lblDir = new JLabel();

        panel.add(lblPos, BorderLayout.NORTH);
        panel.add(lblTarget, BorderLayout.CENTER);
        panel.add(lblDir, BorderLayout.SOUTH);
        getContentPane().add(panel);
        pack();
        setSize(getDefaultWidth(), getDefaultHeight());

        updateTitle();
        updateLabels();
        setVisible(true);
    }

    private void updateTitle() {
        setTitle(LocaleManager.get("robot.title"));
    }

    private void updateLabels() {
        lblPos.setText(LocaleManager.format("robot.pos", model.getX(), model.getY()));
        lblTarget.setText(LocaleManager.format("robot.target", model.getTargetX(), model.getTargetY()));
        lblDir.setText(LocaleManager.format("robot.dir", Math.toDegrees(model.getDir())));
    }

    @Override
    public void onStateChanged(double x, double y, double dir, int tx, int ty) {
        EventQueue.invokeLater(this::updateLabels);
    }

    @Override
    public void onLocaleChanged() {
        EventQueue.invokeLater(() -> {
            updateTitle();
            updateLabels();
            setVisible(true);
        });
    }

    public void close() {
        model.removeObserver(this);
        LocaleManager.removeListener(this);
        dispose();
    }
}