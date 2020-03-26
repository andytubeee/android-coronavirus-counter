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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (isInternetWorking())
            getWebsite();
    }
    public void makeToast(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG).show();
    }

    public final boolean isInternetWorking()
    {
        ConnectivityManager connec = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // ARE WE CONNECTED TO THE NET
        if ( connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED )
        {
            // MESSAGE TO SCREEN FOR TESTING (IF REQ)
            //Toast.makeText(this, connectionType + ” connected”, Toast.LENGTH_SHORT).show();
            return true;
        }
        else if ( connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED
                ||  connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED  )
        {
            makeToast("Device not Connected to the Internet!");
            return false;
        }

        return false;
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
                final StringBuilder mildPercent = new StringBuilder();
                final StringBuilder seriousPercent = new StringBuilder();
                final StringBuilder deathPercentSB = new StringBuilder();
                final StringBuilder recoveredPercent = new StringBuilder();
                final StringBuilder closedNumberString = new StringBuilder();
                final StringBuilder deathNumberString = new StringBuilder();
                Double deathPercent = null;
                try {
                    Document doc = Jsoup.connect("https://www.worldometers.info/coronavirus/").get();
                    //sb.append(doc.title()).append("\n");
                    Elements links = doc.select("span[style=\"color:#aaa\"]");
                    Elements activeHTML = doc.select("div[class=\"number-table-main\"]");
                    Elements DeathLinks = doc.select("#maincounter-wrap > div > span");

                    sb.append(links.html().toString());
                    deathSB.append(DeathLinks.html().toString());
                    closedNumberString.append(activeHTML.html().split("\n")[1]);
                    Double deathNumber = Double.parseDouble(deathSB.toString().split("\n")[1].replaceAll("(\\d+),.*", "$1"));
                    Double closedNumber = Double.parseDouble(closedNumberString.toString().replaceAll("(\\d+),.*", "$1"));
                    deathPercent = (deathNumber/closedNumber)*100;
                    deathPercentSB.append(deathPercent.toString());
                }catch (Exception e){
                    e.printStackTrace();
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

                            String[] allValues = deathSB.toString().split("\n");
                            /*String[] arrOfStr = allValues.split("\n", 2);
                            String[] deathArr = null;
                            for (String a : arrOfStr)
                                //DeathLabel.setText(a);
                                deathArr = a.split("\n");
                                if (!DeathLabel.getText().toString().equals(deathArr[0])){
                                    numberChange = true;
                                    DeathLabel.setText(deathArr[0]+"("+deathPercent.toString()+"%)");
                                }
                                if (!RecoveredLabel.getText().toString().equals(deathArr[1])){
                                    numberChange = true;
                                    RecoveredLabel.setText(deathArr[1]);
                                }*/


                            if (!DeathLabel.getText().toString().equals(allValues[1])){
                                numberChange = true;
                                DeathLabel.setText(allValues[1]+" ("+Math.round(Double.parseDouble(deathPercentSB.toString()))+"%)");
                                //"("+Math.round((Double.parseDouble(allValues[1])/Double.parseDouble(closedNumberString.toString()))*100)+"%)"
                            }

                            if (!RecoveredLabel.getText().toString().equals(allValues[2])){
                                numberChange = true;
                                RecoveredLabel.setText(allValues[2] + " (" +(100-Math.round(Double.parseDouble(deathPercentSB.toString())))+"%)");
                            }
                            CheckBox cbNotification = (CheckBox) findViewById(R.id.notificationCheckBox);
                            if(numberChange&&cbNotification.isChecked()){
                                showNotification("Update Notice","Updated!");
                            }



                            Integer tempInt = Integer.parseInt(debugLbl.getText().toString());
                            debugLbl.setText((tempInt+=1).toString());
                        }catch (Exception ex){
                            sb.append("Error: "+ex.getMessage()).append("\n");
                            ex.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    public void updateWebsite(View view) {
        view.startAnimation(buttonClick);
        if(isInternetWorking())
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

    public void switchCanadaActivity(View view) {
        startActivity(new Intent(this, CanadaActivity.class));
    }
}