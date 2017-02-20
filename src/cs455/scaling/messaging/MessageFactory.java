package cs455.scaling.messaging;

import cs455.scaling.node.Node;

import java.io.IOException;
import java.nio.ByteBuffer;


public class MessageFactory
{
    private static final MessageFactory ourInstance = new MessageFactory();

    private MessageFactory()
    {
    }

    public static MessageFactory getInstance()
    {
        return ourInstance;
    }

    public void FireEvent(byte[] data, Node node)
    {
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);

        // The length has been trimmed off, so we're just going to start with messageType
        MessageType messageType = MessageType.values()[byteBuffer.getInt()];

        try
        {
            Message message;
            switch (messageType)
            {
                case WorkMessage:
                {
                    message = new WorkMessage(data);
                    break;
                }
                case WorkMessageResponse:
                {
                    message = new WorkMessageResponse(data);
                    break;
                }
                case ServerConnectRequest:
                {
                    message = new ServerConnectRequest(data);
                    break;
                }
                case ServerConnectResponse:
                {
                    message = new ServerConnectResponse(data);
                    break;
                }
                case ServerDisconnectRequest:
                {
                    message = new ServerDisconnectRequest(data);
                    break;
                }
                default:
                    System.out.println("Message Factory: Unknown messageType.  Exiting.");
                    return;
            }
            node.onEvent(message);

        } catch (IOException ioe)
        {
            System.out.println("EventFactory.FireEvent " + ioe.getMessage());
        }
    }

}

