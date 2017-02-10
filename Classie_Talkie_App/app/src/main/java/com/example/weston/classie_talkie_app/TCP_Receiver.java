package com.example.weston.classie_talkie_app;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Queue;

/**
 * Created by Weston on 2/7/2017.
 */

public class TCP_Receiver {
    private Socket _socket;
    private Queue<Message> _receiveQueue;
    private ObjectInputStream _inFromServer;
    private boolean _running = true;

    public TCP_Receiver(Socket socket, Queue<Message> receiveQueue)
    {
        this._socket = socket;
        this._receiveQueue = receiveQueue;
        try {
            this._inFromServer = new ObjectInputStream(this._socket.getInputStream());
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void check()
    {

       // while(this._running)
        {
            try {
                if(!this._socket.isClosed())//If Socket Is still open
                {
                    Message m = null;
                    if(this._inFromServer!=null)
                    {
                        m = (Message)this._inFromServer.readObject();//Read what is coming from the Server
                        if(m.mesgID >= 0)
                        {
                          /*  if((this.messageFlag.getMesgID() == m.mesgID)&&(this.messageFlag.isFlagSet()==true))
                            {
                                this.messageFlag.setFlag(false);//received message pack
                                LOG.info("->Received Ack from Server");
                            }
                            */

                            //this._receiveQueue.add(m);
                        }
                        this._receiveQueue.add(m);//Add Message to Receive Queue
                    }

                }
                Thread.sleep(1);
            } catch (ClassNotFoundException | IOException | InterruptedException e) {
                this._running = false;
                e.printStackTrace();
            }
        }
        try {
            if(!this._socket.isClosed())
            {
                this._socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
