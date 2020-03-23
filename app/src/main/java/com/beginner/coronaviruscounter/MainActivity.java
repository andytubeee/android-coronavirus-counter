package com.beginner.coronaviruscounter;

import androidx.appcompat.app.AppCompatActivity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
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
                            totalCaseLabel.setText(sb.toString());

                            String allValues = deathSB.toString();
                            String[] arrOfStr = allValues.split("\n", 2);
                            String[] deathArr = null;
                            for (String a : arrOfStr)
                                //DeathLabel.setText(a);
                                deathArr = a.split("\n");
                            DeathLabel.setText(deathArr[0]);
                            RecoveredLabel.setText(deathArr[1]);
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

}