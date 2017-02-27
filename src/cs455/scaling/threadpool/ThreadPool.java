package cs455.scaling.threadpool;

import java.util.ArrayList;

public class ThreadPool
{
    private final WorkQueue workQueue;
    private final ArrayList<WorkerThread> threads;
    private int totalConnections;
    private int totalConnectedClients;


    public ThreadPool(int threadCount, WorkQueue workQueue)
    {
        this.threads = new ArrayList<>(threadCount);
        this.workQueue = workQueue;

        for (int i = 0; i < threadCount; i++)
        {
            threads.add(new WorkerThread(this.workQueue, this));
        }
        for (WorkerThread thread : threads)
        {
            thread.start();
        }
    }

    synchronized public int getTotalConnections()
    {
        return totalConnections;
    }

    synchronized public void incrementTotalConnections()
    {
        this.totalConnections++;
    }

    synchronized public int getWorkQueueSize()
    {
        return this.workQueue.getSize();
    }

    synchronized public void incrementTotalConnectedClients()
    {
        this.totalConnectedClients++;
    }

    synchronized public void decrementTotalConnectedClients()
    {
        this.totalConnectedClients--;
    }

    synchronized public int getTotalConnectedClients()
    {
        return this.totalConnectedClients;
    }

}
