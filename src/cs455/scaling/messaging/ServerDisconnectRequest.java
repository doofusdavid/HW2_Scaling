package cs455.scaling.messaging;


import java.io.IOException;

public class ServerDisconnectRequest extends Message
{
    private String clientIPAddress;
    private int clientPort;

    public ServerDisconnectRequest(String clientIPAddress, int clientPort)
    {
        super(MessageType.ServerDisconnectRequest);
        this.clientIPAddress = clientIPAddress;
        this.clientPort = clientPort;
    }

    public ServerDisconnectRequest(byte[] marshalledBytes) throws IOException
    {
        super(MessageType.ServerDisconnectRequest);
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
