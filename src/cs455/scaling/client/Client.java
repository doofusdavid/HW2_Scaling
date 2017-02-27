package cs455.scaling.client;


import java.io.IOException;
import java.net.InetAddress;
import java.util.Timer;

public class Client
{
    private int totalSentCount;
    private int totalReceivedCount;

    public Client(InetAddress serverHost, int serverPort, int messageRate)
    {
        SentHashList sentHashCodes = new SentHashList();
        try
        {
            NIOClientSender clientThread = new NIOClientSender(serverHost, serverPort, this, messageRate, sentHashCodes);
            Thread t = new Thread(clientThread);
            t.start();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }

        ClientStatistics clientStats = new ClientStatistics(this);
        Timer timer = new Timer();
        timer.schedule(clientStats, 0, 10000);
    }

    public static void main(String[] args)
    {
        if (args.length != 3)
        {
            System.out.println("Invalid arguments.\nExpected server-host server-port message-rate");
        }

        InetAddress host;
        int port = 0;
        int rate = 0;
        try
        {
            host = InetAddress.getByName(args[0]);
            port = Integer.parseInt(args[1]);
            rate = Integer.parseInt(args[2]);
            Client client = new Client(host, port, rate);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Invalid Arguments.");
            System.exit(0);
        }

    }

    synchronized void incrementTotalSentCount()
    {
        totalSentCount++;
    }

    synchronized void incrementTotalReceivedCount()
    {
        totalReceivedCount++;
    }

    synchronized int getTotalSentCount()
    {
        return totalSentCount;
    }

    synchronized int getTotalReceivedCount()
    {
        return totalReceivedCount;
    }
}
