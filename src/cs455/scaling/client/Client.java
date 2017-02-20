package cs455.scaling.client;


import cs455.scaling.messaging.*;
import cs455.scaling.node.Node;
import cs455.scaling.transport.TCPReceiverThread;
import cs455.scaling.transport.TCPSenderThread;
import cs455.scaling.util.ServerHashCode;

import java.io.IOException;
import java.net.InetAddress;

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
    }

    public static void main(String[] args)
    {
        if (args.length != 3)
        {
            System.out.println("Invalid arguments.\nExpected server-host server-port message-rate");
        }

        try
        {
            String host = InetAddress.getByName(args[0]).getHostName().toString();
            int port = Integer.parseInt(args[1]);
            int rate = Integer.parseInt(args[2]);
            Client client = new Client(host, port, rate);
        } catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Invalid Arguments.");
            System.exit(0);
        }
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
        while (true)
        {
            WorkMessage workMessage = new WorkMessage(this.clientIPAddress, this.clientPort);

            // Add the hashcode to the list so we can verify later
            sentHashCodes.add(ServerHashCode.SHA1FromBytes(workMessage.getPayload()));
            TCPSenderThread sender = new TCPSenderThread(this.serverHost, this.serverPort, workMessage);
            Thread t = new Thread(sender);
            t.start();
            incrementTotalSentCount();
            try
            {
                Thread.sleep(1000 / this.messageRate);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void ReceiveWorkMessageResponse(WorkMessageResponse message)
    {
        String hashResponse = message.getHashValue();
        if (sentHashCodes.remove(hashResponse))
        {
            // only increment if we confirm removal
            incrementTotalReceivedCount();
            System.out.println("Recived work confirmation");
        }
    }
}
