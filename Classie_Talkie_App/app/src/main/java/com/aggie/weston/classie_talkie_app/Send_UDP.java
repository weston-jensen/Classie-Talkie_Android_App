package com.aggie.weston.classie_talkie_app;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaActionSound;
import android.media.MediaRecorder;
import android.net.rtp.AudioStream;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static android.content.ContentValues.TAG;
import static android.media.AudioRecord.getMinBufferSize;

/**
 * Created by Weston on 3/4/2017.
 */

public class Send_UDP{
    private static String TAG = "AudioClient";
    // the audio recording options
    private static final int RECORDING_RATE = 44100;
    private static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private static final int FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static int BUFFER_SIZE =  4410;
    private AudioRecord recorder;
    private boolean currentlySendingAudio = false;
    private static String SERVER;
    private static int PORT = 13002;




    public Send_UDP(String serverIP)
    {
        this.SERVER = serverIP;
    }

    public void startStreamingAudio()
    {
        Log.i(TAG,"Start streaming audio");
        currentlySendingAudio = true;
        startStreaming();

    }

    public void stopStreamingAudio()
    {
        Log.i(TAG,"Stopped streaming audio");
        currentlySendingAudio = false;
        recorder.release();
    }



    private void startStreaming()
    {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
        Log.i(TAG, "Starting the background thread to stream the audio data");

        Thread streamThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                System.out.println("Running UDP streamer");
                try{
                    DatagramSocket socket = new DatagramSocket();

                    final InetAddress serverAddress = InetAddress.getByName(SERVER);
                    DatagramPacket packet;



                    int bufferSize = AudioRecord.getMinBufferSize(RECORDING_RATE,
                            AudioFormat.CHANNEL_IN_MONO,
                            AudioFormat.ENCODING_PCM_16BIT);

                    recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                            RECORDING_RATE,
                            AudioFormat.CHANNEL_IN_MONO,
                            AudioFormat.ENCODING_PCM_16BIT,
                            BUFFER_SIZE);

                   byte[] buffer = new byte[BUFFER_SIZE];

                    Log.i(TAG,"BufferSize = "+bufferSize);

                    if (recorder.getState() != AudioRecord.STATE_INITIALIZED) {
                        Log.i(TAG,"Could not initialize Recorder");
                    }

                    try {
                        recorder.startRecording();
                    }catch(Exception e)
                    {
                        Log.e(TAG,"Could not start recording");
                        e.printStackTrace();
                    }
                    socket.setSendBufferSize(BUFFER_SIZE); //try setting to something like 10000
                    //socket.setSendBufferSize(BUFFER_SIZE/2);
                    while(currentlySendingAudio)
                    {
                        //could try reducing
                        int read = recorder.read(buffer,0,buffer.length);

                        packet = new DatagramPacket(buffer, buffer.length, serverAddress,PORT);
                        socket.send(packet);
                    }
                    Log.d(TAG, "AudioRecord finished recording");

                }catch(Exception e){
                    Log.e(TAG, "Exception: "+e);
                }
            }
        });

        streamThread.start();
    }

}

