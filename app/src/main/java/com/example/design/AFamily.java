package com.example.design;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AFamily extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afamily);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AFamily.this,NavigationDrawer.class));
        super.onBackPressed();
    }
}
