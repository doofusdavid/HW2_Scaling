package cs455.scaling.threadpool;

import java.util.ArrayList;

public class ThreadPool
{
    private final WorkerQueue workQueue;
    private final ArrayList<WorkerThread> threads;

    public ThreadPool(int threadCount, WorkerQueue workQueue)
    {
        this.threads = new ArrayList<>(threadCount);
        this.workQueue = workQueue;

        for (int i = 0; i < threadCount; i++)
        {
            threads.add(new WorkerThread(this.workQueue));
        }
        for (WorkerThread thread : threads)
        {
            thread.start();
        }
    }
}
