package gui;

import model.RobotModel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import javax.swing.JPanel;

public class GameVisualizer extends JPanel implements RobotModel.Observer {
    private final RobotModel model;

    public GameVisualizer(RobotModel model) {
        this.model = model;
        model.addObserver(this);
        setDoubleBuffered(true);


        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                model.setTarget(e.getX(), e.getY());
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int tx = model.getTargetX();
        int ty = model.getTargetY();
        int rx = (int) Math.round(model.getX());
        int ry = (int) Math.round(model.getY());
        double dir = model.getDir();


        drawTarget(g2d, tx, ty);
        drawRobot(g2d, rx, ry, dir);
    }

    private void drawRobot(Graphics2D g, int x, int y, double dir) {
        AffineTransform old = g.getTransform();
        try {
            g.rotate(dir, x, y);
            g.setColor(Color.MAGENTA);
            g.fillOval(x - 15, y - 5, 30, 10);
            g.setColor(Color.BLACK);
            g.drawOval(x - 15, y - 5, 30, 10);
            g.setColor(Color.WHITE);
            g.fillOval(x + 5, y - 2, 5, 5);
            g.setColor(Color.BLACK);
            g.drawOval(x + 5, y - 2, 5, 5);
        } finally {
            g.setTransform(old);
        }
    }

    private void drawTarget(Graphics2D g, int x, int y) {
        g.setColor(Color.GREEN);
        g.fillOval(x - 3, y - 3, 6, 6);
        g.setColor(Color.BLACK);
        g.drawOval(x - 3, y - 3, 6, 6);
    }

    @Override
    public void onStateChanged(double x, double y, double dir, int tx, int ty) {
        repaint();
    }
}