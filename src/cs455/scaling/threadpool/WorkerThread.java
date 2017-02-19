package cs455.scaling.threadpool;

/**
 * Created by david on 2/19/17.
 */
public class WorkerThread extends Thread
{
    private WorkerQueue workQueue;
    private boolean isStopped;

    public WorkerThread(WorkerQueue workQueue)
    {
        this.workQueue = workQueue;
        this.isStopped = false;
    }

    @Override
    public void run()
    {
        while (!isStopped)
        {
            try
            {

            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public synchronized void stopThread()
    {
        isStopped = true;
        this.interrupt();
    }

    public synchronized boolean isStopped()
    {
        return isStopped;
    }
}
