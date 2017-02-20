package cs455.scaling.messaging;

import java.io.IOException;

/**
 * Message sent from Client to Server to ensure that the Server is up and ready.
 */
public class ServerConnectRequest extends Message
{
    private String clientIPAddress;
    private int clientPort;

    public ServerConnectRequest(String clientIPAddress, int clientPort)
    {
        super(MessageType.ServerConnectRequest);
        this.clientIPAddress = clientIPAddress;
        this.clientPort = clientPort;
    }
    public ServerConnectRequest(byte[] marshalledBytes) throws IOException
    {
        super(MessageType.ServerConnectRequest);
        super.openInput(marshalledBytes);

        int ipLength = din.readInt();
        byte[] ipBytes = new byte[ipLength];
        din.readFully(ipBytes);
        this.clientIPAddress = new String(ipBytes);

        this.clientPort = din.readInt();

        super.closeInput();
    }

    public String getClientIPAddress()
    {
        return clientIPAddress;
    }

    public int getClientPort()
    {
        return clientPort;
    }

    @Override
    public byte[] getBytes() throws IOException
    {
        super.openOutput(getType());

        // IP address
        byte[] ipBytes = this.clientIPAddress.getBytes();
        int ipLength = ipBytes.length;
        dout.writeInt(ipLength);
        dout.write(ipBytes);

        // Port
        dout.writeInt(this.clientPort);

        return super.closeOutput();
    }
}
