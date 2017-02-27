package cs455.scaling.server;

import cs455.scaling.threadpool.ThreadPool;

import java.util.TimerTask;

public class ServerStatistics extends TimerTask
{
    private ThreadPool threadPool;
    private int previousConnections;
    private int workQueueSize;

    public ServerStatistics(ThreadPool threadPool)
    {
        this.threadPool = threadPool;
        previousConnections = this.threadPool.getTotalConnections();
    }

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
