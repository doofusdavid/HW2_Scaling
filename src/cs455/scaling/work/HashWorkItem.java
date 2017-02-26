package cs455.scaling.work;


import java.nio.channels.SelectionKey;

public class HashWorkItem implements WorkItem
{
    byte[] payload;
    private SelectionKey key;

    public HashWorkItem(SelectionKey key, byte[] payload)
    {
        this.key = key;
        this.payload = payload;
    }

    public byte[] getPayload()
    {
        return payload;
    }

    @Override
    public SelectionKey getKey()
    {
        return this.key;
    }

    @Override
    public WorkType getType()
    {
        return WorkType.Hash;
    }
}
