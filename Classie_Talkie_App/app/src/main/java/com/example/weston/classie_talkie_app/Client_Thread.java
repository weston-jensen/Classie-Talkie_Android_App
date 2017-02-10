package com.example.weston.classie_talkie_app;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.Queue;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;

/**
 * Created by Weston on 2/7/2017.
 */

public class Client_Thread extends AsyncTask<Void, Void, Void>{

    //Todo
    /*
        Figure out how to update UI from here
        figure out how to receive messages
        figure out how to end 
     */

    /* Send/Receive Queue */
    private Queue<Message> sendQueue;
    private Queue<Message> receiveQueue;

    private Socket _socket;
    private boolean _running = true;
    private InetAddress _inetAddr;
    private int _port;

    private Client_Convo client_convo;

    /*TCP Send/Receive Threads*/
    private TCP_Sender tcp_sender;
    private TCP_Receiver tcp_receiver;

    public Client_Thread(String inetAddr_str, String port)
    {
        //Initialize Message Queues
        this.setSendQueue(new LinkedList<Message>());
        this.setReceiveQueue(new LinkedList<Message>());

        //Set Params
        this.set_port(12001);
        try {
        this._inetAddr = InetAddress.getByName(inetAddr_str);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    protected Void doInBackground(Void... arg0) {
        //Connect to The Server
        //Start TCP Communication
        if(connectToServer()>0)
        {
            System.out.println("Connected, Starting to run system");
            startTcpComm();
            run();


        }
        else
        {
            System.out.println("FAIL");
        }

        return null;
    }

    public void run()
    {
        while(this._running)
        {
           this.tcp_receiver.check();//Check if we received any Messages
           this.client_convo.check();//Deal with those Messages
            Message_Encoder me = new Message_Encoder();
            getSendQueue().add(me.RequestPriorityToken(0,-1));

            this.tcp_sender.check();//Send our reply to Server
        }

        //When We Fall out of the loop
        try {
            this._socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.print("For Some reason client thread ended");
    }

    public int connectToServer() {
       try {
           // TCP Socket
           this._socket = new Socket();
           this._socket.connect(new InetSocketAddress(this._inetAddr, this.get_port()), 1000);
           if (!this.get_socket().isConnected()) {
               // if not connected, return -1 as an error
               System.out.println("Not connected!!!!!!");
               this.set_running(false);
               return -1;
           }
       }catch(IOException e)
       {
           System.out.println("Catch block caugh error");
           e.printStackTrace();
       }
        System.out.println("Connected!!!!!!");
        return 1;
    }

    public int startTcpComm() {
        //Initialize TCP Sender/Receiver
        this.tcp_sender = new TCP_Sender(this.get_socket(), this.getSendQueue());
        this.tcp_receiver = new TCP_Receiver(this.get_socket(), this.getReceiveQueue());

        //Initialize Client Conversation
        this.client_convo = new Client_Convo(this.getSendQueue(), this.getReceiveQueue());

        // Start Threads
        //this.tcp_sender.run();
        //this.tcp_receiver.run();
        //this.client_convo.run();

        return 1;
    }


    public Queue<Message> getSendQueue() {
        return sendQueue;
    }

    public void setSendQueue(Queue<Message> sendQueue) {
        this.sendQueue = sendQueue;
    }

    public Queue<Message> getReceiveQueue() {
        return receiveQueue;
    }

    public void setReceiveQueue(Queue<Message> receiveQueue) {
        this.receiveQueue = receiveQueue;
    }

    public Socket get_socket() {
        return _socket;
    }

    public void set_socket(Socket _socket) {
        this._socket = _socket;
    }

    public boolean is_running() {
        return _running;
    }

    public void set_running(boolean _running) {
        this._running = _running;
    }

    public InetAddress get_inetAddr() {
        return _inetAddr;
    }

    public void set_inetAddr(InetAddress _inetAddr) {
        this._inetAddr = _inetAddr;
    }

    public int get_port() {
        return _port;
    }

    public void set_port(int _port) {
        this._port = _port;
    }

    public Client_Convo getClient_convo() {
        return client_convo;
    }

    public void setClient_convo(Client_Convo client_convo) {
        this.client_convo = client_convo;
    }

    public TCP_Sender getTcp_sender() {
        return tcp_sender;
    }

    public void setTcp_sender(TCP_Sender tcp_sender) {
        this.tcp_sender = tcp_sender;
    }

    public TCP_Receiver getTcp_receiver() {
        return tcp_receiver;
    }

    public void setTcp_receiver(TCP_Receiver tcp_receiver) {
        this.tcp_receiver = tcp_receiver;
    }
}
