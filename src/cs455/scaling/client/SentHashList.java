package cs455.scaling.client;

import java.util.LinkedList;

/**
 * Maintains the list of Hashes sent to the Server.
 */
public class SentHashList
{
    private final LinkedList<String> sentHashValues = new LinkedList<>();

    public SentHashList()
    {
    }

    /**
     * Add a Hash to the LinkedList
     *
     * @param value The Hash to add
     */
    synchronized public void add(String value)
    {
        this.sentHashValues.add(value);
    }

    /**
     * Remove an item from the LinkedList
     * @param value The Hash to remove
     * @return True if successful, False if no such item was found
     */
    synchronized public boolean remove(String value)
    {
        return sentHashValues.remove(value);
    }
}
