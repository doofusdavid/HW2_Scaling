package cs455.scaling.server;


import cs455.scaling.messaging.*;
import cs455.scaling.node.Node;
import cs455.scaling.threadpool.ThreadPool;
import cs455.scaling.threadpool.WorkerQueue;
import cs455.scaling.transport.TCPReceiverThread;
import cs455.scaling.transport.TCPSenderThread;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Timer;

public class Server implements Node
{
    private final int port;
    private final int threadPoolSize;
    private final ThreadPool threadPool;
    private final WorkerQueue workQueue;
    private String host;
    private TCPReceiverThread receiverThread;
    private int totalConnections;
    private int totalConnectedClients;

    public Server(int port, int threadPoolSize)
    {
        this.port = port;
        this.threadPoolSize = threadPoolSize;
        workQueue = new WorkerQueue();
        this.threadPool = new ThreadPool(threadPoolSize, workQueue);

        try
        {
            this.host = InetAddress.getLocalHost().getHostName().toString();
            receiverThread = new TCPReceiverThread(this.port, this);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        Thread t = new Thread(receiverThread);
        t.start();
        Timer timer = new Timer();
        ServerStatistics serverStats = new ServerStatistics(this);
        timer.schedule(serverStats, 0, 5000);

    }

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

    public void onEvent(Message message)
    {
        if (message instanceof WorkMessage)
        {
            this.ReceiveWorkMessage((WorkMessage) message);
        }
        if (message instanceof ServerConnectRequest)
        {
            this.ReceiveServerConnectRequest((ServerConnectRequest) message);
        }
    }

    private void ReceiveServerConnectRequest(ServerConnectRequest message)
    {
        ServerConnectResponse response = new ServerConnectResponse(StatusCode.SUCCESS);

        TCPSenderThread thread = new TCPSenderThread(message.getClientIPAddress(), message.getClientPort(), response);
        thread.run();
        this.incrementTotalConnectedClients();
        System.out.println(String.format("Connected to client %s:%d", message.getClientIPAddress(), message.getClientPort()));
    }

    private void ReceiveWorkMessage(WorkMessage message)
    {
        try
        {
            this.workQueue.enqueue(message);
            this.incrementTotalConnections();
        }
        catch (InterruptedException i)
        {
            i.printStackTrace();
        }
    }

    synchronized public int getTotalConnections()
    {
        return totalConnections;
    }

    synchronized public void incrementTotalConnections()
    {
        this.totalConnections++;
    }

    synchronized public void incrementTotalConnectedClients()
    {
        this.totalConnectedClients++;
    }

    synchronized public int getTotalConnectedClients()
    {
        return this.totalConnectedClients;
    }
}
