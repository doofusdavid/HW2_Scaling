package cs455.scaling.threadpool;

import cs455.scaling.work.WorkItem;

import java.util.LinkedList;

/**
 * Thread-safe Queue, allowing the Server to add items, and the Threadpool to remove them.
 */
public class WorkQueue
{
    private final LinkedList<WorkItem> queue = new LinkedList<>();

    public WorkQueue()
    {
    }

    /**
     * Enqueues an instance of work.
     *
     * @param work a WorkItem to add to the Queue
     * @throws InterruptedException
     */
    public synchronized void enqueue(WorkItem work) throws InterruptedException
    {
        if (this.queue.size() == 0)
            notifyAll();

        this.queue.add(work);
    }

    /**
     * Dequeues an instance of work.  Blocks if the queue is currently empty
     * @return WorkItem at the head of the Queue
     * @throws InterruptedException
     */
    public synchronized WorkItem dequeue() throws InterruptedException
    {
        while (this.queue.size() == 0)
        {
            wait();
        }
        return this.queue.remove(0);
    }

    /**
     * Gets the size of the Queue for statistics
     *
     * @return
     */
    synchronized int getSize()
    {
        return queue.size();
    }
}
