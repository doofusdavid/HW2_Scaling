package cs455.scaling.client;

import java.util.ArrayList;

public class SentHashList
{
    private final ArrayList<String> sentHashValues = new ArrayList<>();

    public SentHashList()
    {
    }

    synchronized public void add(String value)
    {
        this.sentHashValues.add(value);
    }

    synchronized public boolean remove(String value)
    {
        return sentHashValues.remove(value);
    }
}
