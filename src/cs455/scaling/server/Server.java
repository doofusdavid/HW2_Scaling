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

    }

    public void onEvent(Message message)
    {

    }
}
