package cs455.scaling.server;

import cs455.scaling.threadpool.ThreadPool;

import java.util.TimerTask;

/**
 * Class which monitors and displays statistics about the running Server
 */
public class ServerStatistics extends TimerTask
{
    private final ThreadPool threadPool;
    private int previousConnections;
    private int workQueueSize;

    /**
     * Constructor
     *
     * @param threadPool ThreadPool belonging to the Server which will be monitored.
     */
    public ServerStatistics(ThreadPool threadPool)
    {
        this.threadPool = threadPool;
        previousConnections = this.threadPool.getTotalConnections();
    }

    /**
     * Counts the difference in connections between the last call and this one.  Uses that value to determine
     * the connections per second.  Also displays the number of Active Client Connections, and the number of items
     * in the Work Queue.
     */
    @Override
    public void run()
    {
        int totalConnections = this.threadPool.getTotalConnections();
        int deltaConnections = totalConnections - this.previousConnections;
        this.previousConnections = totalConnections;
        double connectionsPerSecond = deltaConnections / 5.0;
        System.out.print(System.nanoTime() + " ");
        System.out.print("Current Server Throughput: ");
        System.out.println(String.format("%.1f messages/s, %d total messages, Active Client Connections: %d, Items in work queue: %d", connectionsPerSecond, totalConnections, this.threadPool.getTotalConnectedClients(), this.threadPool.getWorkQueueSize()));
    }
}
