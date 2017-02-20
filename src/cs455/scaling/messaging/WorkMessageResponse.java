package cs455.scaling.messaging;


import java.io.IOException;

public class WorkMessageResponse extends Message
{
    private final String hashValue;

    public WorkMessageResponse(MessageType type, String hashValue)
    {
        super(MessageType.WorkMessageResponse);
        this.hashValue = hashValue;
    }

    public WorkMessageResponse(byte[] marshalledBytes) throws IOException
    {
        super(MessageType.WorkMessageResponse);
        super.openInput(marshalledBytes);

        int hcLength = din.readInt();
        byte[] hcBytes = new byte[hcLength];
        din.readFully(hcBytes);

        this.hashValue = new String(hcBytes);

        super.closeInput();
    }

    public String getHashValue()
    {
        return hashValue;
    }

    @Override
    byte[] getBytes() throws IOException
    {
        super.openOutput(getType());

        // HashCode
        byte[] hcBytes = hashValue.getBytes();
        int hcLength = hcBytes.length;
        dout.writeInt(hcLength);
        dout.write(hcBytes);

        return super.closeOutput();
    }

}
