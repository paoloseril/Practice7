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

    Button downloadBtn;
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
        notification = findViewById(R.id.notifications);

        jobID = 1;

        manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Register the receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(UI_UPDATE_TAG);
        registerReceiver(receiver, filter);

        // Start the service
        startTheService();
    }

    // To cancel the repeating alarm, need to have three things:
    // 1. Intent should have the same action
    // 2. PendingIntent should have same context, request code (meaning the next send of the job), and intent
    // 3. AlarmManagers have a cancel method, lol.
    public void stopNotifying(View view) {

        Intent intent = new Intent(UI_UPDATE_TAG);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1000000+jobID, intent, 0);
        manager.cancel(pendingIntent);

        // Make Toast
        Toast.makeText(getApplicationContext(), "Downloaded!", Toast.LENGTH_LONG).show();
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

    class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("ALARM AND BROADCAST","NotificationReceiver onReceive");
            notification.append("Is it downloading?\n\n");
        }
    }
}
