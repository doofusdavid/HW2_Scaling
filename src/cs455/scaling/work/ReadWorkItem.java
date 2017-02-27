package cs455.scaling.work;


import java.nio.channels.SelectionKey;

/**
 * ReadWorkItem represents a connected Client, represented by Key, ready to be read from.
 */
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
