package controller;

import model.RobotModel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;

public class RobotController {
    private final RobotModel model;
    private final Timer timer;
    private long lastUpdateTime;
    private boolean isRunning = false;

    private static final double MAX_DELTA_TIME = 0.033;

    public RobotController(RobotModel model, JPanel panel) {
        this.model = model;
        this.timer = new Timer("RobotController", true);
        this.lastUpdateTime = System.currentTimeMillis();


        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                model.setTarget(e.getX(), e.getY());
            }
        });
    }

    public void start() {
        if (isRunning) return;
        isRunning = true;
        lastUpdateTime = System.currentTimeMillis();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                long deltaMs = now - lastUpdateTime;
                lastUpdateTime = now;

                double deltaTime = Math.min(deltaMs / 1000.0, MAX_DELTA_TIME);

                if (deltaTime > 0) {
                    model.update(deltaTime);
                }
            }
        }, 0, 16);
    }

    public void stop() {
        isRunning = false;
        timer.cancel();
    }
}