package com.beginner.coronaviruscounter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ProtectionActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.protectioninfo_layout);
    }

    public void returnMainActivity(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }
}
