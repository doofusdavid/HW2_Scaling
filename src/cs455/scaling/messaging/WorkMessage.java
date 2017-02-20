package cs455.scaling.messaging;


import java.io.IOException;
import java.util.Random;

public class WorkMessage extends Message
{
    private final int PAYLOAD_SIZE = 8092;
    private final String clientIPAddress;
    private final int clientPort;
    byte[] payload;

    /**
     * WorkMessage  creates a random 8k byte array
     */
    public WorkMessage(String clientIPAddress, int clientPort)
    {
        super(MessageType.WorkMessage);

        Random random = new Random();
        this.payload = new byte[PAYLOAD_SIZE];
        random.nextBytes(this.payload);

        this.clientIPAddress = clientIPAddress;
        this.clientPort = clientPort;
    }


    public WorkMessage(byte[] marshalledBytes) throws IOException
    {
        super(MessageType.WorkMessage);
        super.openInput(marshalledBytes);

        int ipLength = din.readInt();
        byte[] ipBytes = new byte[ipLength];
        din.readFully(ipBytes);

        this.clientIPAddress = new String(ipBytes);

        this.clientPort = din.readInt();

        int payloadLength = din.readInt();
        this.payload = new byte[payloadLength];
        din.readFully(this.payload);

        super.closeInput();
    }

    public byte[] getPayload()
    {
        return payload;
    }

    @Override
    public byte[] getBytes() throws IOException
    {
        super.openOutput(getType());

        // IP address - string
        byte[] ipBytes = clientIPAddress.getBytes();
        int ipLength = ipBytes.length;
        dout.writeInt(ipLength);
        dout.write(ipBytes);

        // Port - int
        dout.writeInt(this.clientPort);

        // Payload - byte[]
        dout.writeInt(payload.length);
        dout.write(payload);

        return super.closeOutput();
    }
}
