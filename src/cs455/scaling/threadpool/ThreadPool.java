package cs455.scaling.threadpool;

import java.util.ArrayList;
import java.util.List;

public class ThreadPool
{
    private WorkerQueue workQueue;
    private List<WorkerThread> threads;

    public ThreadPool(int threadCount)
    {
        threads = new ArrayList<>(threadCount);
        workQueue = new WorkerQueue();

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
