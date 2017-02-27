package cs455.scaling.threadpool;

import cs455.scaling.util.ServerHashCode;
import cs455.scaling.work.HashWorkItem;
import cs455.scaling.work.ReadWorkItem;
import cs455.scaling.work.WorkItem;
import cs455.scaling.work.WriteWorkItem;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Created by david on 2/19/17.
 */
public class WorkerThread extends Thread
{
    private final WorkQueue workQueue;
    private final ThreadPool threadPool;
    private final int PAYLOAD_SIZE = 8192;
    private boolean isStopped;

    public WorkerThread(WorkQueue workQueue, ThreadPool threadPool)
    {
        this.workQueue = workQueue;
        this.threadPool = threadPool;
        this.isStopped = false;
    }

    @Override
    public void run()
    {
        while (!isStopped)
        {

            WorkItem work = null;
            try
            {
                work = workQueue.dequeue();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            try
            {
                if (work instanceof ReadWorkItem)
                {
                    this.processRead((ReadWorkItem) work);
                } else if (work instanceof HashWorkItem)
                {
                    this.processHash((HashWorkItem) work);
                } else if (work instanceof WriteWorkItem)
                {
                    this.processWrite((WriteWorkItem) work);
                } else
                {
                    throw new IllegalArgumentException("Unknown WorkItem Type");
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
    }

    private void processRead(ReadWorkItem work) throws IOException, InterruptedException
    {
        SelectionKey workKey = work.getKey();
        SocketChannel channel = (SocketChannel) workKey.channel();
        ByteBuffer readBuffer = ByteBuffer.allocate(PAYLOAD_SIZE);

        //this.threadPool.getSelector().select();

        // Attempt to read off the channel
        int numRead = 0;
        try
        {
            while (readBuffer.hasRemaining() && numRead != -1)
            {
                numRead = channel.read(readBuffer);
            }
            readBuffer.rewind();
        }
        catch (IOException e)
        {
            // The remote forcibly closed the connection, cancel
            // the selection key and close the channel.
            work.getKey().cancel();
            channel.close();
            this.threadPool.decrementTotalConnectedClients();
            return;
        }

        if (numRead == -1)
        {
            // Client Connection shutdown
            work.getKey().channel().close();
            work.getKey().cancel();
            this.threadPool.decrementTotalConnectedClients();
            return;
        }
        HashWorkItem hashWorkItem = new HashWorkItem(work.getKey(), readBuffer.array());
        readBuffer.rewind();
        try
        {
            this.workQueue.enqueue(hashWorkItem);
            this.threadPool.incrementTotalConnections();

            SelectionKey key = channel.register(work.getKey().selector(), SelectionKey.OP_WRITE);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    private void processHash(HashWorkItem work)
    {
        String hashValue = ServerHashCode.SHA1FromBytes(work.getPayload());
        try
        {
            // Add the hashed value back to the queue for response.
            this.workQueue.enqueue(new WriteWorkItem(work.getKey(), hashValue));
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    private void processWrite(WriteWorkItem work) throws InterruptedException
    {
        SocketChannel channel = (SocketChannel) work.getKey().channel();
        String paddedString = String.format("%40s", work.getHashValue());
//        System.out.println("Hash to send ("+paddedString.length()+"): " + paddedString);
        //System.out.println("Hash to send ("+work.getHashValue().length()+"): " + work.getHashValue());
        ByteBuffer padBuffer = ByteBuffer.wrap(paddedString.getBytes());
        //ByteBuffer buffer = ByteBuffer.wrap(work.getHashValue().getBytes());

        try
        {
            padBuffer.rewind();
            while (padBuffer.hasRemaining())
                channel.write(padBuffer);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        this.workQueue.enqueue(new ReadWorkItem(work.getKey()));
        work.getKey().interestOps(SelectionKey.OP_READ);


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
