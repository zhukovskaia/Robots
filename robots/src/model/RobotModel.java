package model;
import java.util.ArrayList;
import java.util.List;

public class RobotModel {
    public interface Observer {
        void onStateChanged(double x, double y, double dir, int tx, int ty);
    }
    private final List<Observer> observers = new ArrayList<>();
    private volatile double x = 100, y = 100, dir = 0;
    private volatile int targetX = 150, targetY = 100;
    private static final double MAX_V = 200.0;
    private static final double MAX_W = 4.0;
    private static final double ARRIVAL_DIST = 15.0;

    public void addObserver(Observer o) { synchronized(observers) { observers.add(o); } }
    public void removeObserver(Observer o) { synchronized(observers) { observers.remove(o); } }
    private void notifyObservers() {
        List<Observer> snapshot;
        synchronized(observers) { snapshot = new ArrayList<>(observers); }
        for (Observer o : snapshot) o.onStateChanged(x, y, dir, targetX, targetY);
    }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getDir() { return dir; }
    public int getTargetX() { return targetX; }
    public int getTargetY() { return targetY; }
    public void setTarget(int tx, int ty) { targetX = tx; targetY = ty; }

    public void update(double deltaTime) {
        double dx = targetX - x, dy = targetY - y;
        double dist = Math.hypot(dx, dy);
        if (dist <= ARRIVAL_DIST) { x = targetX; y = targetY; notifyObservers(); return; }
        double angleToTarget = Math.atan2(dy, dx);
        while (angleToTarget < 0) angleToTarget += 2 * Math.PI;
        while (angleToTarget >= 2 * Math.PI) angleToTarget -= 2 * Math.PI;
        double diff = angleToTarget - dir;
        while (diff > Math.PI) diff -= 2 * Math.PI;
        while (diff < -Math.PI) diff += 2 * Math.PI;
        double linearV = MAX_V * Math.min(1.0, dist / 50.0);
        double angularV = Math.abs(diff) < 0.05 ? 0 : MAX_W * Math.signum(diff);
        if (Math.abs(angularV) < 1e-6) {
            double step = linearV * deltaTime;
            if (step >= dist) { x = targetX; y = targetY; }
            else { x += step * Math.cos(dir); y += step * Math.sin(dir); }
        } else {
            double radius = linearV / Math.abs(angularV);
            double newDir = dir + angularV * deltaTime;
            double newX = x + radius * (Math.sin(newDir) - Math.sin(dir)) * Math.signum(angularV);
            double newY = y - radius * (Math.cos(newDir) - Math.cos(dir)) * Math.signum(angularV);
            if (Math.hypot(targetX - newX, targetY - newY) <= ARRIVAL_DIST) { x = targetX; y = targetY; }
            else { x = newX; y = newY; }
            dir = newDir;
        }
        while (dir < 0) dir += 2 * Math.PI;
        while (dir >= 2 * Math.PI) dir -= 2 * Math.PI;
        notifyObservers();
    }
}