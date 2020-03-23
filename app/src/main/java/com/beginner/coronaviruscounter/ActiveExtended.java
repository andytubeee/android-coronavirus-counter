package com.beginner.coronaviruscounter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class ActiveExtended extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activecaseextended_layout);
        getWebsite();
    }

    private void getWebsite() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final TextView mildLabel = (TextView) findViewById(R.id.mildLabel);
                final TextView seriousLabel = (TextView) findViewById(R.id.seriousLabel);
                final StringBuilder activeExtendedCaseCount = new StringBuilder();
                final StringBuilder percent = new StringBuilder();
                final StringBuilder percentS = new StringBuilder();
                try {
                    Document doc = Jsoup.connect("https://www.worldometers.info/coronavirus/").get();
                    Elements activeExtendedHTML = doc.select("span[class=\"number-table\"]");
                    Elements percentHTML = doc.select("body > div.container > div:nth-child(2) > div.col-md-8 > div > div:nth-child(14) > div > div.panel-body > div > div.panel_front > div:nth-child(3) > div:nth-child(1) > strong");
                    Elements percentSHTML = doc.select("body > div.container > div:nth-child(2) > div.col-md-8 > div > div:nth-child(14) > div > div.panel-body > div > div.panel_front > div:nth-child(3) > div:nth-child(2) > strong");
                    activeExtendedCaseCount.append(activeExtendedHTML.html().toString());
                    percent.append(percentHTML.html().toString());
                    percentS.append(percentSHTML.html().toString());
                }catch (Exception e){
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            String[] countArr = activeExtendedCaseCount.toString().split("\n");
                            mildLabel.setText(countArr[0]+"\n  ("+percent.toString()+"%)");
                            seriousLabel.setText(countArr[1]+"\n  ("+percentS.toString()+"%)");
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    public void takeBack(View view) {
        startActivity(new Intent(this,TotalExtended.class));
    }
}
