package cs455.scaling.threadpool;

import java.util.ArrayList;

/**
 * Responsible for instantiating Threads and monitoring their state.
 */
public class ThreadPool
{
    private final WorkQueue workQueue;
    private int totalConnections;
    private int totalConnectedClients;

    /**
     * Constructor which spins up the Threads
     *
     * @param threadCount Number of threads to start
     * @param workQueue   WorkQueue the Threads will pull from and add to.
     */
    public ThreadPool(int threadCount, WorkQueue workQueue)
    {
        ArrayList<WorkerThread> threads = new ArrayList<>(threadCount);
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

    /**
     * Gets Total number of connections among all Threads
     * @return number of connections.
     */
    synchronized public int getTotalConnections()
    {
        return totalConnections;
    }

    /**
     * Increments the number of connections
     */
    synchronized public void incrementTotalConnections()
    {
        this.totalConnections++;
    }

    /**
     * Gets the size of the WorkQueue for statistics
     * @return Number of work items in the Queue
     */
    synchronized public int getWorkQueueSize()
    {
        return this.workQueue.getSize();
    }

    /**
     * Increments the total number of connected Clients
     */
    synchronized public void incrementTotalConnectedClients()
    {
        this.totalConnectedClients++;
    }

    /**
     * Decrements the total number of connected Clients
     */
    synchronized public void decrementTotalConnectedClients()
    {
        this.totalConnectedClients--;
    }

    /**
     * Gets the total number of connected Clients
     * @return Total number of connected Clients
     */
    synchronized public int getTotalConnectedClients()
    {
        return this.totalConnectedClients;
    }

}
