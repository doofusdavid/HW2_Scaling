package cs455.scaling.work;


import java.nio.channels.SelectionKey;

public class ReadWorkItem implements WorkItem
{
    private final SelectionKey key;

    public ReadWorkItem(SelectionKey key)
    {
        this.key = key;
    }

    @Override
    public SelectionKey getKey()
    {
        return this.key;
    }

    @Override
    public WorkType getType()
    {
        return WorkType.Read;
    }
}
