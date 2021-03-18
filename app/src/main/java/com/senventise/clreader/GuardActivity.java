package com.senventise.clreader;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import com.hanks.passcodeview.PasscodeView;

public class GuardActivity extends AppCompatActivity {

    PasscodeView passcodeView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guard);
        SharedPreferences pref = getSharedPreferences("settings", MODE_PRIVATE);
        // 未设置密码
        if (pref.getString("passcode", "NOT_SET").equals("NOT_SET")){
            Intent i = new Intent(MyApplication.getInstance(), SplashActivity.class);
            startActivity(i);
        }
        // 隐藏状态栏及 Actionbar
        if (this.getSupportActionBar() != null){
            this.getSupportActionBar().hide();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        passcodeView = findViewById(R.id.passcodeView);
        passcodeView.setPasscodeLength(4);
        if (!pref.getString("passcode", "NOT_SET").equals("NOT_SET")) {
            passcodeView.setLocalPasscode(pref.getString("passcode", "NOT_SET"));
        }
        passcodeView.setListener(new PasscodeView.PasscodeViewListener() {
            @Override
            public void onFail() {

            }

            @Override
            public void onSuccess(String number) {
                Intent i = new Intent(MyApplication.getInstance(), SplashActivity.class);
                startActivity(i);
            }
        });
    }


}