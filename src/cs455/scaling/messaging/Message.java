package cs455.scaling.messaging;

import java.io.*;


public class Message
{
    private final MessageType type;

    byte[] marshalledBytes;
    ByteArrayOutputStream baOutputStream;
    ByteArrayInputStream baInputStream;
    DataOutputStream dout;
    DataInputStream din;

    Message(MessageType type)
    {
        this.type = type;
    }

    MessageType getType()
    {
        return this.type;
    }

    public byte[] getBytes() throws IOException
    {
        return new byte[0];
    }

    void openOutput(MessageType type) throws IOException
    {
        baOutputStream = new ByteArrayOutputStream();
        dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(this.type.ordinal());
    }

    byte[] closeOutput() throws IOException
    {
        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();
        baOutputStream.close();
        dout.close();

        return marshalledBytes;
    }

    void openInput(byte[] marshalledBytes) throws IOException
    {
        baInputStream = new ByteArrayInputStream(marshalledBytes);
        din = new DataInputStream(new BufferedInputStream(baInputStream));

        int readType = din.readInt();
        if (type.ordinal() != readType)
        {
            throw new IllegalArgumentException("Bytes didn't correspond to a " + type.toString() + ".");
        }
    }

    void closeInput() throws IOException
    {
        baInputStream.close();
        din.close();
    }
}
