package cs455.scaling.client;

import cs455.scaling.util.ServerHashCode;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Random;

/**
 * Class responsible for establishing a connection to a Server, then sending and receiving data until shut down.
 */
public class ClientSender implements Runnable
{
    public static final int PAYLOAD_SIZE = 8192;
    private final InetAddress serverAddress;
    private final int serverPort;
    private final Client client;
    private final int sendRate;
    private final SentHashList sentHashCodes;
    private Selector selector;

    /**
     * Creates and initializes a ClientSender.
     *
     * @param serverAddress InetAddress of the Server
     * @param serverPort    Port to connect to Server on
     * @param client        Client which maintains statistics on connections.
     * @param sendRate      Number of transmissions to send to the Server per second.
     */
    public ClientSender(InetAddress serverAddress, int serverPort, Client client, int sendRate) throws IOException
    {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.client = client;
        this.sendRate = sendRate;
        this.sentHashCodes = new SentHashList();
    }

    @Override
    public void run()
    {
        try
        {
            // Establish connection to server and register interest in Writing
            this.selector = SelectorProvider.provider().openSelector();
            SocketChannel socketChannel = this.initiateConnection();
            socketChannel.configureBlocking(false);
            socketChannel.register(this.selector, SelectionKey.OP_WRITE);
        }
        catch (IOException ioe)
        {
            System.out.println("Error connecting to client.");
        }

        // Loop until interrupted, sending data.
        while (!Thread.interrupted())
        {
            try
            {

                // Wait for an event one of the registered channels
                this.selector.select();

                // Iterate over the set of keys for which events are available
                Iterator selectedKeys = this.selector.selectedKeys().iterator();
                while (selectedKeys.hasNext())
                {
                    SelectionKey key = (SelectionKey) selectedKeys.next();
                    selectedKeys.remove();

                    if (!key.isValid())
                    {
                        continue;
                    }

                    // Check what event is available and deal with it
                    if (key.isConnectable())
                    {
                        this.completeConnection(key);
                    } else if (key.isReadable())
                    {
                        this.read(key);
                    } else if (key.isWritable())
                    {
                        this.write(key);
                    }
                }

            }
            catch (Exception e)
            {
                System.out.println("Error in Client.  Disconnecting.");
            }
        }

    }

    /**
     * Get and write a byte array to the server.  Then, sleep for the sendRate time.
     * @param key SelectionKey representing the Server
     * @throws IOException If connection fails
     * @throws InterruptedException If Thread.sleep is interrupted
     */
    private void write(SelectionKey key) throws IOException, InterruptedException
    {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        byte[] payload = this.getPayload();
        ByteBuffer buf = ByteBuffer.wrap(payload);
        buf.rewind();

        while (buf.hasRemaining())
            socketChannel.write(buf);

        this.saveHashValue(payload);

        // After writing, register an interest in Reading.
        key.interestOps(SelectionKey.OP_READ);

        this.client.incrementTotalSentCount();

        // Sleep for the given time
        Thread.sleep(1000 / sendRate);

    }

    /**
     * Generate Hash Value from the byte array and save it to the sent Hash list.
     * @param payload Byte Array containing the payload to hash.
     */
    private void saveHashValue(byte[] payload)
    {
        String hashValue = ServerHashCode.SHA1FromBytes(payload);
        //System.out.println("Sent hash ("+ hashValue.length()+"):" + hashValue);

        sentHashCodes.add(hashValue);
    }

    /**
     * read a response from the Server
     * @param key Key representing the Server
     * @throws IOException If the read fails
     */
    private void read(SelectionKey key) throws IOException
    {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        ByteBuffer readBuffer = ByteBuffer.allocate(40);

        int numRead = 0;
        try
        {
            while (readBuffer.hasRemaining() && numRead != -1)
                numRead = socketChannel.read(readBuffer);

            readBuffer.rewind();
        }
        catch (IOException e)
        {
            // remote closed connection
            key.cancel();
            socketChannel.close();
            return;
        }

        if (numRead == -1)
        {
            // client connection shutdown
            key.channel().close();
            key.cancel();
            System.exit(0);
        }

        String hashedValue = new String(readBuffer.array());

        if (sentHashCodes.remove(hashedValue.trim()))
        {
            this.client.incrementTotalReceivedCount();
            //System.out.println("Received Work Confirmation");
        } else
        {
            System.out.println("Work not confirmed");
        }
        // After reading, register an interest in Writing
        key.interestOps(SelectionKey.OP_WRITE);
    }

    /**
     * Initiate the connection to the Server
     * @return The SocketChannel to the Server
     * @throws IOException If connection fails
     */
    private SocketChannel initiateConnection() throws IOException
    {
        // Create a non-blocking socket channel
        SocketChannel socketChannel = SocketChannel.open();
        //socketChannel.configureBlocking(false);

        // Kick off connection establishment...without configureBlocking, it should wait.
        socketChannel.connect(new InetSocketAddress(this.serverAddress, this.serverPort));
        return socketChannel;

    }

    /**
     * Finish the connection to the Server
     *
     * @param key SelectionKey representing Server
     */
    private void completeConnection(SelectionKey key) throws IOException
    {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        socketChannel.finishConnect();

        // We're ready to write
        key.interestOps(SelectionKey.OP_WRITE);
    }

    /**
     * Generate a random byte array payload
     *
     * @return Payload of random data.
     */
    private byte[] getPayload()
    {
        Random random = new Random();
        byte[] payload = new byte[PAYLOAD_SIZE];
        random.nextBytes(payload);
        return payload;
    }
}
