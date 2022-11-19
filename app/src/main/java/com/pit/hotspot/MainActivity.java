package com.pit.hotspot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public Context context;
    public Toast toast;
    public int duration;
    public CharSequence text;

    public TextView TxwTitle;
    public TextView TxwStatus;
    public Button butRefresh;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TxwTitle = findViewById(R.id.title);
        TxwStatus = findViewById(R.id.status);
        butRefresh = findViewById(R.id.butRefresh);

        context = getApplicationContext();
        duration = Toast.LENGTH_SHORT;

        if (!showWritePermissionSettings()) {
            MainActivity.this.finish();
            System.exit(0);     // on below line we are exiting our activity
        }

        boolean connection = ApManager.testInternetConnection(context);
        if (connection) {

            changeStatus();

        } else {
            TxwStatus.setText("No internet connection");
            text = "No internet connection";
            toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        butRefresh.setOnClickListener(view -> changeStatus());
    }
    private void changeStatus() {
        boolean status = ApManager.isApOn(MainActivity.this);
        showStatus(status,"Status ");
        boolean change = ApManager.configApState(MainActivity.this);
        if (change) {
            if (status) {
                showStatus(false,"Set to ");
                TxwStatus.setText("Hotspot is inactive");
            } else {
                showStatus(true,"Set to ");
                TxwStatus.setText("Hotspot is active");
            }
        } else {
            text = "It was not possible to operate on the Hotspot. Check permissions";
            toast = Toast.makeText(context, text, duration);
            toast.show();
        }

    }
    private void showStatus(boolean status,CharSequence addText) {
        if (status) {
            text = addText+"Hotspot active";
        } else {
            text = addText+"Hotspot inactive";
        }
        toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private boolean showWritePermissionSettings() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            if (!Settings.System.canWrite(this)) {
                Log.v("DANG", " " + !Settings.System.canWrite(this));
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(intent);
                return false;
            }
        }
        return true; //Permission already given
    }

}