package com.example.weston.classie_talkie_app;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Queue;

/**
 * Created by Weston on 2/7/2017.
 */

public class TCP_Receiver {
    private static String TAG = "AudioClient";
    private Socket _socket;
    private Queue<Message> _receiveQueue;
    private ObjectInputStream _inFromServer;
    private boolean _running = true;
    private JSON_Decoder decode;

    public TCP_Receiver(Socket socket, Queue<Message> receiveQueue)
    {
        this._socket = socket;
        this._receiveQueue = receiveQueue;
        this.decode = new JSON_Decoder();

        try {
            this._inFromServer = new ObjectInputStream(this._socket.getInputStream());
        }catch(IOException e){
            System.out.println("Error init infromServer Stream");
            e.printStackTrace();
        }
    }

    public void check() {
        Thread receiverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (_running) {
                    try {
                        if (!_socket.isClosed())//If Socket Is still open
                        {
                            Message m = null;
                            if (_inFromServer != null) {
                                Log.i(TAG, "Checking for messages received");
                                String mesg = (String)_inFromServer.readObject();
                                m = decode.decodeMessage(mesg);


                           /* if(m.mesgID >= 0)
                            {
                              / if((this.messageFlag.getMesgID() == m.mesgID)&&(this.messageFlag.isFlagSet()==true))
                                {
                                    this.messageFlag.setFlag(false);//received message pack
                                    LOG.info("->Received Ack from Server");
                                }


                                //this._receiveQueue.add(m);
                            }
                            */
                                _receiveQueue.add(m);//Add Message to Receive Queue
                            }

                        }

                    } catch (ClassNotFoundException | IOException e) {
                        _running = false;
                        e.printStackTrace();
                    }
                }
                try {
                    if (!_socket.isClosed()) {
                        _socket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        receiverThread.start();
    }
}
