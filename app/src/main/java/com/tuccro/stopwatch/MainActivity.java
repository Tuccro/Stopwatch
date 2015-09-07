package com.tuccro.stopwatch;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView tvTimer;
    Button btStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTimer = (TextView) findViewById(R.id.tv_timer);
        btStart = (Button) findViewById(R.id.bt_start);

        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimerTask task = new TimerTask();
                task.execute();
            }
        });
    }

    class TimerTask extends AsyncTask {

        boolean run = true;
        long timeFromStart = 0;

        @Override
        protected Object doInBackground(Object[] params) {

            long startTime = System.currentTimeMillis();
            long lastTime = startTime;
            long timeDifference;

            while (run) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                timeDifference = System.currentTimeMillis() - lastTime;

                if (timeDifference > 500) {
                    lastTime = System.currentTimeMillis();
                    timeFromStart = System.currentTimeMillis() - startTime;
                    publishProgress();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);

            tvTimer.setText(getFormattedTime());
        }

        static final long MILLIS_IN_HOUR = 3600000;
        static final long MILLIS_IN_MINUTE = 60000;
        static final long MILLIS_IN_SECOND = 1000;

        String getFormattedTime() {

            long time = timeFromStart;

            long hours = time / MILLIS_IN_HOUR;
            time = time % MILLIS_IN_HOUR;

            long minutes = time / MILLIS_IN_MINUTE;
            time = time % MILLIS_IN_MINUTE;

            long seconds = time / MILLIS_IN_SECOND;

            String sHours = (hours == 0) ? "00" : (hours < 10) ? "0" + String.valueOf(hours) : String.valueOf(hours);
            String sMinutes = (minutes == 0) ? "00" : (minutes < 10) ? "0" + String.valueOf(minutes) : String.valueOf(minutes);
            String sSeconds = (seconds == 0) ? "00" : (seconds < 10) ? "0" + String.valueOf(seconds) : String.valueOf(seconds);

            return sHours + ":" + sMinutes + ":" + sSeconds;
        }
    }
}
