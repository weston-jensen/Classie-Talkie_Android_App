package com.aggie.weston.classie_talkie_app;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.Thing;

public class MainActivity extends Activity {
    public String serverAddr;
    public String serverPort;
    private String firstName;
    private String lastName;
    private String anum;
    private String password;

    private Thread bgt;
    private NM_Thread nm_thread;
    private Client_Thread client_thread;
    private Message_Encoder mesgEncoder;
    private Send_UDP send_Udp;
    private MainActivity ma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        ma = this;

        final Button button1 = (Button) findViewById(R.id.studentOption);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mesgEncoder = new Message_Encoder();
                client_thread = new Client_Thread(ma);//initialize
                ipAndPort_UI();//change to get Ip and Port UI
            }
        });

        final Button button2 = (Button) findViewById(R.id.profOption);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mesgEncoder = new Message_Encoder();
                nm_thread = new NM_Thread(ma);
                ipAndPort_NM_UI();
            }
        });
    }

    private void ipAndPort_NM_UI() {
        setContentView(R.layout.ip_port);

        final Button button = (Button) findViewById(R.id.connectBtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final EditText edit_sip =  (EditText) findViewById( R.id.serverIP_tf);
                serverAddr = edit_sip.getText().toString();

                final EditText edit_sp =  (EditText) findViewById( R.id.serverPort_tf);
                serverPort = edit_sp.getText().toString();

                System.out.println("just before trying to connect");
                if(nm_thread.connectToServer(serverAddr, serverPort)>0)//connect to the server
                {
                    nm_thread.beginThreads();
                    nm_info_UI();//Change Frame
                }
            }
        });
    }

    private void nm_info_UI() {
        setContentView(R.layout.nm_info);

        final Button button = (Button) findViewById(R.id.serverPswd_btn);
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                String serverPasswd;
                final EditText serverpw = (EditText) findViewById(R.id.serverPswd_tf);
                serverPasswd = serverpw.getText().toString();

                nm_thread.getSendQueue().add(mesgEncoder.AuthenticateManager("CS-5200","Authenticating",-1,-1));
            }
        });
    }

    public void nm_lan_info_UI()
    {
        setContentView(R.layout.nm_lan_info);

        final Button LanEnter = (Button) findViewById(R.id.lanEnter_btn);
        LanEnter.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                String lanPasswd;
                final EditText lanpw = (EditText) findViewById(R.id.lanPswd_tf);
                lanPasswd = lanpw.getText().toString();

                nm_thread.getSendQueue().add(mesgEncoder.CreateLAN(nm_thread.getManagerID(),lanPasswd,-1));
            }
        });
    }

    public void nm_control1_UI() {
        setContentView(R.layout.nm_control_1);

        final Button mute = (Button) findViewById(R.id.mute_btn);
        mute.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                nm_thread.getSendQueue().add(mesgEncoder.MuteComm(nm_thread.getManagerID(),-1));
                nm_control2_UI();
            }
        });

        final Button restart = (Button) findViewById(R.id.end_lan_btn1);
        restart.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                nm_thread.getSendQueue().add(mesgEncoder.EndLAN(nm_thread.getManagerID(),-1));
            }
        });

        final Button shutdown = (Button) findViewById(R.id.shutdown_btn_1);
        shutdown.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                nm_thread.getSendQueue().add(mesgEncoder.GracefulShutdown(nm_thread.getManagerID(),-1));
                ma.finish();
            }
        });
    }

    private void nm_control2_UI() {
        setContentView(R.layout.nm_control_2);

        final Button mute = (Button) findViewById(R.id.unmute_btn);
        mute.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                nm_thread.getSendQueue().add(mesgEncoder.UnMuteComm(nm_thread.getManagerID(),-1));
                nm_control1_UI();
            }
        });

        final Button restart = (Button) findViewById(R.id.end_lan_btn2);
        restart.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                nm_thread.getSendQueue().add(mesgEncoder.EndLAN(nm_thread.getManagerID(),-1));
            }
        });

        final Button shutdown = (Button) findViewById(R.id.shutdown_btn_2);
        shutdown.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                nm_thread.getSendQueue().add(mesgEncoder.GracefulShutdown(nm_thread.getManagerID(),-1));
                ma.finish();
            }
        });
    }



    /*********************Client Interface Functions********************************/
    private void ipAndPort_UI() {
        setContentView(R.layout.ip_port);

        final Button button = (Button) findViewById(R.id.connectBtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final EditText edit_sip =  (EditText) findViewById( R.id.serverIP_tf);
                serverAddr = edit_sip.getText().toString();

                final EditText edit_sp =  (EditText) findViewById( R.id.serverPort_tf);
                serverPort = edit_sp.getText().toString();

                if(client_thread.connectToServer(serverAddr, serverPort)>0)//connect to the server
                {
                    client_thread.beginThreads();
                    clientInfo_UI();//Change Frame
                }

            }
        });
    }



    private void clientInfo_UI() {
        setContentView(R.layout.client_info);

        final Button button = (Button) findViewById(R.id.client_info_btn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final EditText edit_fname =  (EditText) findViewById( R.id.fname_tf);
                firstName = edit_fname.getText().toString();

                final EditText edit_lname =  (EditText) findViewById( R.id.lname_tf);
                lastName = edit_lname.getText().toString();

                final EditText edit_anum =  (EditText) findViewById( R.id.anum_tf);
                anum = edit_anum.getText().toString();

                final EditText edit_passwd =  (EditText) findViewById( R.id.password_tf);
                password = edit_passwd.getText().toString();

                //Send Message to Server
                client_thread.getSendQueue().add(mesgEncoder.AuthenticateClient(firstName,lastName,anum,password,client_thread.getClientID(),-1));
            }
        });

    }

    public void PTT_UI() {
        setContentView(R.layout.push_to_talk);

        System.out.println("IN PTT");

        final Button startButton = (Button) findViewById(R.id.ptt_btn);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("PTT CLICK");
                client_thread.getSendQueue().add(mesgEncoder.RequestPriorityToken(client_thread.getClientID(),-1));
            }
        });

        final Button closeButton = (Button) findViewById(R.id.closeClient_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ma.finish();
            }
        });

        final Button button2 = (Button) findViewById(R.id.leaveLAN_btn);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ipAndPort_UI();
            }
        });
    }


    public void Transmit_UI()
    {
        setContentView(R.layout.transmit_audio);

        final Button Stopbutton = (Button) findViewById(R.id.pushToStop);
        Stopbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Stopped Transmitting");
                getSend_Udp().stopStreamingAudio();
                client_thread.set_priorityToken(false);//release priority token
                client_thread.getSendQueue().add(mesgEncoder.ReleasePriorityToken(client_thread.getClientID(),-1));//Tell Server we no longer need token

                PTT_UI();//go back to PTT
            }
        });
    }

    public void setText(int type, String text)
    {
        switch(type)
        {
            case 0:
                final TextView edit_error = (TextView) findViewById(R.id.ciErrorMsg);
                edit_error.setText(text);
                break;
            case 1:
                TextView mesg = (TextView) findViewById(R.id.msgFeedback_lbl);
                mesg.setText(text);
                break;
            case 2:
                TextView nmMesg = (TextView) findViewById(R.id.nm_mesgStatus);
                nmMesg.setText(text);
                break;
            case 3:
                TextView nm_infoMesg = (TextView) findViewById(R.id.nm_infoMesg);
                nm_infoMesg.setText(text);
                break;

        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }


    public Send_UDP getSend_Udp() {
        return send_Udp;
    }

    public void setSend_Udp(Send_UDP send_Udp) {
        this.send_Udp = send_Udp;
    }
}

