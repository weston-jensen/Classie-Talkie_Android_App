package com.example.weston.classie_talkie_app;

import android.util.Log;

import java.util.Queue;

/**
 * Created by Weston on 2/7/2017.
 */

public class Client_Convo extends Convo{
    private static String TAG = "AudioClient";
    private int clientID = -1;
    private boolean _running = true;
    private Client_Thread ct;

    public Client_Convo(Queue<String> sendQueue, Queue<Message> receiveQueue, Client_Thread ct)
    {
        super(sendQueue, receiveQueue);
        this.ct = ct;

    }

    public void check()
    {
        //while(this._running)
        {
            // read receive queue
            if(!this.getReceiveQueue().isEmpty())
            {
                Messages_Received(this.getReceiveQueue().remove());
            }

            //this._running = false;
        }
    }

    private void Messages_Received(Message m)
    {
        int status = -1;
        int ID = -1;

        System.out.println("*************************************************************");
        Log.i(TAG,"Message Receuved: "+m.getMesgID());


        switch((m.getMesgID()))
        {
            case 2:
                ID = (m.getClientID());
                /*Getting kicked off the LAN*/
                if((m.getMesgStatus())<0)//Received -1 Command that LAN is destroyed
                {
                    this.ct.setClientID(-1);//reset clientID
                    this.clientID = -1;
                    //this.ct.getCG().changeFrameTo_UserInfo();//change to start GUI
                    status = 1;
                    Log.i(TAG,"Getting Kicked off the LAN");
                }
                else //There was an error
                {
                    //Set Error Message in GUI window
                    status = -1;
                    Log.i(TAG,"There was an error get kicked off LAN ");
                }
                this.getSendQueue().add(this.getEncode().EndLAN(-99,status));
                break;
            case 3:
                /*AuthenticateClient reply from server*/
                if((m.getMesgStatus())>0)//If there is no error
                {
                    if(this.clientID<0)
                    {
                        //set our client ID
                        this.clientID = (m.getClientID());
                        this.ct.setClientID((m.getClientID()));
                        this.ct.setAuthenticated(true);
                        Log.i(TAG,"AuthenticateClient Reply From Server, clientID = "+this.clientID);
                    }
                }
                else //There was an error
                {
                    //Set Error Message in GUI window
                   // this.ct.getCG().setStatus(m.getMessage());
                    Log.i(TAG,"Error getting our clientID");
                }
                break;
            case 4:
                //Received Priority Token Request Response
                if((m.getMesgStatus())>0)//if we have received the token
                {
                    this.ct.set_priorityToken(true);
                    Log.i(TAG,"We Received Priority Token");
                }
                else//We did not receive priority token
                {
                    this.ct.set_priorityToken(false);//set flag to false
                    Log.i(TAG,"We Did Not Receive Priority Token");
                }
                break;
            case 5:
                /*Release Priority Token Response*/
                //-1 command, 1 ack
                this.ct.set_priorityToken(false);

                //Server is commanding us to release token (-1)
                if((m.getMesgStatus())==-1)
                {
                    //Acknowledge that we have the released the token
                    this.getSendQueue().add(this.getEncode().ReleasePriorityToken(this.ct.getClientID(),2));
                    Log.i(TAG,"Server is Making us Release the Priority Token");
                }
                break;
            case 7:
                /*Client Disconnect Reply Received*/
                android.os.Process.killProcess(android.os.Process.myPid());
                Log.i(TAG,"ClientDisconnect Reply Received");
                break;
            case 10:
                /*GracefulShutdown Request Received*/
                this.getSendQueue().add(this.getEncode().GracefulShutdown(-99, 1));//Tell server we are shutting down
                android.os.Process.killProcess(android.os.Process.myPid());
                Log.i(TAG,"GracefulShutdown Request Received");
                break;
        }
    }
}
