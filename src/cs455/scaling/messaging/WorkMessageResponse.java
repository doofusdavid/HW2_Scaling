package cs455.scaling.messaging;


import java.io.IOException;

public class WorkMessageResponse extends Message
{
    private final String hashCode;

    public WorkMessageResponse(MessageType type, String hashCode)
    {
        super(MessageType.WorkMessageResponse);
        this.hashCode = hashCode;
    }

    public WorkMessageResponse(byte[] marshalledBytes) throws IOException
    {
        super(MessageType.WorkMessageResponse);
        super.openInput(marshalledBytes);

        int hcLength = din.readInt();
        byte[] hcBytes = new byte[hcLength];
        din.readFully(hcBytes);

        this.hashCode = new String(hcBytes);

        super.closeInput();
    }

    @Override
    byte[] getBytes() throws IOException
    {
        super.openOutput(getType());

        // HashCode
        byte[] hcBytes = hashCode.getBytes();
        int hcLength = hcBytes.length;
        dout.writeInt(hcLength);
        dout.write(hcBytes);

        return super.closeOutput();
    }

}
