package cs455.scaling.client;

import cs455.scaling.messaging.WorkMessage;
import cs455.scaling.transport.TCPSenderThread;
import cs455.scaling.util.ServerHashCode;

/**
 * Created by david on 2/20/17.
 */
public class ClientDataSenderThread implements Runnable
{
    private final Client client;
    private final String clientIPAddress;
    private final int clientPort;
    private final String serverIPAddress;
    private final int serverPort;
    private int messageRate;

    public ClientDataSenderThread(Client client)
    {
        this.client = client;
        this.clientIPAddress = client.getClientIPAddress();
        this.clientPort = client.getClientPort();
        this.serverIPAddress = client.getServerHost();
        this.serverPort = client.getServerPort();
        this.messageRate = client.getMessageRate();
    }

    @Override
    public void run()
    {
        while (!Thread.currentThread().isInterrupted())
        {
            WorkMessage workMessage = new WorkMessage(client.getClientIPAddress(), this.clientPort);

            // Add the hashcode to the list so we can verify later
            client.addSentHashCode(ServerHashCode.SHA1FromBytes(workMessage.getPayload()));
            TCPSenderThread sender = new TCPSenderThread(this.serverIPAddress, this.serverPort, workMessage);
            Thread t = new Thread(sender);
            t.start();
            client.incrementTotalSentCount();
            try
            {
                Thread.sleep(1000 / this.messageRate);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

    }
}
