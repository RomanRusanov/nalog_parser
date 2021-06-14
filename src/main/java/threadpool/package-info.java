/**
 * SimpleBlockingQueue.java The class implements thread safe queue.
 *  FIFO order for extraction.
 *  public synchronized void offer(T value) The method add element to queue.
 *  public synchronized T poll() The method get element from queue.
 *  public synchronized int getSizeQueue() The method get current queue size.
 *  public synchronized boolean isEmpty() The method check queue contain elements.
 * ThreadPool.java The class implements thread pool with blocking queue.
 *  public ThreadPool() The default constructor.
 *  public void work(Runnable job) The method add instance to run.
 *  public void shutdown() The method interrupt all threads in poll.
 *  private void init() The method add thread in list.
 */
package threadpool;