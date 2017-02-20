package cs455.scaling.server;


import cs455.scaling.messaging.*;
import cs455.scaling.node.Node;
import cs455.scaling.threadpool.ThreadPool;
import cs455.scaling.threadpool.WorkerQueue;
import cs455.scaling.transport.TCPSenderThread;
import cs455.scaling.util.NotImplementedException;

public class Server implements Node
{
    private final int port;
    private final int threadPoolSize;
    private final ThreadPool threadPool;
    private final WorkerQueue workQueue;

    public Server(int port, int threadPoolSize)
    {
        this.port = port;
        this.threadPoolSize = threadPoolSize;
        workQueue = new WorkerQueue();
        this.threadPool = new ThreadPool(threadPoolSize, workQueue);

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

    }

    private void ReceiveWorkMessage(WorkMessage message)
    {
        throw new NotImplementedException();
    }

}
