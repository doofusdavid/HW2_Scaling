package cs455.scaling.client;


import cs455.scaling.messaging.WorkMessageResponse;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Timer;

public class Client
{
    private final InetAddress serverHost;
    private final int serverPort;
    private final int messageRate;
    private final SentHashList sentHashCodes;
    private String clientIPAddress;
    private int clientPort;
    private NIOClientSender clientThread;
    private int totalSentCount;
    private int totalReceivedCount;

    public Client(InetAddress serverHost, int serverPort, int messageRate)
    {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.messageRate = messageRate;
        this.sentHashCodes = new SentHashList();
        try
        {
            clientThread = new NIOClientSender(this.serverHost, this.serverPort, this, this.messageRate, this.sentHashCodes);
            this.clientIPAddress = InetAddress.getLocalHost().getHostName().toString();
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

    public int getMessageRate()
    {
        return messageRate;
    }

    public InetAddress getServerHost()
    {
        return serverHost;
    }

    public int getServerPort()
    {
        return serverPort;
    }

    public String getClientIPAddress()
    {
        return clientIPAddress;
    }

    public int getClientPort()
    {
        return clientPort;
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

    synchronized void addSentHashCode(String hashValue)
    {
        sentHashCodes.add(hashValue);
    }

    private void ReceiveWorkMessageResponse(WorkMessageResponse message)
    {
        String hashResponse = message.getHashValue();
        if (sentHashCodes.remove(hashResponse))
        {
            // only increment if we confirm removal
            incrementTotalReceivedCount();
//            System.out.println("Recived work confirmation");
        }
    }
}
