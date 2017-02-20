package cs455.scaling.transport;

import cs455.scaling.messaging.Message;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPSenderThread implements Runnable
{
    private String IPAddress;
    private int Port;
    private byte[] dataToSend;

    public TCPSenderThread(String IPAddress, int Port, Message message)
    {
        this.IPAddress = IPAddress;
        this.Port = Port;
        try
        {
            this.dataToSend = message.getBytes();
        } catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        try
        {
            Socket socket = new Socket(IPAddress, Port);
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            int dataLength = dataToSend.length;
            dout.writeInt(dataLength);

            dout.write(dataToSend, 0, dataLength);
            dout.flush();
            dout.close();
            socket.close();
        } catch (Exception e)
        {
            System.out.println("TCPSenderThread: " + e.getMessage());
        }

    }
}
