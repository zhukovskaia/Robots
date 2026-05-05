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
        setSize(230, 110);

        // Вызываем метод обновления меток
        updateLabels();
    }

    // Переименовано из updateUI, чтобы не конфликтовать со системным методом Swing
    private void updateLabels() {
        lblPos.setText(String.format("Позиция: (%.1f, %.1f)", model.getX(), model.getY()));
        lblTarget.setText(String.format("Цель: (%d, %d)", model.getTargetX(), model.getTargetY()));
        lblDir.setText(String.format("Направление: %.1f°", Math.toDegrees(model.getDir())));
    }

    @Override
    public void onStateChanged(double x, double y, double dir, int tx, int ty) {
        // Обновляем метки в потоке событий при изменении модели
        EventQueue.invokeLater(this::updateLabels);
    }

    public void close() {
        model.removeObserver(this);
        dispose();
    }
}