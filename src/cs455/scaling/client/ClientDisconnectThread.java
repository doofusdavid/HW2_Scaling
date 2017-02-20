package cs455.scaling.client;

import cs455.scaling.messaging.ServerDisconnectRequest;
import cs455.scaling.transport.TCPSenderThread;

/**
 * ClientDisconnectThread attempts to disconnect from the Server gracefully, decrementing the total connected client count.
 */
public class ClientDisconnectThread implements Runnable
{
    private final Client client;

    public ClientDisconnectThread(Client client)
    {
        this.client = client;
    }

    @Override
    public void run()
    {
        ServerDisconnectRequest disconnectRequest = new ServerDisconnectRequest(client.getClientIPAddress(), client.getClientPort());
        TCPSenderThread sender = new TCPSenderThread(client.getServerHost(), client.getServerPort(), disconnectRequest);
        Thread t = new Thread(sender);
        t.start();
    }
}
