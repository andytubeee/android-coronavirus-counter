package com.beginner.coronaviruscounter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Bundle;
import android.text.method.DateTimeKeyListener;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import org.joda.time.DateTime;

public class ExeriseTimerActivity extends AppCompatActivity {

    TextView timer ;
    Button start, pause, reset;
    ListView listView;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    Handler handler;
    int Seconds, Minutes, Hours, MilliSeconds ;
    String[] ListElements = new String[] {  };
    List<String> ListElementsArrayList;
    ArrayAdapter<String> adapter;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";
    private String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exerisetimer_layout);

        timer = (TextView)findViewById(R.id.textView);
        start = (Button)findViewById(R.id.button);
        pause = (Button)findViewById(R.id.button2);
        reset = (Button)findViewById(R.id.button3);
        listView = (ListView)findViewById(R.id.listview1);

        handler = new Handler() ;

        ListElementsArrayList = new ArrayList<String>(Arrays.asList(ListElements));

        adapter = new ArrayAdapter<String>(ExeriseTimerActivity.this,
                R.layout.list_white_text,
                ListElementsArrayList
        );
        listView.setAdapter(adapter);

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

                ListElementsArrayList.clear();

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

    public void saveLap(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String hour = timer.getText().toString().split(":")[0];
            String minute = timer.getText().toString().split(":")[1];
            String second = timer.getText().toString().split(":")[2];
            DateTime dateTime = new DateTime();
            org.joda.time.LocalDate ld = new org.joda.time.LocalDate();
            if ((dateTime.toLocalDate()).toString().equals(ListElementsArrayList.toString().split(" ")[0].replace("[",""))){
                ListElementsArrayList.clear();
                ListElementsArrayList.add(dateTime.toLocalDate()+" You have exercised for "+hour+" hours "+minute+" minutes "+second+" seconds");
                saveData();
            }
            else {
                ListElementsArrayList.add(dateTime.toLocalDate()+" You have exercised for "+hour+" hours "+minute+" minutes "+second+" seconds");
                saveData();
            }

        }

        adapter.notifyDataSetChanged();
    }

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(TEXT, ListElementsArrayList.toString().replace("[,","").replace("]",""));
        editor.apply();

        //Toast.makeText(getApplicationContext(),"Data saved", Toast.LENGTH_LONG).show();
    }
    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        text = sharedPreferences.getString(TEXT,"");
    }
    public void updateViews(){
        text.replace("[","");
        ListElementsArrayList.add(text.replace(",","\n").replace("[",""));
    }
}