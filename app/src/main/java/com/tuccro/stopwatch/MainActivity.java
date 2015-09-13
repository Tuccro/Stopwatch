package com.tuccro.stopwatch;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private int timerAccuracy = 20;

    TextView tvTimer;
    Button btStart;
    NumberPicker npAccuracy;

    TimerTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    private void initViews() {

        tvTimer = (TextView) findViewById(R.id.tv_timer);
        btStart = (Button) findViewById(R.id.bt_start);

        npAccuracy = (NumberPicker) findViewById(R.id.number_picker);
        npAccuracy.setMinValue(5);
        npAccuracy.setMaxValue(300);

        btStart.setOnClickListener(onClickListener);
    }

    void stopTimer() {
        task.setRun(false);
        task = null;
        btStart.setText(getResources().getString(R.string.start));
    }

    void startTimer() {
        task = new TimerTask();
        task.execute();
        btStart.setText(getResources().getString(R.string.stop));
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (task == null || task.isCancelled()) {

                timerAccuracy = npAccuracy.getValue();
                npAccuracy.setEnabled(false);

                startTimer();
            } else {

                if (task.getStatus() == AsyncTask.Status.RUNNING) {

                    stopTimer();
                    npAccuracy.setEnabled(true);
                }
            }
        }
    };

    class TimerTask extends AsyncTask {

        private static final long MILLIS_IN_HOUR = 3600000;
        private static final long MILLIS_IN_MINUTE = 60000;
        private static final long MILLIS_IN_SECOND = 1000;

        boolean run;
        long timeFromStart = 0;

        protected void setRun(boolean run) {
            this.run = run;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            run = true;
        }

        @Override
        protected Object doInBackground(Object[] params) {

            long startTime = System.currentTimeMillis();

            while (run) {
                try {
                    Thread.sleep(timerAccuracy);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                timeFromStart = System.currentTimeMillis() - startTime;
                publishProgress();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);

            tvTimer.setText(getFormattedTimeString());
        }

        private String getFormattedTimeString() {

            long time = timeFromStart;

            long hours = time / MILLIS_IN_HOUR;
            time = time % MILLIS_IN_HOUR;

            long minutes = time / MILLIS_IN_MINUTE;
            time = time % MILLIS_IN_MINUTE;

            long seconds = time / MILLIS_IN_SECOND;
            time = time % MILLIS_IN_SECOND;

            String sHours = (hours == 0) ? "00" :
                    (hours < 10) ? "0" + String.valueOf(hours) : String.valueOf(hours);

            String sMinutes = (minutes == 0) ? "00" :
                    (minutes < 10) ? "0" + String.valueOf(minutes) : String.valueOf(minutes);

            String sSeconds = (seconds == 0) ? "00" :
                    (seconds < 10) ? "0" + String.valueOf(seconds) : String.valueOf(seconds);

            String sMilliseconds = (time == 0) ? "000" :
                    (time < 10) ? "00" + String.valueOf(time) :
                            (time < 100) ? "0" + String.valueOf(time) : String.valueOf(time);

            return sHours + ":" + sMinutes + ":" + sSeconds + ":" + sMilliseconds;
        }
    }
}
