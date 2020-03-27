package com.beginner.coronaviruscounter;

import android.content.Intent;
import android.os.Bundle;

import org.jsoup.select.Elements;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TotalExtended extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.totalcaseextend_layout);
        getWebsite();
    }

    private void getWebsite() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final TextView activeCaseLabel = findViewById(R.id.activeCaseLabel);
                final TextView closedCaseLabel = findViewById(R.id.closedCaseLabel);
                final StringBuilder activeCaseCount = new StringBuilder();

                try {
                    Document doc = Jsoup.connect("https://www.worldometers.info/coronavirus/").get();
                    Elements activeHTML = doc.select("div[class=\"number-table-main\"]");
                    activeCaseCount.append(activeHTML.html());
                }catch (Exception e){
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            String[] countArr = activeCaseCount.toString().split("\n");
                            activeCaseLabel.setText(countArr[0]);
                            closedCaseLabel.setText(countArr[1]);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    public void takeBack(View view) {
        startActivity(new Intent(this,MainActivity.class));
    }

    public void switchActiveExtended(View view) {
        startActivity(new Intent(this,ActiveExtended.class));
    }
}
