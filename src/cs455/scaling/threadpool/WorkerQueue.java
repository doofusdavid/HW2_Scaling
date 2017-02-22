package cs455.scaling.threadpool;

import cs455.scaling.messaging.WorkMessage;

import java.util.LinkedList;

/**
 * Thread-safe Queue, allowing the Server to add items, and the Threadpool to remove them.
 */
public class WorkerQueue
{
    private final LinkedList<WorkMessage> queue = new LinkedList<>();

    public WorkerQueue()
    {
    }

    /**
     * Enqueues an instance of work.
     *
     * @param work
     * @throws InterruptedException
     */
    public synchronized void enqueue(WorkMessage work) throws InterruptedException
    {
        if (this.queue.size() == 0)
            notifyAll();

        this.queue.add(work);
    }

    /**
     * Dequeues an instance of work.  Blocks if the queue is currently empty
     * @return the instance of work
     * @throws InterruptedException
     */
    public synchronized WorkMessage dequeue() throws InterruptedException
    {
        while (this.queue.size() == 0)
        {
            wait();
        }
        return this.queue.remove(0);
    }

    public int getSize()
    {
        return queue.size();
    }
}
