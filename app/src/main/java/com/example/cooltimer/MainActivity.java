package com.example.cooltimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SeekBar seekBar;
    private TextView textView;
    private ImageView imageView;
    private boolean isTimerOn;
    private Button button;
    private CountDownTimer countDownTimer;
    private MediaPlayer mediaPlayer;
    private int defaultInterval;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = findViewById(R.id.seekBar);
        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        seekBar.setMax(600);
        isTimerOn = false;
        setIntervalFromSharedPreferences(sharedPreferences);

        button =findViewById(R.id.button);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                long progressInMillis = progress *1000;
                updateTimer(progressInMillis);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    public void start(View view) {

        if (!isTimerOn) {
            button.setText("Stop");
            seekBar.setEnabled(false);
            isTimerOn = true;

            countDownTimer = new CountDownTimer(seekBar.getProgress()*1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    updateTimer(millisUntilFinished);
                }

                @Override
                public void onFinish() {

                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    if(sharedPreferences.getBoolean("enable_sound", true)) {
                        String melodyName = sharedPreferences.getString("timer_melody", "USA");
                        if (melodyName.equals("USA")) {
                            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.din_din);
                        } else if (melodyName.equals("Germany")) {
                            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.din_din2);
                        } else if (melodyName.equals("Korea")) {
                            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.din_din3);
                        }
                        mediaPlayer.start();
                        imageView.setImageResource(R.drawable.haha);
                    }
                    resetTimer();
                }
            };
            countDownTimer.start();

        } else {
            resetTimer();
        }



    }

    private void updateTimer(long millisUntilFinished) {
        int minutes = (int) millisUntilFinished/60/1000;
        int seconds = (int) millisUntilFinished/1000 - (minutes * 60);

        String minutesString = "";
        String secondsString = "";

        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = String.valueOf(seconds);
        }
        if (minutes < 10) {
            minutesString = "0" + minutes;
        } else {
            minutesString = String.valueOf(minutes);
        }
        textView.setText(minutesString + ":" + secondsString);
    }

    private void  resetTimer() {
        countDownTimer.cancel();
        button.setText("Start");
        seekBar.setEnabled(true);
        isTimerOn = false;
        setIntervalFromSharedPreferences(sharedPreferences);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater =getMenuInflater();
        menuInflater.inflate(R.menu.timer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent openSettings = new Intent(this, SettingsActivity.class);
            startActivity(openSettings);
            return true;
        }else if (id == R.id.action_about) {
            Intent openSettings = new Intent(this, AboutActivity.class);
            startActivity(openSettings);
            return true;
        } else if (id == R.id.action_stop){
            mediaPlayer.pause();
            imageView.setImageResource(R.drawable.original);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setIntervalFromSharedPreferences(SharedPreferences sharedPreferences) {

        defaultInterval = Integer.valueOf(sharedPreferences.getString("default_interval", "30"));
        long defaultIntervalInMillis = defaultInterval * 1000;
        updateTimer(defaultIntervalInMillis);
        seekBar.setProgress(defaultInterval);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("default_interval")) {
            setIntervalFromSharedPreferences(sharedPreferences);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}
