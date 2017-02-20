package cs455.scaling.node;

import cs455.scaling.messaging.Message;

/**
 * Interface containing the basics used by Nodes, currently Registry and MessagingNode
 */
public interface Node
{

    void onEvent(Message message);
}
