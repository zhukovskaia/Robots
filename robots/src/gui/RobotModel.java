package gui;

import java.util.ArrayList;
import java.util.List;

public class RobotModel {
    public interface Observer {
        void onStateChanged(double x, double y, double dir, int tx, int ty);
    }

    private final List<Observer> observers = new ArrayList<>();
    private volatile double x = 100, y = 100, dir = 0;
    private volatile int targetX = 150, targetY = 100;

    private static final double MAX_V = 0.1;
    private static final double MAX_W = 0.001;

    public void addObserver(Observer o) {
        synchronized(observers) { observers.add(o); }
    }
    public void removeObserver(Observer o) {
        synchronized(observers) { observers.remove(o); }
    }

    private void notifyObservers() {
        List<Observer> snapshot;
        synchronized(observers) { snapshot = new ArrayList<>(observers); }
        for (Observer o : snapshot) {
            o.onStateChanged(x, y, dir, targetX, targetY);
        }
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getDir() { return dir; }
    public int getTargetX() { return targetX; }
    public int getTargetY() { return targetY; }

    public void setTarget(int tx, int ty) {
        targetX = tx;
        targetY = ty;
    }

    public void update() {
        double dx = targetX - x;
        double dy = targetY - y;
        double dist = Math.hypot(dx, dy);
        if (dist < 0.5) return;

        double angleToTarget = Math.atan2(dy, dx);
        while (angleToTarget < 0) angleToTarget += 2 * Math.PI;
        while (angleToTarget >= 2 * Math.PI) angleToTarget -= 2 * Math.PI;

        double diff = angleToTarget - dir;
        // Исправление бага: всегда поворачиваем кратчайшим путем
        while (diff > Math.PI) diff -= 2 * Math.PI;
        while (diff < -Math.PI) diff += 2 * Math.PI;

        double angularV = Math.abs(diff) < 0.01 ? 0 : Math.signum(diff) * MAX_W;
        move(MAX_V, angularV, 10);
    }

    private void move(double v, double w, double dt) {
        double newX, newY;
        if (Math.abs(w) < 1e-6) {
            newX = x + v * dt * Math.cos(dir);
            newY = y + v * dt * Math.sin(dir);
        } else {
            newX = x + (v / w) * (Math.sin(dir + w * dt) - Math.sin(dir));
            newY = y - (v / w) * (Math.cos(dir + w * dt) - Math.cos(dir));
        }
        x = newX;
        y = newY;
        dir = dir + w * dt;
        while (dir < 0) dir += 2 * Math.PI;
        while (dir >= 2 * Math.PI) dir -= 2 * Math.PI;

        notifyObservers();
        // Logger.debug(...) удален, чтобы не засорять протокол
    }
}