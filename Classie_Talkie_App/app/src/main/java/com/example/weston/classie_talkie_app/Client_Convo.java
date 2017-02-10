package com.example.weston.classie_talkie_app;

import java.util.Queue;

/**
 * Created by Weston on 2/7/2017.
 */

public class Client_Convo extends Convo{
    private int clientID = -1;
    private boolean _running = true;

    public Client_Convo(Queue<Message> sendQueue, Queue<Message> receiveQueue)
    {
        super(sendQueue, receiveQueue);

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

        System.out.println("Message Received: "+m.getMesgID());

        switch((m.getMesgID()))
        {
            case 2:
                ID = (m.getClientID());
                /*Getting kicked off the LAN*/
                if((m.getMesgStatus())<0)//Received -1 Command that LAN is destroyed
                {
                    //set our client ID
                    //this.ct.setClient_ID(-1);//reset clientID
                    this.clientID = -1;
                    //this.ct.getCG().setStatus("Reconnect to LAN");
                    //this.ct.getCG().changeFrameTo_UserInfo();//change to start GUI
                    status = 1;

                }
                else //There was an error
                {
                    //Set Error Message in GUI window
                    status = -1;
                }
                this.getSendQueue().add(this.getEncode().EndLAN(null,status));
                break;
            case 3:
                /*AuthenticateClient reply from server*/
                if((m.getMesgStatus())>0)//If there is no error
                {
                    if(this.clientID<0)
                    {
                        //set our client ID
                        this.clientID = (m.getClientID());
                        //this.ct.setClient_ID((m.getClientID()));
                       // this.ct.getCG().changeFrameTo_PTT();

                    }
                }
                else //There was an error
                {
                    //Set Error Message in GUI window
                   // this.ct.getCG().setStatus(m.getMessage());

                }
                break;
            case 4:
                //Received Priority Token Request Response
                if((m.getMesgStatus())>0)//if we have received the token
                {
                    //this.ct.setPriorityToken(true);//set flag to true

                    //this.ct.getCG().setPttState("Push To Finish");
                    //this.ct.getCG().setPttToggle(0);
                    //this.ct.getCG().setPptMessage("Transmitting is Online.");//update GUI message
                    //this.ct.getCG().changeFrameTo_PTT();//update GUI

                    //Set Up UDP Socket
                    //connectToUDP();
                }
                else//We did not receive priority token
                {
                    //this.ct.setPriorityToken(false);//set flag to false
                    //this.ct.getCG().setPptMessage(m.getMessage());//update GUI message
                }
                break;
            case 5:
                /*Release Priority Token Response*/
                //-1 command, 1 ack

                //this.ct.setPriorityToken(false);//set flag to false
                //this.ct.getCG().setPttState("Push To Talk");
                //this.ct.getCG().setPttToggle(1);
                //this.ct.getCG().setPptMessage("Transmitting is Offline.");//Change GUI message
                //this.ct.getCG().changeFrameTo_PTT();//update GUI

                //Server is commanding us to release token (-1)
                if((m.getMesgStatus())==-1)
                {
                    //Acknowledge that we have the released the token
                    //disconnectFromUDP();//Close UDP Socket
                    //LOG.info("->NM kicked us off audio transmit!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    //reply with ack
                    //this.getSendQueue().add(this.getEncode().ReleasePriorityToken(this.ct.getClient_ID(),2));
                }
                break;
            case 7:
                /*Client Disconnect Reply Received*/
                //this.ct.setClient_ID(-1);
                //disconnectFromUDP();//Close UDP Socket if it was open
                //this.ct.killThreads();
                break;
            case 10:
                /*GracefulShutdown Request Received*/
                this.getSendQueue().add(this.getEncode().GracefulShutdown(null, 1));//Tell server we are shutting down
                //disconnectFromUDP();//Close UDP Socket if it was open
                //this.ct.killThreads();
                //this._running = false;
                break;
        }
    }
}
