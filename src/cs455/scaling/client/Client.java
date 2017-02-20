package cs455.scaling.client;


import cs455.scaling.messaging.Message;
import cs455.scaling.messaging.WorkMessageResponse;
import cs455.scaling.node.Node;

import java.net.InetAddress;

public class Client implements Node
{
    private final String serverHost;
    private final int serverPort;
    private final int messageRate;
    private final SentHashList sentHashCodes;
    private int totalSentCount;
    private int totalReceivedCount;

    public Client(String serverHost, int serverPort, int messageRate)
    {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.messageRate = messageRate;
        sentHashCodes = new SentHashList();
    }

    public static void main(String[] args)
    {
        if (args.length != 3)
        {
            System.out.println("Invalid arguments.\nExpected server-host server-port message-rate");
        }

        try
        {
            String host = InetAddress.getByName(args[0]).toString();
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
    }

    private void ReceiveWorkMessageResponse(WorkMessageResponse message)
    {
        String hashResponse = message.getHashValue();
        if (sentHashCodes.remove(hashResponse))
        {
            // only increment if we confirm removal
            incrementTotalReceivedCount();
        }
    }
}
