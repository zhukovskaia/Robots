package utils.collections;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CyclicLogQueue<T> extends AbstractQueue<T> {
    private final Object[] ringStorage;
    private final int ringCapacity;
    private int readPtr;
    private int itemCount;
    private volatile long stateVersion;
    private final ReentrantReadWriteLock accessGuard = new ReentrantReadWriteLock();

    @SuppressWarnings("unchecked")
    public CyclicLogQueue(int maxElements) {
        if (maxElements <= 0) throw new IllegalArgumentException("Capacity must be positive");
        this.ringCapacity = maxElements;
        this.ringStorage = new Object[maxElements];
        this.readPtr = 0;
        this.itemCount = 0;
        this.stateVersion = 0L;
    }

    @Override
    public boolean offer(T element) {
        if (element == null) throw new NullPointerException("Null elements not allowed");
        accessGuard.writeLock().lock();
        try {
            int writePos = (readPtr + itemCount) % ringCapacity;
            if (itemCount == ringCapacity) {
                readPtr = (readPtr + 1) % ringCapacity;
            } else {
                itemCount++;
            }
            ringStorage[writePos] = element;
            stateVersion++;
        } finally {
            accessGuard.writeLock().unlock();
        }
        return true;
    }

    @Override
    public T poll() {
        accessGuard.writeLock().lock();
        try {
            if (itemCount == 0) return null;
            @SuppressWarnings("unchecked")
            T removed = (T) ringStorage[readPtr];
            ringStorage[readPtr] = null;
            readPtr = (readPtr + 1) % ringCapacity;
            itemCount--;
            stateVersion++;
            return removed;
        } finally {
            accessGuard.writeLock().unlock();
        }
    }

    @Override
    public T peek() {
        accessGuard.readLock().lock();
        try {
            return itemCount == 0 ? null : (T) ringStorage[readPtr];
        } finally {
            accessGuard.readLock().unlock();
        }
    }

    @Override
    public int size() {
        accessGuard.readLock().lock();
        try { return itemCount; } finally { accessGuard.readLock().unlock(); }
    }

    public List<T> fetchSegment(int startIndex, int fetchCount) {
        accessGuard.readLock().lock();
        try {
            if (startIndex < 0 || startIndex >= itemCount) return new ArrayList<>(0);
            int actualCount = Math.min(fetchCount, itemCount - startIndex);
            List<T> snapshot = new ArrayList<>(actualCount);
            for (int step = 0; step < actualCount; step++) {
                int physicalIdx = (readPtr + startIndex + step) % ringCapacity;
                @SuppressWarnings("unchecked")
                T val = (T) ringStorage[physicalIdx];
                snapshot.add(val);
            }
            return snapshot;
        } finally {
            accessGuard.readLock().unlock();
        }
    }

    @Override
    public Iterator<T> iterator() {
        accessGuard.readLock().lock();
        try {
            return new VersionCheckedIterator(readPtr, itemCount, stateVersion);
        } finally {
            accessGuard.readLock().unlock();
        }
    }

    private class VersionCheckedIterator implements Iterator<T> {
        private final int initialPtr;
        private final int expectedCount;
        private final long expectedVersion;
        private int traversalCursor;

        VersionCheckedIterator(int ptr, int count, long ver) {
            this.initialPtr = ptr;
            this.expectedCount = count;
            this.expectedVersion = ver;
            this.traversalCursor = 0;
        }

        @Override
        public boolean hasNext() {
            return traversalCursor < expectedCount;
        }

        @Override
        @SuppressWarnings("unchecked")
        public T next() {
            if (!hasNext()) throw new NoSuchElementException();
            accessGuard.readLock().lock();
            try {
                if (stateVersion != expectedVersion) {
                    throw new ConcurrentModificationException("Queue modified during iteration");
                }
                int physicalIdx = (initialPtr + traversalCursor) % ringCapacity;
                traversalCursor++;
                return (T) ringStorage[physicalIdx];
            } finally {
                accessGuard.readLock().unlock();
            }
        }
    }
}