package cs455.scaling.threadpool;

import java.util.ArrayList;
import java.util.List;

public class ThreadPool
{
    private WorkerQueue workQueue;
    private List<WorkerThread> threads;

    public ThreadPool(int threadCount, WorkerQueue workQueue)
    {
        this.threads = new ArrayList<>(threadCount);
        this.workQueue = workQueue;

        for (int i = 0; i < threadCount; i++)
        {
            threads.add(new WorkerThread(workQueue));
        }
        for (WorkerThread thread : threads)
        {
            thread.start();
        }
    }
}
