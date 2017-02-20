package cs455.scaling.client;


import java.util.TimerTask;

public class ClientStatistics extends TimerTask
{
    private Client client;

    public ClientStatistics(Client client)
    {
        this.client = client;
    }

    @Override
    public void run()
    {
        System.out.print(System.nanoTime() + " ");
        System.out.print("Total Sent Count: " + client.getTotalSentCount() + " ");
        System.out.println("Total Received Count: " + client.getTotalReceivedCount());
    }
}
