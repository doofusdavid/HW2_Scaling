package cs455.scaling.transport;

import cs455.scaling.messaging.MessageFactory;
import cs455.scaling.node.Node;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPReceiverThread implements Runnable
{
    private ServerSocket serverSocket;
    private Socket socket;
    private Node node;
    private int port;
    private DataInputStream din;

    /**
     * @param port the port number, or {@code 0} to use a port
     *             number that is automatically allocated.
     * @throws IOException In case of error, throws Exception
     */
    public TCPReceiverThread(int port, Node node) throws IOException
    {

        try
        {
            // Passing a 0 auto-allocates a port
            this.serverSocket = new ServerSocket(port, 10000);
            this.port = this.serverSocket.getLocalPort();
            this.node = node;
            //System.out.println(String.format("TCPReceiverThread running on %s:%d", InetAddress.getLocalHost().getHostAddress(), this.port));
        } catch (IOException ioe)
        {
            System.out.println(ioe.getMessage());
        }
    }

    public int getPort()
    {
        return this.port;
    }

    @Override
    public void run()
    {
        int dataLength;

        while (!Thread.currentThread().isInterrupted())
        {
            try
            {
                Socket socket = serverSocket.accept();
                din = new DataInputStream(socket.getInputStream());
                dataLength = din.readInt();

                byte[] data = new byte[dataLength];
                din.readFully(data, 0, dataLength);
                din.close();
                socket.close();
                MessageFactory mf = MessageFactory.getInstance();
                mf.FireEvent(data, this.node);

            } catch (Exception e)
            {
                System.out.println("TCPReceiverThread: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

}
