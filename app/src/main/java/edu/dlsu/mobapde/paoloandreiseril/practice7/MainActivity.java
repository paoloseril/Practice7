package edu.dlsu.mobapde.paoloandreiseril.practice7;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button downloadBtn, resumeBtn;
    int jobID;
    TextView notification;
    AlarmManager manager;

    private static final String UI_UPDATE_TAG = "ph.ccs.mobapde.practice7";
    private BroadcastReceiver receiver = new NotificationReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the UI components on code
        downloadBtn = findViewById(R.id.downloadButton);
        resumeBtn = findViewById(R.id.resumeButton);
        notification = findViewById(R.id.notifications);

        resumeBtn.setEnabled(false);

        jobID = 1;

        manager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    // To cancel the repeating alarm, need to have three things:
    // 1. Intent should have the same action
    // 2. PendingIntent should have same context, request code (meaning the next send of the job), and intent
    // 3. AlarmManagers have a cancel method, lol.
    public void stopNotifying(View view) {

        Intent intent = new Intent(UI_UPDATE_TAG);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1000000+jobID, intent, 0);
        manager.cancel(pendingIntent);
        resumeBtn.setEnabled(true);
        downloadBtn.setEnabled(false);

        // Make Toast
        Toast.makeText(this, "Downloaded!", Toast.LENGTH_LONG).show();
    }

    private void startTheService() {
        Intent alarmIntent = new Intent(UI_UPDATE_TAG);
        jobID++;

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                1000000+jobID, alarmIntent, 0);

        // Set alarm to repeat every 5 seconds (5000 ms)
        // manager.setInexactRepeating -- can set alarm for any intervalMillis time
        // manager.setRepeating -- minimum time to set an alarm is every one minute (60000 ms)
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 5000, pendingIntent);
    }

    public void startNotifying(View view) {
        Toast.makeText(this, "Resuming download", Toast.LENGTH_LONG).show();
        resumeBtn.setEnabled(false);
        downloadBtn.setEnabled(true);
        notification.setText("");
        startTheService();
    }

    class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("ALARM AND BROADCAST","NotificationReceiver onReceive");
            notification.append("Is it downloading?\n\n");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Register the receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(UI_UPDATE_TAG);
        registerReceiver(receiver, filter);

        // Start the service
        startTheService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }
}
