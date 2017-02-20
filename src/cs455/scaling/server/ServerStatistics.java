package cs455.scaling.server;

import java.util.TimerTask;

public class ServerStatistics extends TimerTask
{
    private Server server;
    private int previousConnections;
    private int workQueueSize;

    public ServerStatistics(Server server)
    {
        this.server = server;
        previousConnections = server.getTotalConnections();
        workQueueSize = server.getWorkQueueSize();
    }

    @Override
    public void run()
    {
        int totalConnections = server.getTotalConnections();
        int deltaConnections = totalConnections - this.previousConnections;
        this.previousConnections = totalConnections;
        double connectionsPerSecond = deltaConnections / 5.0;
        System.out.print(System.nanoTime() + " ");
        System.out.print("Current Server Throughput: ");
        System.out.println(String.format("%.1f messages/s, %d total messages, Active Client Connections: %d, Items in work queue: %d", connectionsPerSecond, totalConnections, server.getTotalConnectedClients(), workQueueSize));
    }
}
