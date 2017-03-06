package com.example.weston.classie_talkie_app;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static android.content.ContentValues.TAG;
import static android.media.AudioRecord.getMinBufferSize;

/**
 * Created by Weston on 3/4/2017.
 */

public class Send_UDP {
    private static String TAG = "AudioClient";
    // the audio recording options
    private static final int RECORDING_RATE = 44100;
    private static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private static final int FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static int BUFFER_SIZE = getMinBufferSize(
    RECORDING_RATE, CHANNEL, FORMAT);
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

                    /*recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                            RECORDING_RATE, CHANNEL, FORMAT, BUFFER_SIZE * 10);*/

                    recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                            RECORDING_RATE,
                            AudioFormat.CHANNEL_IN_MONO,
                            AudioFormat.ENCODING_PCM_16BIT,
                            bufferSize);

                   // short[] buffer = new short[bufferSize / 2];

                   byte[] buffer = new byte[bufferSize/2];

                    Log.i(TAG,"BufferSize = "+bufferSize);

                    if (recorder.getState() != AudioRecord.STATE_INITIALIZED) {
                        Log.i(TAG,"Could not initialize Recorder");
                    }

                    recorder.startRecording();


                    while(currentlySendingAudio)
                    {
                        int read = recorder.read(buffer,0,buffer.length);


                        Filter filter2 = new Filter(85,44100, Filter.PassType.Highpass,1);
                        for (int i = 0; i < buffer.length; i++)
                        {
                            filter2.Update(buffer[i]);
                            buffer[i] = (byte) filter2.getValue();
                        }


                        Filter filter = new Filter(4500,44100, Filter.PassType.Lowpass,1);
                        for (int i = 0; i < buffer.length; i++)
                        {
                            filter.Update(buffer[i]);
                            buffer[i] = (byte) filter.getValue();
                        }

                        packet = new DatagramPacket(buffer, read,
                                serverAddress,PORT);

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

