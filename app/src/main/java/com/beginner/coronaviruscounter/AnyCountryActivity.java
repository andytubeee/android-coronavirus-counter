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
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.NumberFormat;
import java.util.Locale;

public class AnyCountryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anycountry_layoutt);

        if(isInternetWorking())
            getWebsite();
    }
    String countryName = "";
    private void getWebsite() {
        new Thread(new Runnable() {
            final TextView coronavirusCaseLabel = findViewById(R.id.coronavirusCaseLabel);
            final TextView deathCaseLabel = findViewById(R.id.deathCaseLabel);
            final TextView recoveredCaseLabel = findViewById(R.id.recoveredCaseLabel);
            final TextView activeCaseLabel = findViewById(R.id.activeCaseLabel);
            final TextView mildCaseLabel = findViewById(R.id.mildCaseLabel);
            final TextView seriousCaseLabel = findViewById(R.id.seriousCaseLabel);
            final TextView closedCaseLabel = findViewById(R.id.closedCaseLabel);
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
            Intent previousIntentMain = getIntent();
            @Override
            public void run() {
                try {
                    //String countryName = "";
                    switch (previousIntentMain.getStringExtra("theCountryName")){
                        case "America":
                            countryName = "us";
                            break;
                        case "usa":
                            countryName = "us";
                            break;
                        case "United States":
                            countryName = "us";
                            break;
                        case "south korea":
                            countryName = "south-korea";
                            break;
                        case "sk":
                            countryName = "south-korea";
                            break;
                        default:
                            countryName = previousIntentMain.getStringExtra("theCountryName");
                    }

                    Document doc = Jsoup.connect("https://www.worldometers.info/coronavirus/country/"+countryName+"/").get();
                    //sb.append(doc.title()).append("\n");
                    Elements links = doc.select("#maincounter-wrap > div > span");
                    Elements active_death_Link = doc.select("div[class=\"number-table-main\"]");
                    Elements mildLink = doc.select("span[class=\"number-table\"]");
                    Element percent = doc.select("strong").first();
                    Element seriousPercentHtml = doc.select("body > div:nth-child(11) > div:nth-child(2) > div.col-md-8 > div > div.row > div:nth-child(1) > div > div.panel-body > div > div.panel_front > div:nth-child(3) > div:nth-child(2) > strong").first();
                    Elements deathPercentHTML = doc.select("body > div:nth-child(11) > div:nth-child(2) > div.col-md-8 > div > div.row > div:nth-child(2) > div > div.panel-body > div > div.panel_front > div:nth-child(3) > div:nth-child(2) > strong");

                    totalCount.append(links.html().split("\n")[0]);
                    deathCount.append(links.html().split("\n")[1]);
                    recoveredCount.append(links.html().split("\n")[2]);
                    activeCount.append(active_death_Link.html().split("\n")[0]);
                    closedCount.append(active_death_Link.html().split("\n")[1]);
                    mildCount.append(mildLink.html().split("\n")[0]);
                    seriousCount.append(mildLink.html().split("\n")[1]);
                    mildPercent.append(percent.text());
                    seriousPercent.append(100-Integer.parseInt(mildPercent.toString()));
                    Double deathNumber = Double.parseDouble(deathCount.toString().replace(",",""));
                    Double closedNumber = Double.parseDouble(closedCount.toString().replace(",",""));
                    deathPercent.append(Math.round((deathNumber/closedNumber)*100));
                    recoveredPercent.append(100-Math.round((deathNumber/closedNumber)*100));

                    if(activeCaseLabel.getText().toString().matches("")){
                        Double totalNumber = Double.parseDouble(links.html().split("\n")[0].replaceAll("(\\d+),.*", "$1"));
                        Double dNum = Double.parseDouble(links.html().split("\n")[1].replaceAll("(\\d+),.*", "$1"));
                        Double rNum = Double.parseDouble(links.html().split("\n")[2].replaceAll("(\\d+),.*", "$1"));
                        activeCaseLabel.setText(Double.toString(totalNumber-(dNum+rNum)));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            coronavirusCaseLabel.setText(totalCount.toString());
                            deathCaseLabel.setText(deathCount.toString()+" ("+deathPercent.toString()+"%)");
                            recoveredCaseLabel.setText(recoveredCount.toString()+" ("+recoveredPercent.toString()+"%)");
                            activeCaseLabel.setText(activeCount.toString());
                            //NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(activeCount.toString())))
                            closedCaseLabel.setText(closedCount.toString());
                            mildCaseLabel.setText(" "+mildCount.toString()+"\n("+mildPercent.toString()+"%)");
                            seriousCaseLabel.setText("  "+seriousCount.toString()+"\n("+seriousPercent.toString()+"%)");

                            if(activeCaseLabel.getText().toString().matches("")){
                                Integer totalNumber = Integer.parseInt(totalCount.toString().replace(",",""));
                                Double dNum = Double.parseDouble(deathCount.toString().replace(",",""));
                                Double rNum = Double.parseDouble(recoveredCount.toString().replace(",",""));
                                activeCaseLabel.setText(NumberFormat.getNumberInstance(Locale.US).format(totalNumber-(dNum+rNum)));

                                mildCaseLabel.setText("Unknown");
                                seriousCaseLabel.setText("Unknown");
                            }
                            if(closedCaseLabel.getText().toString().matches("")){
                                Double dNum = Double.parseDouble(deathCount.toString().replace(",",""));
                                Double rNum = Double.parseDouble(recoveredCount.toString().replace(",",""));
                                closedCaseLabel.setText(NumberFormat.getNumberInstance(Locale.US).format(dNum+rNum));
                            }
                            if(countryName=="south-korea"){
                                Integer skDeathPercent = Math.round((Integer.parseInt(deathCount.toString().replace(",","")) / Integer.parseInt(closedCount.toString().replace(",","")))*100);
                                deathCaseLabel.setText(deathCount.toString()+" ("+(100-Integer.parseInt(recoveredPercent.toString()))+"%)");
                                recoveredCaseLabel.setText(recoveredCount.toString()+" ("+recoveredPercent.toString()+"%)");
                            }
                            if(countryName.equals("us")){
                                Integer USclosedCount = Integer.parseInt(deathCount.toString().replace(",",""))+Integer.parseInt(recoveredCount.toString().replace(",",""));
                                Double USdeathPercent = (Double.parseDouble(deathCount.toString().replace(",",""))/USclosedCount)*100;
                                deathCaseLabel.setText(deathCount.toString()+" ("+Math.round(USdeathPercent)+"%)");
                                recoveredCaseLabel.setText(recoveredCount.toString()+" ("+(100-Math.round(USdeathPercent))+"%)");
                                //activeCaseLabel.setText(String.valueOf(NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(activeCount.toString()))));
                            }
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

    public void updateWebsite(View view) {
        AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
        view.startAnimation(buttonClick);
        if(isInternetWorking())
            getWebsite();
    }
}
