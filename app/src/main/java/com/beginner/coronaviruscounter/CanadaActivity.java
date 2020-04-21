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
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.text.NumberFormat;
import java.util.Locale;

public class CanadaActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.canada_layout);

        if(isInternetWorking())
            getWebsite();
    }
    Elements newCasesTodayLink, lastUpdatedLink;

    private void getWebsite() {
        new Thread(new Runnable() {
            final TextView coronavirusCaseLabel = findViewById(R.id.coronavirusCaseLabel);
            final TextView deathCaseLabel = findViewById(R.id.deathCaseLabel);
            final TextView recoveredCaseLabel = findViewById(R.id.recoveredCaseLabel);
            final TextView activeCaseLabel = findViewById(R.id.activeCaseLabel);
            final TextView closedCaseLabel = findViewById(R.id.closedCaseLabel);
            final TextView lastUpdatedLabel = findViewById(R.id.lastUpdatedLabel);
            final TextView newCasesTotalLabel = findViewById(R.id.newCaseLabel);
            final StringBuilder totalCount = new StringBuilder();
            final StringBuilder deathCount = new StringBuilder();
            final StringBuilder recoveredCount = new StringBuilder();
            final StringBuilder activeCount = new StringBuilder();
            final StringBuilder closedCount = new StringBuilder();
            final StringBuilder mildPercent = new StringBuilder();
            final StringBuilder seriousPercent = new StringBuilder();
            final StringBuilder deathPercent = new StringBuilder();
            final StringBuilder recoveredPercent = new StringBuilder();
            final StringBuilder newCasesToday = new StringBuilder();
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect("https://www.ctvnews.ca/health/coronavirus/tracking-every-case-of-covid-19-in-canada-1.4852102").get();
                    //sb.append(doc.title()).append("\n");
                    Elements links = doc.select("#responsive_main > section > div > div > div:nth-child(2) > div.articleBody.election-col-11.election-col-s-12.election-col-m-12.election-col-l-10.election-col-xl-11.offset-right-col-l-1.offset-left-col-l-1.offset-left-col-xl-1.offset-left-col-1 > table.covid-province-table.cases-table > tbody:nth-child(3) > tr > td:nth-child(1)");
                    Elements deathLinks = doc.select("#responsive_main > section > div > div > div:nth-child(2) > div.articleBody.election-col-11.election-col-s-12.election-col-m-12.election-col-l-10.election-col-xl-11.offset-right-col-l-1.offset-left-col-l-1.offset-left-col-xl-1.offset-left-col-1 > table.covid-province-table.status-table > tbody:nth-child(3) > tr > td:nth-child(3)");
                    Elements recLink = doc.select("#responsive_main > section > div > div > div:nth-child(2) > div.articleBody.election-col-11.election-col-s-12.election-col-m-12.election-col-l-10.election-col-xl-11.offset-right-col-l-1.offset-left-col-l-1.offset-left-col-xl-1.offset-left-col-1 > table.covid-province-table.status-table > tbody:nth-child(3) > tr > td:nth-child(2)");
                    Elements activeLink = doc.select("#responsive_main > section > div > div > div:nth-child(2) > div.articleBody.election-col-11.election-col-s-12.election-col-m-12.election-col-l-10.election-col-xl-11.offset-right-col-l-1.offset-left-col-l-1.offset-left-col-xl-1.offset-left-col-1 > table.covid-province-table.status-table > tbody:nth-child(3) > tr > td:nth-child(1)");
                    newCasesTodayLink = doc.select("#responsive_main > section > div > div > div:nth-child(2) > div.articleBody.election-col-11.election-col-s-12.election-col-m-12.election-col-l-10.election-col-xl-11.offset-right-col-l-1.offset-left-col-l-1.offset-left-col-xl-1.offset-left-col-1 > table.covid-province-table.cases-table > tbody:nth-child(3) > tr > td:nth-child(2)");
                    lastUpdatedLink = doc.select("#top > div > div.content-wrapper > div.s-data > span:nth-child(7)");

                    totalCount.append(links.html().split("\n")[0]);
                    deathCount.append(deathLinks.html());
                    recoveredCount.append(recLink.html());
                    activeCount.append(activeLink.html());
                    seriousPercent.append(100-Integer.parseInt(mildPercent.toString()));
                    newCasesToday.append(newCasesTodayLink.html());
                    Double deathNumber = Double.parseDouble(deathCount.toString().replace(",",""));
                    Double closedNumber = Double.parseDouble(deathCount.toString().replace(",",""))+Double.parseDouble(recoveredCount.toString().replace(",",""));
                    /*deathPercent.append(Math.round((deathNumber/closedNumber)*100));
                    recoveredPercent.append(100-Math.round((deathNumber/closedNumber)*100));*/
                }catch (Exception e){
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Double deathNumber = Double.parseDouble(deathCount.toString().replace(",",""));
                            Double closedNumber = Double.parseDouble(deathCount.toString().replace(",",""))+Double.parseDouble(recoveredCount.toString().replace(",",""));
                            deathPercent.append(Math.round((deathNumber/closedNumber)*100));
                            recoveredPercent.append(100-Math.round((deathNumber/closedNumber)*100));
                            coronavirusCaseLabel.setText(totalCount.toString().split("\n")[0]);
                            deathCaseLabel.setText(deathCount.toString()+" ("+deathPercent.toString()+"%)");
                            recoveredCaseLabel.setText(recoveredCount.toString()+" ("+recoveredPercent.toString()+"%)");
                            activeCaseLabel.setText(activeCount.toString());
                            closedCaseLabel.setText(NumberFormat.getNumberInstance(Locale.US).format(closedNumber));
                            newCasesTotalLabel.setText(newCasesTodayLink.html());
                            lastUpdatedLabel.setText(lastUpdatedLink.html());
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
    public void updateCanadaWebsite(View view) {
        AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
        view.startAnimation(buttonClick);
        if(isInternetWorking())
            getWebsite();
    }

    public void launchProvinceDetail(View view) {
        startActivity(new Intent(this,ProvinceDetail.class));
    }
}
