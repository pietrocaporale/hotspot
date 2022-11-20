package com.pit.hotspot;

import static android.content.ContentValues.TAG;

import static com.pit.hotspot.R.color.green;
import static com.pit.hotspot.R.color.red;
import static com.pit.hotspot.R.color.white;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public Context context;
    public Toast toast;
    public int duration;
    //public CharSequence text;
    public TextView TxwStatus;
    public ImageButton butGreen;
    public ImageButton butRed;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //case R.id.add:
        if (item.getItemId() == R.id.exit) {
            onDestroy();
            finish();
            return (true);
        }
        return(super.onOptionsItemSelected(item));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "MainActivity:onDestroy");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TxwStatus = findViewById(R.id.status);
        butGreen = findViewById(R.id.imageButtonGreen);
        butRed = findViewById(R.id.imageButtonRed);
        context = getApplicationContext();
        duration = Toast.LENGTH_SHORT;

        if (!showWritePermissionSettings()) {
            MainActivity.this.finish();
            System.exit(0);     // on below line we are exiting our activity
        }

        //status bar
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);


        boolean connection = ApManager.testInternetConnection(context);
        if (connection) {
            changeStatus();
        } else {
            TxwStatus.setText(R.string.no_internet);
            toast = Toast.makeText(context, R.string.no_internet, duration);
            toast.show();
        }
        butGreen.setOnClickListener(view -> changeStatus());
        butRed.setOnClickListener(view -> changeStatus());

    }
    private void changeStatus() {
        boolean status = ApManager.isApOn(MainActivity.this);
        showStatus(status,"");
        boolean change = ApManager.configApState(MainActivity.this);
        if (change) {
            if (status) {
                showStatus(false,"Hotspot ");
                TxwStatus.setText(R.string.hs_off);
            } else {
                showStatus(true,"Hotspot ");
                TxwStatus.setText(R.string.hs_on);
            }
        } else {
            toast = Toast.makeText(context, R.string.hs_no_operate, duration);
            toast.show();
        }

    }
    private void showStatus(boolean status, String addText) {
        String text;
        int backgroundColor;
        int textColor;
        if (status) {
            text = addText + getResources().getString(R.string.status_hs_on);
            backgroundColor = ContextCompat.getColor(context, green);
            textColor = ContextCompat.getColor(context, white);
            TxwStatus.setTextColor(textColor);
            TxwStatus.setBackgroundColor(backgroundColor);
            butRed.setVisibility(View.INVISIBLE);
            butGreen.setVisibility(View.VISIBLE);
        } else {
            text = addText + getResources().getString(R.string.status_hs_off);
            backgroundColor = ContextCompat.getColor(context, red);
            textColor = ContextCompat.getColor(context, white);
            TxwStatus.setTextColor(textColor);
            TxwStatus.setBackgroundColor(backgroundColor);
            butGreen.setVisibility(View.INVISIBLE);
            butRed.setVisibility(View.VISIBLE);
        }
        if ( !addText.equals("")) {
            toast = Toast.makeText(context, text, duration);
            toast.show();
        }
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