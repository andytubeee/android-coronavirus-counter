package com.beginner.coronaviruscounter;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class CanadaActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.canada_layout);

        if(isInternetWorking())
            getWebsite();
    }

    private void getWebsite() {
        new Thread(new Runnable() {
            final TextView coronavirusCaseLabel = (TextView) findViewById(R.id.coronavirusCaseLabel);
            final TextView deathCaseLabel = (TextView) findViewById(R.id.deathCaseLabel);
            final TextView recoveredCaseLabel = (TextView) findViewById(R.id.recoveredCaseLabel);
            final TextView activeCaseLabel = (TextView) findViewById(R.id.activeCaseLabel);
            final TextView mildCaseLabel = (TextView) findViewById(R.id.mildCaseLabel);
            final TextView seriousCaseLabel = (TextView) findViewById(R.id.seriousCaseLabel);
            final TextView closedCaseLabel = (TextView) findViewById(R.id.closedCaseLabel);
            final StringBuilder totalCount = new StringBuilder();
            final StringBuilder deathCount = new StringBuilder();
            final StringBuilder recoveredCount = new StringBuilder();
            final StringBuilder activeCount = new StringBuilder();
            final StringBuilder mildCount = new StringBuilder();
            final StringBuilder seriousCount = new StringBuilder();
            final StringBuilder closedCount = new StringBuilder();
            final StringBuilder mildPercent = new StringBuilder();
            final StringBuilder seriousPercent = new StringBuilder();
            final StringBuilder deathPercent = new StringBuilder();
            final StringBuilder recoveredPercent = new StringBuilder();
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect("https://www.worldometers.info/coronavirus/country/canada/").get();
                    //sb.append(doc.title()).append("\n");
                    Elements links = doc.select("#maincounter-wrap > div > span");
                    Elements active_death_Link = doc.select("div[class=\"number-table-main\"]");
                    Elements mildLink = doc.select("span[class=\"number-table\"]");
                    Element percent = doc.select("strong").first();
                    Element seriousPercentHtml = doc.select("body > div:nth-child(11) > div:nth-child(2) > div.col-md-8 > div > div.row > div:nth-child(1) > div > div.panel-body > div > div.panel_front > div:nth-child(3) > div:nth-child(2) > strong").first();
                    Elements deathPercentHTML = doc.select("body > div:nth-child(11) > div:nth-child(2) > div.col-md-8 > div > div.row > div:nth-child(2) > div > div.panel-body > div > div.panel_front > div:nth-child(3) > div:nth-child(2) > strong");

                    totalCount.append(links.html().toString().split("\n")[0]);
                    deathCount.append(links.html().toString().split("\n")[1]);
                    recoveredCount.append(links.html().toString().split("\n")[2]);
                    activeCount.append(active_death_Link.html().toString().split("\n")[0]);
                    closedCount.append(active_death_Link.html().toString().split("\n")[1]);
                    mildCount.append(mildLink.html().toString().split("\n")[0]);
                    seriousCount.append(mildLink.html().toString().split("\n")[1]);
                    mildPercent.append(percent.text());
                    seriousPercent.append(100-Integer.parseInt(mildPercent.toString()));
                    Double deathNumber = Double.parseDouble(deathCount.toString());
                    Double closedNumber = Double.parseDouble(closedCount.toString());
                    deathPercent.append(Math.round((deathNumber/closedNumber)*100));
                    recoveredPercent.append(100-Math.round((deathNumber/closedNumber)*100));
                }catch (Exception e){
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            coronavirusCaseLabel.setText(totalCount.toString());
                            deathCaseLabel.setText(deathCount.toString()+"("+deathPercent.toString()+"%)");
                            recoveredCaseLabel.setText(recoveredCount.toString()+"("+recoveredPercent.toString()+"%)");
                            activeCaseLabel.setText(activeCount.toString());
                            closedCaseLabel.setText(closedCount.toString());
                            mildCaseLabel.setText(" "+mildCount.toString()+"\n("+mildPercent.toString()+"%)");
                            seriousCaseLabel.setText("  "+seriousCount.toString()+"\n("+seriousPercent.toString()+"%)");

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
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

    public void showNotification(String title, String message) {
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

    public void returnMainActivity(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }
}
