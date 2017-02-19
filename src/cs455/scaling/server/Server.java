package cs455.scaling.server;


import cs455.scaling.messaging.Message;

public class Server
{
    private final int port;
    private final int threadPoolSize;

    public Server(int port, int threadPoolSize)
    {
        this.port = port;
        this.threadPoolSize = threadPoolSize;
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

    }
}
