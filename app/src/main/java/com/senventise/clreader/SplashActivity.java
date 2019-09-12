package com.senventise.clreader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SplashActivity extends AppCompatActivity {

    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 是否为夜间模式
        SharedPreferences pref = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isNightMode = pref.getBoolean("night",true);
        if (isNightMode){
            setTheme(R.style.AppThemeNight);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init();
        new Thread(networkTask).start();
    }

    private void init(){
        textView = findViewById(R.id.splash_url_test);
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            Bundle data = msg.getData();
            boolean isValid = data.getBoolean("isValid");
            if (isValid){
                textView.setText("网址有效，可以连接。");
                jumpToMain();
            }else {
                textView.setText("无法连接，请尝试使用代理。");
            }
        }
    };



    Runnable networkTask = new Runnable() {
        @Override
        public void run() {
            Message msg = new Message();
            Bundle data = new Bundle();
            try{
                UrlValid.run();
                data.putBoolean("isValid",true);
                msg.setData(data);
            }catch (IOException e){
                e.printStackTrace();
                data.putBoolean("isValid",false);
                msg.setData(data);
            }
            handler.sendMessage(msg);
        }
    };

    private void jumpToMain(){
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}

class UrlValid {
    private static OkHttpClient client = new OkHttpClient();
    private static final String url = "https://t66y.com";
    public static boolean run() throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println(response.body());
            return true;
        }
    }
}