package cs455.scaling.work;


import java.nio.channels.SelectionKey;

/**
 * Interface for the WorkItems which are added to, and consumed from, the WorkQueue
 */
public interface WorkItem
{
    WorkType getType();

    SelectionKey getKey();
}
