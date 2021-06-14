package threadpool;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Roman Rusanov
 * @version 0.1
 * @since 30.09.2020
 * email roman9628@gmail.com
 * The class implements thread safe queue. FIFO order for extraction.
 * @param <T> The type that queue store.
 */
@ThreadSafe
public class SimpleBlockingQueue<T> {
    /**
     * Monitor for synchronize.
     */
    @GuardedBy("this")
    /**
     * The field contain linked list.
     */
    private final Queue<T> queue = new LinkedList<>();
    /**
     * The field contain max value queue size.
     */
    private final int sizeQueue;

    /**
     * The default constructor.
     * @param sizeQueue Max queue size.
     */
    public SimpleBlockingQueue(int sizeQueue) {
        this.sizeQueue = sizeQueue;
    }

    /**
     * The method add element to queue.
     * @param value Element to store.
     */
    public synchronized void offer(T value) {
        while (this.queue.size() == this.sizeQueue) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
        this.queue.offer(value);
        notifyAll();
    }

    /**
     * The method get element from queue.
     * @return Element.
     * @throws InterruptedException wait may throw.
     */
    public synchronized T poll() throws InterruptedException {
        while (this.queue.isEmpty()) {
            wait();
        }
        notifyAll();
        return this.queue.poll();
    }

    /**
     * The method get current queue size.
     * @return Int value.
     */
    public synchronized int getSizeQueue() {
        return this.queue.size();
    }

    /**
     * The method check queue contain elements.
     * @return If queue not empty return true, otherwise false.
     */
    public synchronized boolean isEmpty() {
        return this.queue.isEmpty();
    }
}