package cs455.scaling.client;


import cs455.scaling.messaging.*;
import cs455.scaling.node.Node;
import cs455.scaling.transport.TCPReceiverThread;
import cs455.scaling.transport.TCPSenderThread;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Timer;

public class Client implements Node
{
    private final String serverHost;
    private final int serverPort;
    private final int messageRate;
    private final SentHashList sentHashCodes;
    private String clientIPAddress;
    private int clientPort;
    private TCPReceiverThread receiverThread;
    private int totalSentCount;
    private int totalReceivedCount;
    public Client(String serverHost, int serverPort, int messageRate)
    {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.messageRate = messageRate;
        sentHashCodes = new SentHashList();
        try
        {
            receiverThread = new TCPReceiverThread(0, this);
            this.clientPort = receiverThread.getPort();
            this.clientIPAddress = InetAddress.getLocalHost().getHostName().toString();
            Thread t = new Thread(receiverThread);
            t.start();
        } catch (IOException ioe)
        {
            ioe.printStackTrace();
        }

        this.SendConnectionRequestToServer();
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

        String host = "";
        int port = 0;
        int rate = 0;
        try
        {
            host = InetAddress.getByName(args[0]).getHostName().toString();
            port = Integer.parseInt(args[1]);
            rate = Integer.parseInt(args[2]);
        }
        catch (Exception e)
        {
            //e.printStackTrace();
            System.out.println("Invalid Arguments.");
            System.exit(0);
        }

        Client client = new Client(host, port, rate);
    }

    public int getMessageRate()
    {
        return messageRate;
    }

    public String getServerHost()
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

    private void SendConnectionRequestToServer()
    {
        ServerConnectRequest request = new ServerConnectRequest(this.clientIPAddress, this.clientPort);

        TCPSenderThread senderThread = new TCPSenderThread(this.serverHost, this.serverPort, request);
        Thread t = new Thread(senderThread);
        t.start();
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

    @Override
    public void onEvent(Message message)
    {
        if (message instanceof WorkMessageResponse)
        {
            this.ReceiveWorkMessageResponse((WorkMessageResponse) message);
        }
        if (message instanceof ServerConnectResponse)
        {
            this.ReceiveServerConnectResponse((ServerConnectResponse) message);
        }
    }

    private void ReceiveServerConnectResponse(ServerConnectResponse message)
    {
        if (message.getStatusCode() == StatusCode.SUCCESS)
        {
            System.out.println("Connected to Server!");
        }

        this.SendDataToServer();
    }

    private void SendDataToServer()
    {
        ClientDataSenderThread senderThread = new ClientDataSenderThread(this);
        Thread t = new Thread(senderThread);
        t.start();
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
