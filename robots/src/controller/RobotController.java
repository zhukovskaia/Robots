package controller;

import model.RobotModel;

import java.util.Timer;
import java.util.TimerTask;

public class RobotController {
    private final RobotModel model;
    private final Timer timer;
    private long lastUpdateTime;
    private boolean isRunning = false;

    public RobotController(RobotModel model) {
        this.model = model;
        this.timer = new Timer("RobotController", true);
        this.lastUpdateTime = System.currentTimeMillis();
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

                double deltaTime = Math.min(deltaMs / 1000.0, 0.033);

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