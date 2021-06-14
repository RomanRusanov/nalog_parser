package threadpool;

import net.jcip.annotations.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Roman Rusanov
 * @version 0.1
 * @since 02.10.2020
 * email roman9628@gmail.com
 * The class implements thread pool with blocking queue.
 */
@ThreadSafe
public class ThreadPool {
    /**
     * The instance with logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ThreadPool.class.getName());
    /**
     * The marker for logger.
     */
    private static final Marker MARKER = MarkerFactory.getMarker("Pool");
    /**
     * The field contain list with all threads in pool.
     */
    private final List<Thread> threads = new LinkedList<>();
    /**
     * The field contain max numbers of threads used.
     */
    private final int sizeThreadPool = Runtime.getRuntime().availableProcessors() - 1;
    /**
     * The instance implement thread safe blocking queue.
     */
    private final SimpleBlockingQueue<Runnable> tasks = new SimpleBlockingQueue<>(sizeThreadPool);

    /**
     * The default constructor.
     */
    public ThreadPool() {
        this.init();
        this.threads.forEach(Thread::start);
    }

    /**
     * The method add instance to run.
     * @param job the instance that implements Runnable interface.
     */
    public void work(Runnable job) {
        this.tasks.offer(job);
    }

    /**
     * The method interrupt all threads in poll.
     */
    public void shutdown() {
        this.threads.forEach(Thread::interrupt);
    }

    public void complete() {
        System.out.println("Thread pool size: " + sizeThreadPool);
        AtomicInteger counterCompletedThreads = new AtomicInteger(0);
        while (counterCompletedThreads.get() < sizeThreadPool) {
            threads.forEach(thread -> {
                if (thread.getState().equals(Thread.State.WAITING)) {
                    thread.interrupt();
                } else {
                    System.out.println("Clear: " + counterCompletedThreads.getAndSet(0));
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }
        System.out.println("Complete: " + counterCompletedThreads.get());
        this.shutdown();
    }

    /**
     * The method add thread in list.
     * Each thread run instance that tasks.poll return.
     * If tasks is empty then pool method wait.
     * When pool not need all threads in list interrupted.
     */
    private void init() {
        for (int i = 0; i < this.sizeThreadPool; i++) {
            this.threads.add(new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        new Thread(this.tasks.poll()).start();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }));
        }
    }
}