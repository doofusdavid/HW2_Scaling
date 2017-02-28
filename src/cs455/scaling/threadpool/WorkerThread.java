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
 * Class representing a Thread in the ThreadPool, responsible for communication with the Client,
 * as well as adding and removing from the WorkQueue
 */
public class WorkerThread extends Thread
{
    private static final int PAYLOAD_SIZE = 8192;
    private final WorkQueue workQueue;
    private final ThreadPool threadPool;

    /**
     * Constructor giving the Thread knowledge of the WorkQueue and the ThreadPool
     *
     * @param workQueue  Shared Workqueue between all WorkerThreads
     * @param threadPool Shared ThreadPool containing other WorkerThreads
     */
    public WorkerThread(WorkQueue workQueue, ThreadPool threadPool)
    {
        this.workQueue = workQueue;
        this.threadPool = threadPool;
    }

    /**
     * Repeat the Read/Hash/Write loop until interrupted.
     */
    @Override
    public void run()
    {
        while (!Thread.interrupted())
        {
            WorkItem work = null;
            try
            {
                // WorkQueue blocks until a WorkItem is available
                work = workQueue.dequeue();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            try
            {
                // Find out which type of WorkItem it is, and proceed accordingly
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
                System.out.println("Error in WorkerThread: " + e.getMessage());
            }
        }
    }

    /**
     * Read in from a Client
     * @param work The WorkItem which contains the Key needed to identify the Client
     * @throws IOException In case of error reading from client
     * @throws InterruptedException If interrupted while Enqueuing WorkItem
     */
    private void processRead(ReadWorkItem work) throws IOException, InterruptedException
    {
        SelectionKey workKey = work.getKey();
        SocketChannel channel = (SocketChannel) workKey.channel();
        ByteBuffer readBuffer = ByteBuffer.allocate(PAYLOAD_SIZE);

        //this.threadPool.getSelector().select();

        // Attempt to read off the channel
        int numRead = 0;
        while (readBuffer.hasRemaining() && numRead != -1)
        {
            numRead = channel.read(readBuffer);
        }
        readBuffer.rewind();

        if (numRead == -1)
        {
            // Client Connection shutdown
            work.getKey().channel().close();
            work.getKey().cancel();
            this.threadPool.decrementTotalConnectedClients();
            return;
        }

        HashWorkItem hashWorkItem = new HashWorkItem(work.getKey(), readBuffer.array());

        // Enqueue the new WorkItem containing the byte array from the Client
        this.workQueue.enqueue(hashWorkItem);
        this.threadPool.incrementTotalConnections();

        // register interest in Writing
        work.getKey().interestOps(SelectionKey.OP_WRITE);
    }

    /**
     * Process a HashWorkItem, enqueueing the resulting WorkITem
     * @param work Contains the byte array to hash
     */
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

    /**
     * Process a WriteWorkItem, sending back to the Client the generated Hash Value
     * @param work WriteWorkItem containing the Hash Value to return
     * @throws InterruptedException If interrupted while enqueueing the ReadWorkItem
     */
    private void processWrite(WriteWorkItem work) throws InterruptedException
    {
        SocketChannel channel = (SocketChannel) work.getKey().channel();

        // Hash value can be < 40 characters, so we want to pad it to get up to 40, as that's what the client is expecting back
        String paddedString = String.format("%40s", work.getHashValue());

        ByteBuffer padBuffer = ByteBuffer.wrap(paddedString.getBytes());

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
}
