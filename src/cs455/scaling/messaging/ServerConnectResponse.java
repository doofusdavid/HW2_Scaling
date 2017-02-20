package cs455.scaling.messaging;

import java.io.IOException;

/**
 * Response from Server acknowledging connection
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

    public byte getStatusCode()
    {
        return statusCode;
    }

    @Override
    public byte[] getBytes() throws IOException
    {
        super.openOutput(getType());

        dout.write(statusCode);

        return super.closeOutput();
    }
}
