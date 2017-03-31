package com.example.weston.classie_talkie_app;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.Queue;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;

/**
 * Created by Weston on 2/7/2017.
 */

public class Client_Thread {
    private static String TAG = "AudioClient";
    //Todo
    /*
        Figure out how to update UI from here
        figure out how to receive messages
        figure out how to end 
     */

    /* Send/Receive Queue */
    private Queue<String> sendQueue;
    private Queue<Message> receiveQueue;

    private Socket _socket;
    private boolean _waitingToConnect = true;
    private boolean _running = true;
    private InetAddress _inetAddr;
    private int _port;
    private int clientID = -1;
    private boolean _priorityToken = false;
    private Client_Convo client_convo;

    /*TCP Send/Receive Threads*/
    private TCP_Sender tcp_sender;
    private TCP_Receiver tcp_receiver;

    /*Flags*/
    private boolean isAuthenticated = false;
    private MainActivity ma;


    public Client_Thread(MainActivity ma)
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        this.ma = ma;
        //Initialize Message Queues
        this.setSendQueue(new LinkedList<String>());
        this.setReceiveQueue(new LinkedList<Message>());
    }

    //BackGround Process Runs here
    public void beginThreads()
    {
        Thread serverThread  = new Thread(new Runnable()
        {
            @Override
            public void run() {

                System.out.println("starting to run....");
                while (_waitingToConnect) {
                    //System.out.println("stuck in the first while loop");
                }

                tcp_receiver.check();//Check if we received any Messages
                tcp_sender.check();//Send our reply to Server

                while (_running)
                {
                    client_convo.check();
                }

                System.out.print("Server Connection has been lost");

                //When We Fall out of the loop
                try {
                    _socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        serverThread.start();
    }




    public int connectToServer(String ip, String port) {
        System.out.println("Trying to connect to server");
        //Set Params
        this._port = 12001;

        try {
            this._inetAddr = InetAddress.getByName(ip);
        }catch(IOException e){
            System.out.println("Could not assign inetAddr");
            e.printStackTrace();
        }

       try {
           // TCP Socket
           this._socket = new Socket();
           System.out.println("Will it connect? to "+this._inetAddr+":"+ this._port);
           this._socket.connect(new InetSocketAddress(this._inetAddr,  this._port), 1000); //this is where its breaking
           System.out.println("connected ah yeah");
           if (!this.get_socket().isConnected())
           {
               // if not connected, return -1 as an error
               System.out.println("Not connected!!!!!!");
               this.set_running(false);
               return -1;
           }
       }catch(IOException e)
       {
           System.out.println("Catch block caught error");
           e.printStackTrace();
           return -1;
       }
        System.out.println("Connected!!!!!!");
        startTcpComm();
        return 1;
    }

    public void startTcpComm() {
        //Initialize TCP Sender/Receiver
        this.tcp_sender = new TCP_Sender(this.get_socket(), this.getSendQueue());
        this.tcp_receiver = new TCP_Receiver(this.get_socket(), this.getReceiveQueue());

        //Initialize Client Conversation
        this.client_convo = new Client_Convo(this.getSendQueue(), this.getReceiveQueue(), this);
        this._waitingToConnect = false;
    }

    public MainActivity getMainActivity()
    {
        return ma;
    }

    public Queue<String> getSendQueue() {
        return sendQueue;
    }

    public void setSendQueue(Queue<String> sendQueue) {
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

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }

    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    public boolean is_priorityToken() {
        return _priorityToken;
    }

    public void set_priorityToken(boolean _priorityToken) {
        this._priorityToken = _priorityToken;
    }
}
