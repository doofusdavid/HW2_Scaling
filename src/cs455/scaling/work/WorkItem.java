package cs455.scaling.work;


import java.nio.channels.SelectionKey;

public interface WorkItem
{
    WorkType getType();

    SelectionKey getKey();
}
