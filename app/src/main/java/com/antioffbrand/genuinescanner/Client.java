package com.antioffbrand.genuinescanner;

import android.util.Log;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class Client{
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private String input;
    private String output;
    private boolean connected;
    private TextView textView;
    private boolean update;



    public Client() {
        connected = true;
        getConnection();
    }

    public void getConnection() {
        Log.i("Response", "Called getConnection");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                connected = true;

                try {
                    socket = new Socket("192.168.172.66", 800);

                    dis = new DataInputStream(socket.getInputStream());
                    dos = new DataOutputStream(socket.getOutputStream());

                    input = new String();
                    output = new String();

                    while (connected) {
                        input = dis.readUTF();
                        Log.i("Debug", input);
                        if (!(input.equals("") || input.equals("Test_Connection"))) {
                            update = true;
                        }
                    }

                } catch (IOException e) {
                    Log.i("Catch Disconnection", "Client Disconnected");
                    connected = false;
                    try {
                        socket.close();
                    } catch (IOException e1) {

                    } catch (NullPointerException e2) {

                    }
                }
            }
        });

        thread.start();
    }
    public void setString() {
        if (!input.equals("")) {
            textView.setText(input);
            update = false;
        }
    }

    public boolean getConnected() {
        return connected;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public void sendOutput(String output) {
        this.output = output;
        Log.i("User Input", this.output);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!output.equals("")) {
                        dos.writeUTF(output);
                        dos.flush();
                    }
                } catch (IOException e1) {
                    
                } catch (NullPointerException e2) {

                }
            }
        });

        thread.start();

        for (int i = 0; i < 100 && update == false; i++) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
                if (i == 99) {
                    //OutOfTimeException
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        setString();
    }
}


