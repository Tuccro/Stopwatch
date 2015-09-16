package com.tuccro.stopwatch;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static final String SAVED_START_TIME = "saved_start_time";
    public static final String SAVED_TIME = "saved_time";
    public static final String SAVED_ACCURACY = "saved_accuracy";

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

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        npAccuracy.setValue(savedInstanceState.getInt(SAVED_ACCURACY));

        if (savedInstanceState.containsKey(SAVED_START_TIME)) {

            startTimer(savedInstanceState.getLong(SAVED_START_TIME), npAccuracy.getValue());
        } else tvTimer.setText(savedInstanceState.getString(SAVED_TIME));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (task != null
                && task.getStatus() == AsyncTask.Status.RUNNING) {

            outState.putLong(SAVED_START_TIME, task.getStartTime());
            stopTimer();
        } else outState.putString(SAVED_TIME, tvTimer.getText().toString());

        outState.putInt(SAVED_ACCURACY, npAccuracy.getValue());
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

    void startTimer(long startTime, int accuracy) {
        task = new TimerTask(startTime, accuracy);
        task.execute();
        btStart.setText(getResources().getString(R.string.stop));
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (task == null || task.isCancelled()) {

                npAccuracy.setEnabled(false);

                startTimer(System.currentTimeMillis(), npAccuracy.getValue());
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
        long timeFromStart;
        long startTime;

        private int timerAccuracy;

        public long getStartTime() {
            return startTime;
        }

        public TimerTask(long startTime, int accuracy) {
            this.startTime = startTime;
            this.timerAccuracy = accuracy;
        }

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
