package utils.collections;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
import java.util.concurrent.*;

public class CyclicLogQueueTest {

    @Test
    public void testAddAndEviction() {
        CyclicLogQueue<String> queue = new CyclicLogQueue<>(3);
        queue.offer("A");
        queue.offer("B");
        queue.offer("C");
        queue.offer("D");

        assertEquals(3, queue.size());
        assertEquals("B", queue.peek());
    }

    @Test
    public void testRangeExtraction() {
        CyclicLogQueue<Integer> queue = new CyclicLogQueue<>(10);
        for (int i = 0; i < 10; i++) {
            queue.offer(i);
        }

        List<Integer> segment = queue.fetchSegment(2, 3);
        assertEquals(Arrays.asList(2, 3, 4), segment);

        assertTrue(queue.fetchSegment(15, 5).isEmpty());
    }

    @Test
    public void testIteratorContents() {
        CyclicLogQueue<String> queue = new CyclicLogQueue<>(5);
        queue.offer("X");
        queue.offer("Y");
        queue.offer("Z");

        List<String> collected = new ArrayList<>();
        for (String s : queue) {
            collected.add(s);
        }

        assertEquals(Arrays.asList("X", "Y", "Z"), collected);
    }

    @Test
    public void testConcurrentModificationProtection() {
        CyclicLogQueue<String> queue = new CyclicLogQueue<>(5);
        queue.offer("First");

        Iterator<String> iterator = queue.iterator();
        queue.offer("Second");

        assertThrows(ConcurrentModificationException.class, iterator::next);
    }

    @Test
    public void testThreadSafety() throws InterruptedException {
        CyclicLogQueue<Integer> queue = new CyclicLogQueue<>(100);
        int threadCount = 4;
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int t = 0; t < threadCount; t++) {
            new Thread(() -> {
                try {
                    for (int i = 0; i < 2000; i++) {
                        queue.offer(i);
                        if (i % 100 == 0) {

                            try {
                                for (Integer ignored : queue) {

                                }
                            } catch (ConcurrentModificationException e) {

                            }
                            queue.fetchSegment(0, 5);
                        }
                    }
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        latch.await();


        assertTrue("Queue size exceeds capacity", queue.size() <= 100);
    }
}