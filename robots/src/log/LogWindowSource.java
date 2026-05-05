package log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogWindowSource {
    private final int maxQueueLength;
    private final List<LogEntry> messages = new ArrayList<>();
    private final List<LogChangeListener> listeners = new ArrayList<>();
    private volatile LogChangeListener[] activeListeners;

    public LogWindowSource(int queueLength) {
        this.maxQueueLength = queueLength;
    }

    public void registerListener(LogChangeListener listener) {
        synchronized(listeners) {
            listeners.add(listener);
            activeListeners = null;
        }
    }

    public void unregisterListener(LogChangeListener listener) {
        synchronized(listeners) {
            listeners.remove(listener);
            activeListeners = null;
        }
    }

    public void append(LogLevel level, String message) {
        synchronized(listeners) {
            messages.add(new LogEntry(level, message));
            while (messages.size() > maxQueueLength) messages.remove(0);
        }
        notifyListeners();
    }

    private void notifyListeners() {
        LogChangeListener[] current = activeListeners;
        if (current == null) {
            synchronized(listeners) {
                if (activeListeners == null) {
                    current = listeners.toArray(new LogChangeListener[0]);
                    activeListeners = current;
                }
            }
        }
        for (LogChangeListener l : current) {
            try { l.onLogChanged(); } catch (Exception ignored) {}
        }
    }

    public int size() { return messages.size(); }
    public Iterable<LogEntry> all() { return new ArrayList<>(messages); }
    public Iterable<LogEntry> range(int from, int count) {
        if (from < 0 || from >= messages.size()) return Collections.emptyList();
        int to = Math.min(from + count, messages.size());
        return messages.subList(from, to);
    }
}