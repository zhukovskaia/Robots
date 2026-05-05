package gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;

public class GameVisualizer extends JPanel implements RobotModel.Observer {
    private final RobotModel model;
    private final Timer timer = new Timer("events generator", true);

    public GameVisualizer(RobotModel model) {
        this.model = model;
        model.addObserver(this); // Подписываемся на обновления

        timer.schedule(new TimerTask() {
            @Override public void run() { EventQueue.invokeLater(GameVisualizer.this::repaint); }
        }, 0, 50);

        timer.schedule(new TimerTask() {
            @Override public void run() { model.update(); }
        }, 0, 10);

        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                model.setTarget(e.getX(), e.getY());
            }
        });
        setDoubleBuffered(true);
    }

    private static int round(double v) { return (int)(v + 0.5); }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        drawRobot(g2d, round(model.getX()), round(model.getY()), model.getDir());
        drawTarget(g2d, model.getTargetX(), model.getTargetY());
    }

    private void drawRobot(Graphics2D g, int x, int y, double dir) {
        g.setTransform(AffineTransform.getRotateInstance(dir, x, y));
        g.setColor(Color.MAGENTA); g.fillOval(x-15, y-5, 30, 10);
        g.setColor(Color.BLACK); g.drawOval(x-15, y-5, 30, 10);
        g.setColor(Color.WHITE); g.fillOval(x+5, y-2, 5, 5);
        g.setColor(Color.BLACK); g.drawOval(x+5, y-2, 5, 5);
    }

    private void drawTarget(Graphics2D g, int x, int y) {
        g.setTransform(new AffineTransform());
        g.setColor(Color.GREEN); g.fillOval(x-2, y-2, 5, 5);
        g.setColor(Color.BLACK); g.drawOval(x-2, y-2, 5, 5);
    }

    @Override
    public void onStateChanged(double x, double y, double dir, int tx, int ty) {
        // Перерисовка при изменении модели
        repaint();
    }
}