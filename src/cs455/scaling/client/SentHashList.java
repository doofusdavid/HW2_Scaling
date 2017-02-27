package cs455.scaling.client;

import java.util.ArrayList;

/**
 * Maintains the list of Hashes sent to the Server.
 */
public class SentHashList
{
    private final ArrayList<String> sentHashValues = new ArrayList<>();

    public SentHashList()
    {
    }

    /**
     * Add a Hash to the ArrayList
     *
     * @param value The Hash to add
     */
    synchronized public void add(String value)
    {
        this.sentHashValues.add(value);
    }

    /**
     * Remove an item from the ArrayList
     * @param value The Hash to remove
     * @return True if successful, False if no such item was found
     */
    synchronized public boolean remove(String value)
    {
        return sentHashValues.remove(value);
    }
}
