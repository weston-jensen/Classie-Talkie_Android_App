package com.example.weston.classie_talkie_app;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.*;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import static android.R.attr.start;

public class MainActivity extends AppCompatActivity {
    private String serverAddr;
    private String serverPort;
    private String firstName;
    private String lastName;
    private String anum;
    private String password;

    private Thread bgt;
    private NM_Thread nm_thread;
    private Client_Thread client_thread;
    private Message_Encoder mesgEncoder;
    private Send_UDP send_Udp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        final Button button1 = (Button) findViewById(R.id.studentOption);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mesgEncoder = new Message_Encoder();
                client_thread = new Client_Thread();//initialize
                ipAndPort_UI();//change to get Ip and Port UI
            }
        });

        final Button button2 = (Button) findViewById(R.id.profOption);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mesgEncoder = new Message_Encoder();
                nm_thread = new NM_Thread();
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
                    clientInfo_UI();//Change Frame
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

                nm_thread.getSendQueue().add(mesgEncoder.AuthenticateManager(serverPasswd,"Authenticating",-1,-1));

                //TODO
                //dont change UI until we are authenticated
                nm_lan_info_UI();

            }
        });
    }

    private void nm_lan_info_UI() {
        setContentView(R.layout.nm_info);

        final Button button = (Button) findViewById(R.id.lanEnter_btn);
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                String lanPasswd;
                final EditText serverpw = (EditText) findViewById(R.id.lanPswd_tf);
                lanPasswd = serverpw.getText().toString();

                nm_thread.getSendQueue().add(mesgEncoder.CreateLAN(nm_thread.getManagerID(),lanPasswd,-1));

                //TODO
                //dont change UI until we receive ACK

            }
        });
    }







    private void ipAndPort_UI() {
        setContentView(R.layout.ip_port);

        final Button button = (Button) findViewById(R.id.connectBtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final EditText edit_sip =  (EditText) findViewById( R.id.serverIP_tf);
                serverAddr = edit_sip.getText().toString();

                final EditText edit_sp =  (EditText) findViewById( R.id.serverPort_tf);
                serverPort = edit_sp.getText().toString();

                System.out.println("just before trying to connect");

                //send_Udp = new Send_UDP(serverAddr);
                //PTT_UI();


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

                //could do a quick process right here...

                //client_thread.getSendQueue().add(mesgEncoder.AuthenticateClient(firstName,lastName,anum,password,-1,-1));
                client_thread.getSendQueue().add(mesgEncoder.AuthenticateClient("Weston", "Jensen", "A01211187", "password",-1,-1));



                if(client_thread.isAuthenticated())
                {
                    send_Udp = new Send_UDP(serverAddr);
                    PTT_UI();
                }
                else
                {
                    //display error message
                    final TextView edit_error = (TextView) findViewById(R.id.ciErrorMsg);
                    edit_error.setText("Error Authenticating to server");
                }

            }
        });

    }

    private void PTT_UI() {
        setContentView(R.layout.push_to_talk);

        System.out.println("IN PTT");

        final Button startButton = (Button) findViewById(R.id.ptt_btn);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("PTT CLICK");
                client_thread.getSendQueue().add(mesgEncoder.RequestPriorityToken(0,-1));

                if(client_thread.is_priorityToken())
                {
                    send_Udp.startStreamingAudio();
                    Transmit_UI();
                }
            }
        });

        final Button button2 = (Button) findViewById(R.id.leaveLAN_btn);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ipAndPort_UI();
            }
        });
    }

    private void Transmit_UI()
    {
        setContentView(R.layout.transmit_audio);

        final Button Stopbutton = (Button) findViewById(R.id.pushToStop);
        Stopbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Stoped Transmitting");
                send_Udp.stopStreamingAudio();
                client_thread.set_priorityToken(false);//release priority token
                client_thread.getSendQueue().add(mesgEncoder.ReleasePriorityToken(client_thread.getClientID(),1));//Tell Server we no longer need token
                PTT_UI();//go back to PTT
            }
        });
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



}

