package com.example.weston.classie_talkie_app;

import android.os.StrictMode;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Weston on 3/6/2017.
 */

public class NM_Thread {
    private static String TAG = "AudioClient";

    /* Send/Receive Queue */
    private Queue<String> sendQueue;
    private Queue<Message> receiveQueue;

    private Socket _socket;
    private boolean _waitingToConnect = true;
    private boolean _running = true;
    private boolean _authServerPw = false;
    private boolean _setLanPw = false;
    private boolean _muteConvo = false;
    private InetAddress _inetAddr;
    private int _port;
    private int managerID = -1;//need to fix this
    private boolean _priorityToken = false;
    private NM_Convo nm_convo;

    /*TCP Send/Receive Threads*/
    private TCP_Sender tcp_sender;
    private TCP_Receiver tcp_receiver;

    private MainActivity ma;

    public NM_Thread(MainActivity ma)
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        this.setMa(ma);

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
                    nm_convo.check();
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
            System.out.println("Connect to "+this._inetAddr+":"+ this._port);
            this._socket.connect(new InetSocketAddress(this._inetAddr,  this._port), 1000);
            System.out.println("connected ");
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

        //Initialize NM Conversation
        this.nm_convo = new NM_Convo(this.getSendQueue(),this.getReceiveQueue(),this);
        this._waitingToConnect = false;
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

    public boolean is_waitingToConnect() {
        return _waitingToConnect;
    }

    public void set_waitingToConnect(boolean _waitingToConnect) {
        this._waitingToConnect = _waitingToConnect;
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


    public boolean is_priorityToken() {
        return _priorityToken;
    }

    public void set_priorityToken(boolean _priorityToken) {
        this._priorityToken = _priorityToken;
    }

    public NM_Convo getNm_convo() {
        return nm_convo;
    }

    public void setNm_convo(NM_Convo nm_convo) {
        this.nm_convo = nm_convo;
    }

    public TCP_Sender getTcp_sender() {
        return tcp_sender;
    }

    public void setTcp_sender(TCP_Sender tcp_sender) {
        this.tcp_sender = tcp_sender;
    }

    public int getManagerID() {
        return managerID;
    }

    public void setManagerID(int managerID) {
        this.managerID = managerID;
    }

    public boolean is_authServerPw() {
        return _authServerPw;
    }

    public void set_authServerPw(boolean _authServerPw) {
        this._authServerPw = _authServerPw;
    }

    public boolean is_setLanPw() {
        return _setLanPw;
    }

    public void set_setLanPw(boolean _setLanPw) {
        this._setLanPw = _setLanPw;
    }

    public boolean is_muteConvo() {
        return _muteConvo;
    }

    public void set_muteConvo(boolean _muteConvo) {
        this._muteConvo = _muteConvo;
    }

    public MainActivity getMainActivity() {
        return ma;
    }

    public void setMa(MainActivity ma) {
        this.ma = ma;
    }
}
