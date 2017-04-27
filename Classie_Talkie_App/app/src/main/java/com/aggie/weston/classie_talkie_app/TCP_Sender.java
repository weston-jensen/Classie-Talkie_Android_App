package com.aggie.weston.classie_talkie_app;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Queue;

/**
 * Created by Weston on 2/7/2017.
 */

public class TCP_Sender {
    private static String TAG = "AudioClient";
    private Socket _socket;
    private Queue<String> _sendQueue;
    private ObjectOutputStream _outToServer;
    private boolean _running = true;

    public TCP_Sender(Socket socket, Queue<String> sendQueue)
    {
        this._socket = socket;
        this._sendQueue = sendQueue;

        try{
            this._outToServer = new ObjectOutputStream(this._socket.getOutputStream());
        }catch(IOException e){
            System.out.print("_OutToServer failed to initiaitize");
            e.printStackTrace();
        }
    }

    public void check() {
        Thread senderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (_running) {
                    if (!_sendQueue.isEmpty()) {
                        try {
                            //Message m = new Message();
                            //m = _sendQueue.remove();

                            String m = _sendQueue.remove();
                            _outToServer.writeObject(m);
                            _outToServer.flush();

                            /*Log.i(TAG, "Checkinging for messages to send " + m.getMesgID());

                            Log.i(TAG, "FirstName = " + m.getFname());
                            Log.i(TAG, "LastName = " + m.getLname());
                            Log.i(TAG, "ANumber = " + m.getaNum());
                            Log.i(TAG, "Password = " + m.getLANPass());
                            _outToServer.flush();


                            if (m.mesgStatus < 0) {
                                // reliableSend(m);
                                _outToServer.writeObject(m);
                                _outToServer.flush();
                            } else {
                                _outToServer.writeObject(m);
                                _outToServer.flush();
                            }
                            */


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
        senderThread.start();
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
