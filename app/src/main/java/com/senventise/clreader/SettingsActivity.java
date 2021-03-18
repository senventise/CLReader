package com.senventise.clreader;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class SettingsActivity extends AppCompatActivity {

    SwitchCompat switchGfw, switchPass;
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
        switchPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton compoundButton, boolean b) {
                if (b) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(SettingsActivity.this);
                    alert.setTitle(getResources().getString(R.string.set_pass));
                    final EditText input = new EditText(SettingsActivity.this);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    input.setRawInputType(Configuration.KEYBOARD_12KEY);
                    alert.setView(input);
                    alert.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // 设置密码
                            if (input.getText().toString().length() == 4) {
                                SharedPreferences.Editor editor = getSharedPreferences("settings", MODE_PRIVATE).edit();
                                editor.putString("passcode", input.getText().toString());
                                editor.apply();
                            } else {
                                compoundButton.setChecked(false);
                                Toast.makeText(SettingsActivity.this, "密码长度错误，已取消", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // 取消设置密码
                            compoundButton.setChecked(false);
                        }
                    });
                    alert.show();
                }else {
                    SharedPreferences.Editor editor = getSharedPreferences("settings", MODE_PRIVATE).edit();
                    editor.putString("passcode", "NOT_SET");
                    editor.apply();
                }
            }
        });
        setTitle("设置");
    }

    public void onAboutClick(View view) {
        Intent i = new Intent(this, AboutActivity.class);
        startActivity(i);
    }

    public void init(){
        switchGfw = findViewById(R.id.switch_gfw);
        switchPass = findViewById(R.id.switch_pass);
        SharedPreferences pref = getSharedPreferences("settings", MODE_PRIVATE);
        switchGfw.setChecked(pref.getBoolean("gfw", false));
        if (!pref.getString("passcode", "NOT_SET").equals("NOT_SET")) {
            switchPass.setChecked(true);
        }
    }
}