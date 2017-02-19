package cs455.scaling.client;


import java.util.ArrayList;

public class Client
{
    private final String serverHost;
    private final int serverPort;
    private final int messageRate;
    private final ArrayList<String> sentHashCodes;

    public Client(String serverHost, int serverPort, int messageRate)
    {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.messageRate = messageRate;
        sentHashCodes = new ArrayList<>();
    }

    public static void main(String[] args)
    {

    }
}
