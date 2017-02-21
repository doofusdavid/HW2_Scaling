package cs455.scaling.threadpool;

import cs455.scaling.messaging.WorkMessage;
import cs455.scaling.messaging.WorkMessageResponse;
import cs455.scaling.transport.TCPSenderThread;
import cs455.scaling.util.ServerHashCode;

/**
 * Created by david on 2/19/17.
 */
public class WorkerThread extends Thread
{
    private final WorkerQueue workQueue;
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
                // Blocks until available
                WorkMessage work = workQueue.dequeue();
                String hashValue = ServerHashCode.SHA1FromBytes(work.getPayload());
                WorkMessageResponse response = new WorkMessageResponse(hashValue);
                TCPSenderThread senderThread = new TCPSenderThread(work.getClientIPAddress(), work.getClientPort(), response);
                Thread t = new Thread(senderThread);
                t.start();
                //System.out.println("WorkerThread Completed Task");
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
