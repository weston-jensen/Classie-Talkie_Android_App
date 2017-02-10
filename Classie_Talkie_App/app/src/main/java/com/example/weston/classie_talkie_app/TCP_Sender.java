package com.example.weston.classie_talkie_app;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Queue;

/**
 * Created by Weston on 2/7/2017.
 */

public class TCP_Sender {
    private Socket _socket;
    private Queue<Message> _sendQueue;
    private ObjectOutputStream _outToServer;
    private boolean _running = true;

    public TCP_Sender(Socket socket, Queue<Message> sendQueue)
    {
        this._socket = socket;
        this._sendQueue = sendQueue;

        try{
            this._outToServer = new ObjectOutputStream(this._socket.getOutputStream());
            System.out.print("_OutToServer is initiaited");
        }catch(IOException e){
            System.out.print("_OutToServer failed to initiaitize");
            e.printStackTrace();
        }
    }

    public void check()
    {
        //while(_running)
        {
            if(!this._sendQueue.isEmpty())
            {
                try {
                    Message m = new Message();
                    m = this._sendQueue.remove();

                    if(m.mesgStatus<0)
                    {
                       // reliableSend(m);
                        this._outToServer.writeObject(m);
                        this._outToServer.flush();
                    }
                    else
                    {
                        this._outToServer.writeObject(m);
                        this._outToServer.flush();
                    }



                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

       /* try {
            if(!this._socket.isClosed())
            {
                this._socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }
/*
    private void reliableSend(Message m)
    {
        try {
            this.messageFlag.setFlag(true);
            this.messageFlag.setMesgID(m.getMesgID());
            this._outToServer.writeObject(m);
            this._outToServer.flush();

            Thread.sleep(500);

            int iter = 0;
            while((this.messageFlag.isFlagSet()==true)&&(iter<3))
            {
                try {
                    this._outToServer.writeObject(m);
                    this._outToServer.flush();
                    iter++;
                    Thread.yield();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if(iter>=3)
            {
                this.messageFlag.setFlag(false);//reset flag
            }

        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }
    */

}
