package com.beginner.coronaviruscounter;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextPaint;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;

import static com.beginner.coronaviruscounter.Timer_Service.NOTIFY_INTERVAL;

public class ExeriseTimerActivity extends AppCompatActivity{

    TextView timer, pointsTextView,trackingLog ;
    Button start, pause, reset;
    ListView listView;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    Handler handler;
    int Seconds, Minutes, Hours, MilliSeconds ;

    boolean RedeemedPoints;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";
    private String exerciseLogText,pointsText;


    SharedPreferences mpref;
    SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exerisetimer_layout);

        timer = (TextView)findViewById(R.id.textView);
        start = (Button)findViewById(R.id.button);
        pause = (Button)findViewById(R.id.button2);
        reset = (Button)findViewById(R.id.button3);
        trackingLog=(TextView)findViewById(R.id.trackingLog);
        pointsTextView = (TextView)findViewById(R.id.pointsText);

        handler = new Handler();

        loadData();
        updateViews();
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StartTime = SystemClock.uptimeMillis();
                handler.postDelayed(runnable, 0);

                reset.setEnabled(false);
                start.setEnabled(false);

            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimeBuff += MillisecondTime;

                handler.removeCallbacks(runnable);

                reset.setEnabled(true);
                start.setEnabled(true);

            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MillisecondTime = 0L ;
                StartTime = 0L ;
                TimeBuff = 0L ;
                UpdateTime = 0L ;
                Seconds = 0 ;
                Minutes = 0 ;
                MilliSeconds = 0 ;

                timer.setText("00:00:00");
            }
        });

    }

    public Runnable runnable = new Runnable() {

        @SuppressLint({"SetTextI18n", "DefaultLocale"})
        public void run() {

            MillisecondTime = SystemClock.uptimeMillis() - StartTime;

            UpdateTime = TimeBuff + MillisecondTime;

            Seconds = (int) (UpdateTime / 1000);

            Hours = Minutes / 60;

            Minutes = Seconds / 60;

            Seconds = Seconds % 60;

            MilliSeconds = (int) (UpdateTime % 1000);

            timer.setText("" + String.format("%02d", Hours) + ":" + String.format("%02d", Minutes) + ":"
                    + String.format("%02d", Seconds));

            // + ":" + String.format("%03d", MilliSeconds)

            handler.postDelayed(this, 0);
        }

    };

    @SuppressLint("SetTextI18n")
    public void saveLap(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String hour = timer.getText().toString().split(":")[0];
            String minute = timer.getText().toString().split(":")[1];
            String second = timer.getText().toString().split(":")[2];
            DateTime dateTime = new DateTime();
            org.joda.time.LocalDate ld = new org.joda.time.LocalDate();
            if (!dateTime.toLocalDate().toString().equals(trackingLog.toString().split(" ")[0].replace("[", ""))) {
                if(!trackingLog.getText().toString().equals("")){
                    String secWorked, minWorked, hWorked;
                    secWorked = trackingLog.getText().toString().split(" ")[9];
                    minWorked = trackingLog.getText().toString().split(" ")[7];
                    hWorked = trackingLog.getText().toString().split(" ")[5];

                    Integer totalS, totalM, totalH;
                    totalS = Integer.parseInt(second)+Integer.parseInt(secWorked);
                    totalM = Integer.parseInt(minute)+Integer.parseInt(minWorked);
                    totalH = Integer.parseInt(hour)+Integer.parseInt(hWorked);

                    totalH = totalM/60;
                    totalM = totalS/60;
                    totalS = totalS%60;

                    trackingLog.setText(dateTime.toLocalDate() + " You have exercised for " + totalH.toString() + " hours " + totalM.toString() + " minutes " + totalS.toString() + " seconds");
                }
                else{
                    trackingLog.setText(dateTime.toLocalDate() + " You have exercised for " + hour + " hours " + minute + " minutes " + Integer.parseInt(second) + " seconds");
                }
                saveData();
                timer.setText("00:00:00");
                TimeBuff += MillisecondTime;
                handler.removeCallbacks(runnable);
                MillisecondTime = 0L ;
                StartTime = 0L ;
                TimeBuff = 0L ;
                UpdateTime = 0L ;
                Seconds = 0 ;
                Minutes = 0 ;
                MilliSeconds = 0 ;
                start.setEnabled(true);
            } /*else {
                String secWorked, minWorked, hWorked;
                secWorked = trackingLog.getText().toString().split(" ")[9];
                minWorked = trackingLog.getText().toString().split(" ")[7];
                hWorked = trackingLog.getText().toString().split(" ")[5];

                Integer totalS, totalM, totalH;
                totalS = Integer.parseInt(second)+Integer.parseInt(secWorked);
                totalM = Integer.parseInt(minute)+Integer.parseInt(minWorked);
                totalH = Integer.parseInt(hour)+Integer.parseInt(hWorked);

                trackingLog.setText(dateTime.toLocalDate() + " You have exercised for " + totalH.toString() + " hours " + totalM.toString() + " minutes " + totalS.toString() + " seconds");
                saveData();
            }*/

        }
    }
    public static final String POINT_KEY = "savePoint";
    public static final String RED_BOOL_KEY = "saveRedeemedBoolean";
    public static final String MILLI_KEY = "saveMillisecondLeft";

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        editor.putString(TEXT, trackingLog.getText().toString().replace("[,","").replace("]",""));
        editor.putBoolean(RED_BOOL_KEY,RedeemedPoints);
        editor.putString(POINT_KEY, pointsTextView.getText().toString());

        editor.apply();
    }
    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        exerciseLogText = sharedPreferences.getString(TEXT,"");
        RedeemedPoints = sharedPreferences.getBoolean(RED_BOOL_KEY,RedeemedPoints);
        pointsText = sharedPreferences.getString(POINT_KEY,pointsText);
    }
    public void updateViews(){
        exerciseLogText.replace("[","");
        trackingLog.setText(exerciseLogText.replace(",","\n").replace("[",""));
        if(pointsText!=null){
            pointsTextView.setText(pointsText);
        }
        else {
            pointsTextView.setText("Points: 0");
        }
    }

    public void popNoti(View view) {
        Toast.makeText(getApplicationContext(), "1 Point per Every 30 Minutes of Exercise", Toast.LENGTH_LONG).show();
    }

    protected void nextDay() {
        super.onResume();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        int lastTimeStarted = settings.getInt("last_time_started", -1);
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_YEAR);

        if (today != lastTimeStarted) {
            if(RedeemedPoints)
                RedeemedPoints = false;

            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("last_time_started", today);
            editor.commit();
        }
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void redeemPoints(View view) {
        try{
        Integer seconds, minutes,hours;
        seconds = Integer.parseInt(trackingLog.getText().toString().split(" ")[9]);
        minutes = Integer.parseInt(trackingLog.getText().toString().split(" ")[7]);
        hours = Integer.parseInt(trackingLog.getText().toString().split(" ")[5]);
        if(!RedeemedPoints&&seconds>=5){
            Integer totalPoints = ((seconds)+(minutes*60)+(hours*3600))/5;
            pointsTextView.setText("Points: "+totalPoints.toString());
            trackingLog.setText(new DateTime().toLocalDate() + " You have exercised for 0 hours 0 minutes "+((seconds)+(minutes*60)+(hours*3600))%5+" seconds");
            Toast.makeText(this,"You can only redeem points once a day",Toast.LENGTH_SHORT).show();
            RedeemedPoints = true;
            saveData();
            nextDay();
        }
        else if(RedeemedPoints){
            Toast.makeText(this,"Please wait Tomorrow to Redeem your points",Toast.LENGTH_SHORT).show();
        }

        if(seconds<5&&!RedeemedPoints){
            Toast.makeText(this,"More time needed to redeem",Toast.LENGTH_SHORT).show();
        }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this,"If this shows up, something is probably wrong and I am not fixing it",Toast.LENGTH_SHORT).show();
        }

    }

    public void startGiftCardActivity(View view) {
        Toast.makeText(this,"Redeem Points for Gift Card, Motivates Users to Stay Active at Home",Toast.LENGTH_SHORT).show();
    }
}
