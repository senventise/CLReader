package com.senventise.clreader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends AppCompatActivity {

    SwitchCompat switchGfw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences pref = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isNightMode = pref.getBoolean("night",true);
        if (isNightMode){
            setTheme(R.style.AppThemeNight);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        init();
        switchGfw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = getSharedPreferences("settings", MODE_PRIVATE).edit();
                editor.putBoolean("gfw", b);
                editor.apply();
            }
        });
        setTitle("设置");
    }

    public void onAboutClick(View view) {
        Intent i = new Intent(this,AboutActivity.class);
        startActivity(i);
    }

    public void init(){
        switchGfw = findViewById(R.id.switch_gfw);
        SharedPreferences pref = getSharedPreferences("settings", MODE_PRIVATE);
        switchGfw.setChecked(pref.getBoolean("gfw", false));
    }
}