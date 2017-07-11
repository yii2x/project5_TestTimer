package com.murach.ch10_ex5;

import java.util.Timer;
import java.util.TimerTask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private RSSFeed feed;
    private FileIO io;

    private TextView messageTextView; 
    private Timer timer;

    private Button startStopTimerBtn;
    private Boolean timerRunning = false;

    private int downloadCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        io = new FileIO(getApplicationContext());
        messageTextView = (TextView) findViewById(R.id.messageTextView);


        startStopTimerBtn = (Button) findViewById(R.id.startStopTimerBtn);

        startStopTimerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timerRunning == true){
                    stopTimer();
                }
                else{
                    startTimer();
                }
            }
        });
        startTimer();
    }
    private void stopTimer(){
        timer.cancel();
        timerRunning = false;
        toggleStartStopButton();
    }
    private void toggleStartStopButton(){
        if(timerRunning == true){
            startStopTimerBtn.setText("Stop");
        }
        else{
            startStopTimerBtn.setText("Start");
        }
    }

    private void startTimer() {

        timerRunning = true;

        final long startMillis = System.currentTimeMillis();
        timer = new Timer(true);
        TimerTask task = new TimerTask() {
            
            @Override
            public void run() {
                long elapsedMillis = System.currentTimeMillis() - startMillis;
                new MainActivity.DownloadFeed().execute();
                updateView(elapsedMillis);
            }
        };
        timer.schedule(task, 0, 10000);
        toggleStartStopButton();
    }

    private void updateView(final long elapsedMillis) {
        // UI changes need to be run on the UI thread
        messageTextView.post(new Runnable() {

            int elapsedSeconds = (int) elapsedMillis/1000;

            @Override
            public void run() {
                downloadCount++;
                messageTextView.setText("File downloaded "+downloadCount+" time(s): ");

            }
        });
    }

    @Override
    protected void onPause() {

            timer.purge();
            timer.cancel();



        super.onPause();
    }

    class DownloadFeed extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            io.downloadFile();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d("News reader", "Feed downloaded");
        }
    }


}