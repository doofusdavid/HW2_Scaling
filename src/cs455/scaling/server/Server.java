package cs455.scaling.server;


import cs455.scaling.threadpool.ThreadPool;
import cs455.scaling.threadpool.WorkQueue;
import cs455.scaling.work.ReadWorkItem;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Timer;

/**
 * Main Server class.  Takes in parameters and listens for incoming connections from Clients.  Launches a
 * ThreadPool to maintain threads, and a Statistics thread to monitor connections and throughput to the server.
 */
public class Server
{
    private final ThreadPool threadPool;
    private final WorkQueue workQueue;
    private String host;
    private Selector selector;


    /**
     * Constructor creates a ServerSocketChannel
     *
     * @param port           The Port the server should listen on
     * @param threadPoolSize Number of Threads that should process this Server's tasks.
     */
    public Server(int port, int threadPoolSize)
    {
        workQueue = new WorkQueue();

        try
        {
            this.selector = SelectorProvider.provider().openSelector();

            // Create a new non-blocking server socket channel
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);

            // Bind the server socket to the specified address and port
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        this.threadPool = new ThreadPool(threadPoolSize, workQueue);

        Timer timer = new Timer();
        ServerStatistics serverStats = new ServerStatistics(this.threadPool);
        timer.schedule(serverStats, 0, 5000);

        this.ReceiveServerConnections();
    }

    /**
     * Takes in parameters and creates the Server Object
     * @param args
     */
    public static void main(String[] args)
    {
        if (args.length != 2)
        {
            System.out.println("Invalid arguments.\nExpected portnum thread-pool-size");
        }

        try
        {
            int port = Integer.parseInt(args[0]);
            int threads = Integer.parseInt(args[1]);
            Server server = new Server(port, threads);
        } catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Invalid Arguments.");
            System.exit(0);
        }
    }

    /**
     * Loop which monitors the selector for new Client connections.
     */
    private void ReceiveServerConnections()
    {
        while (true)
        {
            try
            {
                this.selector.select();

                Iterator keys = this.selector.selectedKeys().iterator();

                while (keys.hasNext())
                {
                    SelectionKey key = (SelectionKey) keys.next();
                    keys.remove();

                    if (key.isAcceptable())
                    {
                        processIncomingConnection(key, selector);
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param key SelectionKey representing the incoming Client
     * @param selector The Selector used to register the client
     * @throws IOException
     * @throws InterruptedException
     */
    private void processIncomingConnection(SelectionKey key, Selector selector) throws IOException, InterruptedException
    {
        ServerSocketChannel serverSocket = (ServerSocketChannel) key.channel();
        SocketChannel clientSocket = serverSocket.accept();
        clientSocket.configureBlocking(false);

        System.out.println("Incoming connection from " + clientSocket.getRemoteAddress());


        SelectionKey clientKey = clientSocket.register(selector, SelectionKey.OP_READ);
        this.workQueue.enqueue(new ReadWorkItem(clientKey));
        this.threadPool.incrementTotalConnectedClients();
        System.out.println("Accepting Incoming Connection");
    }
}
