package com.antioffbrand.genuinescanner;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button clearButton, proceedButton;
    TextInputEditText numberInput;
    ImageView resultImage;
    TextView testText;
    Client client;
    ReconnectDialog dialog;


    public MainActivity() {
        client = new Client();
        dialog = new ReconnectDialog(client);
        check();
    }

    public void check() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    while (true) {
                        try {
                            if (client.getConnected() == false) {
                                if (!dialog.isAdded()) {
                                    dialog.show(MainActivity.this.getFragmentManager(), "Reconnect");
                                }
                            }
                            Log.i("Client Status", String.format("%s", client.getConnected()));
                            this.wait(10000 * dialog.getMultiplier());
                        } catch (InterruptedException e) {

                        }
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clearButton = findViewById(R.id.Clear);
        proceedButton = findViewById(R.id.Proceed);
        numberInput = findViewById(R.id.NumberInput);
        resultImage = findViewById(R.id.Result);
        resultImage.setImageResource(R.drawable.title);
        testText = findViewById(R.id.TestText);
        client.setTextView(testText);

        proceedButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.equals(proceedButton)) {
            try {
                if (!numberInput.getText().toString().isEmpty()) {
                    client.sendOutput(numberInput.getText().toString());
                }
                if (testText.getText().equals("Not Found")) {
                    resultImage.setImageResource(R.drawable.notfound);
                    testText.setText("Your Purchase Has No Problem!");
                } else if (testText.getText().equals("Transaction Found")) {
                    resultImage.setImageResource(R.drawable.found);
                    testText.setText("Your Purchase Might Have A Problem\nPlease Bring Your Purchase To Our Store If You Are Concerned");
                }
            } catch (Exception e1) {
                if (!dialog.isAdded()) {
                    dialog.show(MainActivity.this.getFragmentManager(), "Reconnect");
                }
            }
        } else {
            resultImage.setImageResource(R.drawable.title);
            testText.setText(R.string.text_default);
            numberInput.setText("");
        }

        numberInput.clearFocus();
    }
}