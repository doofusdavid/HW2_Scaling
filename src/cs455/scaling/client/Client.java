package cs455.scaling.client;


import java.net.InetAddress;
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
        if (args.length != 3)
        {
            System.out.println("Invalid arguments.\nExpected server-host server-port message-rate");
        }

        try
        {
            String host = InetAddress.getByName(args[0]).toString();
            int port = Integer.parseInt(args[1]);
            int rate = Integer.parseInt(args[2]);
            Client client = new Client(host, port, rate);
        } catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Invalid Arguments.");
            System.exit(0);
        }
    }
}
