package com.example.weston.classie_talkie_app;

import java.util.Queue;

/**
 * Created by Weston on 2/7/2017.
 */

public class Convo {

    private Queue<String> sendQueue;
    private Queue<Message> receiveQueue;
    private Message_Encoder encode;

    public Convo(Queue<String> sendQueue, Queue<Message> receiveQueue)
    {
        this.setSendQueue(sendQueue);
        this.setReceiveQueue(receiveQueue);
        this.setEncode(new Message_Encoder());
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

    public Message_Encoder getEncode() {
        return encode;
    }

    public void setEncode(Message_Encoder encode) {
        this.encode = encode;
    }
}
