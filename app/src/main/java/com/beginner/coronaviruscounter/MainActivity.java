package com.beginner.coronaviruscounter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWebsite();
    }

    public void getWebsite(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final TextView totalCaseLabel = (TextView) findViewById(R.id.totalCountLabel);
                final TextView DeathLabel = (TextView) findViewById(R.id.deathsLabel);
                final CheckBox cbNotification = (CheckBox) findViewById(R.id.notificationCheckBox);
                final TextView RecoveredLabel = (TextView) findViewById(R.id.recoveredLabel);
                final StringBuilder sb = new StringBuilder();
                final StringBuilder deathSB = new StringBuilder();
                try {
                    Document doc = Jsoup.connect("https://www.worldometers.info/coronavirus/").get();
                    //sb.append(doc.title()).append("\n");
                    Elements links = doc.select("span[style=\"color:#aaa\"]");
                    Elements DeathLinks = doc.select("#maincounter-wrap > div > span");

                    sb.append(links.html().toString());
                    deathSB.append(DeathLinks.html().toString());
                    /*for (Element link : links){
                        sb.append(link.attr("span"));
                    }*/

                }catch (Exception e){
                    e.printStackTrace();
                    sb.append("Error: "+e.getMessage()).append("\n");
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            final TextView debugLbl = (TextView) findViewById(R.id.debugLabel);
                            Boolean numberChange = false;

                            if(!totalCaseLabel.getText().toString().equals(sb.toString())) {
                                numberChange = true;
                                totalCaseLabel.setText(sb.toString());
                            }

                            String allValues = deathSB.toString();
                            String[] arrOfStr = allValues.split("\n", 2);
                            String[] deathArr = null;
                            for (String a : arrOfStr)
                                //DeathLabel.setText(a);
                                deathArr = a.split("\n");
                                if (!DeathLabel.getText().toString().equals(deathArr[0])){
                                    numberChange = true;
                                    DeathLabel.setText(deathArr[0]);
                                }
                                if (!RecoveredLabel.getText().toString().equals(deathArr[1])){
                                    numberChange = true;
                                    RecoveredLabel.setText(deathArr[1]);
                                }
                                CheckBox cbNotification = (CheckBox) findViewById(R.id.notificationCheckBox);
                                if(numberChange&&cbNotification.isChecked()){
                                    showNotification("Update Notice","Updated!");
                                }
                            Integer tempInt = Integer.parseInt(debugLbl.getText().toString());
                            debugLbl.setText((tempInt+=1).toString());


                        }catch (Exception ex){
                            finish();
                            System.exit(0);
                        }
                    }
                });
            }
        }).start();
    }

    public void updateWebsite(View view) {
        view.startAnimation(buttonClick);
        getWebsite();
    }

    void showNotification(String title, String message) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("YOUR_CHANNEL_ID",
                    "YOUR_CHANNEL_NAME",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DISCRIPTION");
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "YOUR_CHANNEL_ID")
                .setSmallIcon(R.drawable.cvbgicon) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText(message)// message for notification
                .setAutoCancel(true); // clear notification after click
        mBuilder.setStyle(new
                NotificationCompat.BigTextStyle().bigText(message));
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(0, mBuilder.build());
    }

    public void turnOnNotification(View view) {
        final TextView totalCaseLabel = (TextView) findViewById(R.id.totalCountLabel);
        final TextView DeathLabel = (TextView) findViewById(R.id.deathsLabel);
        final CheckBox cbNotification = (CheckBox) findViewById(R.id.notificationCheckBox);
        final TextView RecoveredLabel = (TextView) findViewById(R.id.recoveredLabel);
            //if (cbNotification.isChecked()){
                //showNotification("Coronavirus Count","Total Count: "+totalCaseLabel.getText().toString()+"\nDeaths: "+DeathLabel.getText().toString()+"\nRecovered: "+RecoveredLabel.getText().toString());
        }

    public void autoUpdate(View view) {
        CheckBox autoUpdateCB = (CheckBox) findViewById(R.id.AutoUpdateCheckbox);
        if (autoUpdateCB.isChecked()){
            Thread thread = new Thread("Auto Update") {
                public void run() {
                    while(true){
                        try {
                            sleep(5000);
                            try{
                                getWebsite();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            thread.start();
        }
    }

    public void changeActivity(View view) {
        Intent intent = new Intent(this, TotalExtended.class);
        startActivity(intent);
    }
}