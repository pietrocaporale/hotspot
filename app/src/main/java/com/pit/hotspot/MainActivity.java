package com.pit.hotspot;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    public Context context;
    public Toast toast;
    public int duration;
    public CharSequence text;

    public TextView TxwTitle;
    public TextView TxwStatus;
    public Button butRefresh;

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
            TxwStatus.setText(R.string.no_internet);
            toast = Toast.makeText(context, R.string.no_internet, duration);
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
                TxwStatus.setText(R.string.hs_off);
            } else {
                showStatus(true,"Set to ");

                TxwStatus.setText(R.string.hs_on);
            }
        } else {
            toast = Toast.makeText(context, R.string.hs_no_operate, duration);
            toast.show();
        }

    }
    private void showStatus(boolean status, String addText) {
        String text;
        if (status) {
            text = addText + getResources().getString(R.string.status_hs_on);
        } else {
            text = addText + getResources().getString(R.string.status_hs_off);
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