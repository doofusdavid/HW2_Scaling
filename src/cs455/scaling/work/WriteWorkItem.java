package cs455.scaling.work;


import java.nio.channels.SelectionKey;

public class WriteWorkItem implements WorkItem
{
    private SelectionKey key;
    private String hashValue;

    public WriteWorkItem(SelectionKey key, String hashValue)
    {
        this.key = key;
        this.hashValue = hashValue;
    }

    @Override
    public SelectionKey getKey()
    {
        return this.key;
    }

    @Override
    public WorkType getType()
    {
        return WorkType.Write;
    }

    public String getHashValue()
    {
        return hashValue;
    }
}
