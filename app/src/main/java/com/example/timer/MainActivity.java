package com.example.timer;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final TextView timeView = findViewById(R.id.time_text_view);

        // Set values by shared preferences
        timeView.setText(String.format("%1$02d:%2$02d:%3$02d",
                sharedPreferences.getInt("Hours", 0),
                sharedPreferences.getInt("Minutes", 1),
                sharedPreferences.getInt("Seconds", 30)
        ));

        timeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(timeView);
            }
        });

        final TextView tapText = findViewById(R.id.tap_text);
        tapText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(timeView);
            }
        });

        // Save/Set Timer
        Button buttonSave = findViewById(R.id.button_save);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent intent = new Intent(MainActivity.this, Timer.class);

                // Get time interval
                String text = timeView.getText().toString();
                String[] time = text.split( ":" );
                int hours = Integer.parseInt(time[0]);
                int minutes = Integer.parseInt(time[1]);
                int seconds = Integer.parseInt(time[2]);
                long interval = 1000 * (seconds + minutes * 60L + hours * 60L * 60L);
                String intervalStr = String.format("%1$02d:%2$02d:%3$02d", hours, minutes, seconds);

                // Send interval to notification BroadcastReceiver via intent extras
                intent.putExtra("Interval", intervalStr);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                // Set alarm
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + interval, interval, alarmIntent);
                Toast.makeText(MainActivity.this, "Timer set with an interval: " + intervalStr, Toast.LENGTH_SHORT).show();
            }
        });

        // Cancel/Stop Timer
        Button buttonCancel = findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent intent = new Intent(MainActivity.this, Timer.class);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
                if(alarmManager != null) {
                    alarmManager.cancel(alarmIntent);
                    Toast.makeText(MainActivity.this, "Timer Stopped", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showTimePicker(TextView timeView) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        View view = View.inflate(MainActivity.this, R.layout.time_dialog, null);
        // Hours
        final NumberPicker numberPickerHour = view.findViewById(R.id.numpicker_hours);
        numberPickerHour.setMaxValue(23);
        numberPickerHour.setValue(sharedPreferences.getInt("Hours", 0));
        // Minutes
        final NumberPicker numberPickerMinutes = view.findViewById(R.id.numpicker_minutes);
        numberPickerMinutes.setMaxValue(59);
        numberPickerMinutes.setValue(sharedPreferences.getInt("Minutes", 1));
        // Seconds
        final NumberPicker numberPickerSeconds = view.findViewById(R.id.numpicker_seconds);
        numberPickerSeconds.setMaxValue(59);
        numberPickerSeconds.setValue(sharedPreferences.getInt("Seconds", 30));

        // Cancel and Ok button
        Button cancel = view.findViewById(R.id.cancel);
        Button ok = view.findViewById(R.id.ok);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {
                // Format interval (e.g., 0h 1m 2s -> 00:01:02)
                timeView.setText(String.format("%1$02d:%2$02d:%3$02d",
                        numberPickerHour.getValue(),
                        numberPickerMinutes.getValue(),
                        numberPickerSeconds.getValue())
                );
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("Hours", numberPickerHour.getValue());
                editor.putInt("Minutes", numberPickerMinutes.getValue());
                editor.putInt("Seconds", numberPickerSeconds.getValue());
                editor.apply();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "TIMER";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel("timerNotification", name, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}