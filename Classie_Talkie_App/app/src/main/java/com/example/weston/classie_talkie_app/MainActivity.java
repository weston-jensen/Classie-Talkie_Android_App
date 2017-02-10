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

    private Client_Thread client_thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        final Button button1 = (Button) findViewById(R.id.studentOption);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ipAndPort_UI();
            }
        });

        final Button button2 = (Button) findViewById(R.id.profOption);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ipAndPort_UI();
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

                client_thread = new Client_Thread(serverAddr,serverPort);
                client_thread.execute();

                clientInfo_UI();

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

                Message_Encoder me = new Message_Encoder();
                client_thread.getSendQueue().add(me.AuthenticateClient(firstName,lastName,anum,password,-1,-1));

                PTT_UI();

            }
        });

    }

    private void PTT_UI() {
        setContentView(R.layout.push_to_talk);

        final Button button = (Button) findViewById(R.id.ptt_btn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Message_Encoder me = new Message_Encoder();
                client_thread.getSendQueue().add(me.RequestPriorityToken(0,-1));
            }
        });

        final Button button2 = (Button) findViewById(R.id.leaveLAN_btn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //ipAndPort_UI();
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

