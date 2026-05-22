package log;

import utils.collections.CyclicLogQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class LogWindowSource implements Iterable<LogEntry> {
    private final CyclicLogQueue<LogEntry> entryQueue;
    private final List<LogChangeListener> subscriberList = new ArrayList<>();
    private volatile LogChangeListener[] cachedSubscribers;

    public LogWindowSource(int maxLength) {
        this.entryQueue = new CyclicLogQueue<>(maxLength);
    }

    public void registerListener(LogChangeListener target) {
        synchronized(subscriberList) {
            subscriberList.add(target);
            cachedSubscribers = null;
        }
    }

    public void unregisterListener(LogChangeListener target) {
        synchronized(subscriberList) {
            subscriberList.remove(target);
            cachedSubscribers = null;
        }
    }

    public void append(LogLevel severity, String text) {
        entryQueue.offer(new LogEntry(severity, text));
        dispatchUpdate();
    }

    private void dispatchUpdate() {
        LogChangeListener[] active = cachedSubscribers;
        if (active == null) {
            synchronized(subscriberList) {
                if (cachedSubscribers == null) {
                    active = subscriberList.toArray(new LogChangeListener[0]);
                    cachedSubscribers = active;
                }
            }
        }
        for (LogChangeListener sub : active) {
            try { sub.onLogChanged(); } catch (Exception ignored) {}
        }
    }

    public int size() { return entryQueue.size(); }
    public Iterable<LogEntry> all() { return entryQueue; }
    public Iterable<LogEntry> range(int startIndex, int count) {
        return entryQueue.fetchSegment(startIndex, count);
    }

    @Override
    public Iterator<LogEntry> iterator() { return entryQueue.iterator(); }
}