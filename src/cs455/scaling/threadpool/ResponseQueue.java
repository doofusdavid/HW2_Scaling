package cs455.scaling.threadpool;

import cs455.scaling.messaging.WorkMessageResponse;

import java.util.LinkedList;

/**
 * Non-blocking queue for the responses sent back to clients
 */
public class ResponseQueue
{
    private final LinkedList<WorkMessageResponse> queue = new LinkedList<>();

    public ResponseQueue()
    {

    }

    public synchronized void enqueue(WorkMessageResponse response) throws InterruptedException
    {
        this.queue.add(response);
    }

    public synchronized WorkMessageResponse dequeueIfAvailable() throws InterruptedException
    {
        if (this.queue.size() > 0)
            return this.queue.remove(0);
        else
            return null;
    }

    public synchronized int getSize()
    {
        return this.queue.size();
    }
}
