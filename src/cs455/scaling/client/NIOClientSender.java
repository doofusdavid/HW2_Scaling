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


public class NIOClientSender implements Runnable
{
    private final InetAddress serverAddress;
    private final int serverPort;
    private final Client client;
    private final Selector selector;
    private final int sendRate;
    private final int PAYLOAD_SIZE = 8192;
    private final SentHashList sentHashCodes;
    private long lastSentTime;
    private SocketChannel socketChannel;


    public NIOClientSender(InetAddress serverAddress, int serverPort, Client client, int sendRate, SentHashList sentHashCodes) throws IOException
    {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.client = client;
        this.sendRate = sendRate;
        this.sentHashCodes = sentHashCodes;
        this.selector = SelectorProvider.provider().openSelector();
        this.socketChannel = this.initiateConnection();
        this.socketChannel.configureBlocking(false);
        this.socketChannel.register(this.selector, SelectionKey.OP_WRITE);
    }

    @Override
    public void run()
    {
        while (true)
        {
            try
            {

                // Wait for an event one of the registered channels
                this.selector.select(1000 / sendRate);

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
                e.printStackTrace();
            }
        }

    }

    private void write(SelectionKey key) throws IOException
    {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        byte[] payload = this.getPayload();
        ByteBuffer buf = ByteBuffer.wrap(payload);
        socketChannel.write(buf);
        this.saveHashValue(payload);
        key.interestOps(SelectionKey.OP_READ);

    }

    private void saveHashValue(byte[] payload)
    {
        String hashValue = ServerHashCode.SHA1FromBytes(payload);
        client.addSentHashCode(hashValue);
    }

    private void read(SelectionKey key) throws IOException
    {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        // TODO: This may break, 20 was based on lenth of 160 bit (20 byte) hash
        ByteBuffer readbuff = ByteBuffer.allocate(20);

        int numRead = 0;
        try
        {
            numRead = socketChannel.read(readbuff);
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

        String hashedValue = readbuff.toString();
        if (sentHashCodes.remove(hashedValue))
        {
            client.incrementTotalReceivedCount();
            System.out.println("Received Work Confirmation");
        }
    }

    private SocketChannel initiateConnection() throws IOException
    {
        // Create a non-blocking socket channel
        SocketChannel socketChannel = SocketChannel.open();
        //socketChannel.configureBlocking(false);

        // Kick off connection establishment...without configureBlocking, it should wait.
        socketChannel.connect(new InetSocketAddress(this.serverAddress, this.serverPort));
        return socketChannel;

    }

    private void completeConnection(SelectionKey key)
    {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        try
        {
            socketChannel.finishConnect();
        }
        catch (IOException e)
        {
            System.out.println(e);
            key.cancel();
            return;
        }

        // We're ready to write
        key.interestOps(SelectionKey.OP_WRITE);
    }

    public byte[] getPayload()
    {
        Random random = new Random();
        byte[] payload = new byte[PAYLOAD_SIZE];
        random.nextBytes(payload);
        return payload;
    }
}
