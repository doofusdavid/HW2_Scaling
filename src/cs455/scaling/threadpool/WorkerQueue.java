package cs455.scaling.threadpool;

import cs455.scaling.messaging.WorkMessage;

import java.util.LinkedList;


public class WorkerQueue
{
    private final LinkedList<WorkMessage> queue = new LinkedList<>();

    public WorkerQueue()
    {
    }

    public synchronized void enqueue(WorkMessage work) throws InterruptedException
    {
        if (this.queue.size() == 0)
            notifyAll();

        this.queue.add(work);
    }

    public synchronized WorkMessage dequeue() throws InterruptedException
    {
        while (this.queue.size() == 0)
        {
            wait();
        }
        return this.queue.remove(0);
    }
}
