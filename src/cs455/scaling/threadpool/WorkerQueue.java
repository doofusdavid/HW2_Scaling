package cs455.scaling.threadpool;

import cs455.scaling.messaging.WorkMessage;

import java.util.concurrent.ConcurrentLinkedDeque;

public class WorkerQueue
{
    private final ConcurrentLinkedDeque<WorkMessage> queue = new ConcurrentLinkedDeque();

    public WorkerQueue()
    {
    }

    public void add(WorkMessage work)
    {
        if (work != null)
            queue.add(work);
    }

    public WorkMessage remove()
    {
        return queue.poll();
    }

}
