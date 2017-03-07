package com.example.weston.classie_talkie_app;

import android.support.annotation.MainThread;
import android.util.Log;

import java.util.Queue;

/**
 * Created by Weston on 3/6/2017.
 */

public class NM_Convo extends Convo {
    private static String TAG = "AudioClient";
    private NM_Thread nmt;

    public NM_Convo(Queue<String> sendQueue, Queue<Message> receiveQueue, NM_Thread nmt) {
        super(sendQueue, receiveQueue);
        this.nmt = nmt;
    }

    public void check()
    {
       // while(this.isRunning())
        {
            Message in = new Message();
            if(!this.getReceiveQueue().isEmpty())
            {
                in = this.getReceiveQueue().remove();
                Messages_Received(in);
            }
            Thread.yield();
        }
       // this.nmt.killThreads();
    }

    private void Messages_Received(Message m)
    {
        Log.i(TAG,"->NM Received Message with Mesg ID: "+m.getMesgID());

        switch((m.getMesgID()))
        {
            case 0:
                //AuthenticateManager reply from server
                if((m.getMesgStatus())>0)//If there is no error
                {
                    //set our Manager ID
                    this.nmt.setManagerID(m.getManagerID());
                    Log.i(TAG,"->NM is now connected with ManagerID: "+this.nmt.getManagerID());
                    this.nmt.set_authServerPw(true);
                }
                else //There was an error
                {
                    //Set Error Message in GUI window
                    Log.i(TAG,"->NM Failed to Authenticate to Server because:"+m.getMessage());
                }
                break;
            case 1:
                //Create LAN
                if((m.getMesgStatus())>0)//Server received LAN password correctly
                {
                    Log.i(TAG,"LAN was created");
                    this.nmt.set_setLanPw(true);
                }
                else//Server did not receive LAN password correctly
                {

                }
                break;
            case 2:
                //End LAN
                if((m.getMesgStatus())>0)
                {

                }
                else
                {

                }

                break;
            case 6:
                //Requested Analytic Data Response
                if((m.getMesgStatus())>0)//Got data
                {

                }
                else//Error getting data
                {

                }

                break;
            case 8:
                //muteComm Ack Received
                this.nmt.set_muteConvo(true);
                break;
            case 9:
                //unMuteComm Ack Received
                this.nmt.set_muteConvo(false);
                break;
            case 10:
			/*
			 * Graceful shutdown command
			 * Server ends comm with clients and sends back
			 * an ack when complete
			 */
                if((m.getMesgStatus())>0)//Got data
                {
                    //KILL Order 66
                    Log.i(TAG,"->NM ending all Threads, Shutting Down");
                    this.getSendQueue().add(this.getEncode().GracefulShutdown(-99, 1));//Tell server we are shutting down
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
                else//Error getting data
                {
                    //this.nmt.getNm_gui().setStatus(m.getMessage());
                }
                break;

        }
    }
}
