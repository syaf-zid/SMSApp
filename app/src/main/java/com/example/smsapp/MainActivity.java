package com.example.smsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText etTo, etContent;
    Button btnSend, btnSendViaMsg;
    BroadcastReceiver br;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();

        etTo = findViewById(R.id.editTextTo);
        etContent = findViewById(R.id.editTextContent);
        btnSend = findViewById(R.id.buttonSend);
        btnSendViaMsg = findViewById(R.id.buttonSendViaMsg);
        br = new MessageReceiver();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        this.registerReceiver(br, filter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toNum = etTo.getText().toString();
                String content = etContent.getText().toString();

                SmsManager smsManager = SmsManager.getDefault();

                if(toNum.contains(", ")) {
                    String[] splitNum = toNum.split(", ");
                    for(int i = 0; i < splitNum.length; i++) {
                        smsManager.sendTextMessage(splitNum[i], null, content, null, null);
                    }
                } else {
                    smsManager.sendTextMessage(toNum, null, content, null, null);
                }




                Toast sentMsg = Toast.makeText(MainActivity.this, "Message sent", Toast.LENGTH_SHORT);
                sentMsg.show();
            }
        });

        btnSendViaMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toNum = etTo.getText().toString();
                String content = etContent.getText().toString();

                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setType("vnd.android-dir/mms-sms");
                sendIntent.setData(Uri.parse("smsto:" + toNum));
                sendIntent.putExtra("sms_body", content);
                startActivity(sendIntent);
            }
        });
    }

    private void checkPermission() {
        int permissionSendSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);
        int permissionRecvSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);
        if (permissionSendSMS != PackageManager.PERMISSION_GRANTED &&
                permissionRecvSMS != PackageManager.PERMISSION_GRANTED) {
            String[] permissionNeeded = new String[]{Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS};
            ActivityCompat.requestPermissions(this, permissionNeeded, 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(br);
    }
}
