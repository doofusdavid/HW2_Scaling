package cs455.scaling.messaging;

import java.io.IOException;

/**
 * Created by david on 2/19/17.
 */
public class ServerConnectResponse extends Message
{
    private final byte statusCode;

    public ServerConnectResponse(byte statusCode)
    {
        super(MessageType.ServerConnectResponse);
        this.statusCode = statusCode;
    }

    public ServerConnectResponse(byte[] marshalledBytes) throws IOException
    {
        super(MessageType.ServerConnectResponse);
        super.openInput(marshalledBytes);

        this.statusCode = din.readByte();

        super.closeInput();
    }

    @Override
    byte[] getBytes() throws IOException
    {
        super.openOutput(getType());


        return super.closeOutput();
    }
}
