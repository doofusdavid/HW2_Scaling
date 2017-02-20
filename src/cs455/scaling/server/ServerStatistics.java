package cs455.scaling.server;

import java.util.TimerTask;

public class ServerStatistics extends TimerTask
{
    private Server server;
    private int previousConnections;

    public ServerStatistics(Server server)
    {
        this.server = server;
        previousConnections = server.getTotalConnections();
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
        System.out.println(String.format("%.1f messages/s, Active Client Connections: %d", connectionsPerSecond, server.getTotalConnectedClients()));
    }
}
