package cs455.scaling.client;


import java.io.IOException;
import java.net.InetAddress;
import java.util.Timer;

/**
 * Main Client class.  Launches a ClientSender thread, responsible for sending packets to the Server
 * and reconciling the returned Hash Code.  Launches a ClientStatistics timer thread to display
 * statistics regarding messages sent and received.  Maintains a count of messages sent and received.
 */
public class Client
{
    private int totalSentCount;
    private int totalReceivedCount;

    /**
     * Constructor which launches ClientSender thread, and a ClientStatistics Thread.
     *
     * @param serverHost  Address of the Server
     * @param serverPort  Port the Server is listening on
     * @param messageRate Rate that this Client should send messages (messageRate/sec)
     */
    public Client(InetAddress serverHost, int serverPort, int messageRate)
    {
        SentHashList sentHashCodes = new SentHashList();
        try
        {
            ClientSender clientThread = new ClientSender(serverHost, serverPort, this, messageRate, sentHashCodes);
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

    /**
     * Takes in parameters and instantiates a new Client.
     * @param args Arguments are the Server Host, Server Port and Message Rate (Messages/sec)
     */
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
