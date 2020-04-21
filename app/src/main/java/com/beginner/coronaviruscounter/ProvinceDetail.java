package com.beginner.coronaviruscounter;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class ProvinceDetail extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.provincedetail_layout);

        if(!isInternetWorking()){
            makeToast("No Internet Connection!");
        }
    }

    //Variables

   /* private void getWebsite(final String prov) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    doc = Jsoup.connect("https://www.ctvnews.ca/health/coronavirus/tracking-every-case-of-covid-19-in-canada-1.4852102").get();
                    switch (prov){
                        case "British Columbia":
                            totalCaseL = doc.select("#responsive_main > section > div > div > div:nth-child(2) > div.articleBody.election-col-11.election-col-s-12.election-col-m-12.election-col-l-10.election-col-xl-11.offset-right-col-l-1.offset-left-col-l-1.offset-left-col-xl-1.offset-left-col-1 > div:nth-child(21) > div.covid-table-charts > div.tb1 > table > tbody:nth-child(3) > tr > td:nth-child(1)");
                            totalCaseString = totalCaseL.html();
                            break;
                    }

                }catch (Exception ex){
                    ex.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            switch (prov){
                                case "British Columbia":
                                    //makeToast(totalCaseL.html());
                                    break;
                            }
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }
*/

    String totalCaseString;
    Elements totalCaseL;
    Document doc;
    public void displayBC(View view) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    doc = Jsoup.connect("https://www.canada.ca/en/public-health/services/diseases/2019-novel-coronavirus-infection.html#a1").get();
                    totalCaseL = doc.select("#g59 > g > text");
                    totalCaseString = totalCaseL.html();
                }catch (Exception ex){
                    makeToast("IDK WTF HAPPEND!");
                    ex.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                           makeToast(totalCaseL.toString());
                        }catch (Exception ex){
                            makeToast("Something happened and I do not care");
                            ex.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }


    //Other Functions
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

    public void returnActivity(View view) {
        startActivity(new Intent(this,CanadaActivity.class));
    }
}
