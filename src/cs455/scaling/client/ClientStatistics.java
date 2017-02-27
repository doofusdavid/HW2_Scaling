package cs455.scaling.client;


import java.util.TimerTask;

/**
 * Class which monitors and displays statistics about the running Client
 */
public class ClientStatistics extends TimerTask
{
    private final Client client;

    /**
     * Constructor stores the Client to monitor.
     *
     * @param client
     */
    public ClientStatistics(Client client)
    {
        this.client = client;
    }

    /**
     * Queries the client to find the total number of messages sent and received, then
     * displays them to System.out.
     */
    @Override
    public void run()
    {
        System.out.print(System.nanoTime() + " ");
        System.out.print("Total Sent Count: " + client.getTotalSentCount() + " ");
        System.out.println("Total Received Count: " + client.getTotalReceivedCount());
    }
}
