package com.senventise.clreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = getSharedPreferences("settings", MODE_PRIVATE);
        boolean firstOpen = pref.getBoolean("firstOpen",true);
        if (firstOpen){
            // 第一次打开
            SharedPreferences.Editor editor = getSharedPreferences("settings", MODE_PRIVATE).edit();
            editor.putBoolean("firstOpen", false);
            editor.putBoolean("night", false);
            editor.apply();
        }
    }

    // 技术讨论区
    public void onJSTLClick(View view) {
        Intent i = new Intent(this,PostListActivity.class);
        i.putExtra("node","JSTL");
        startActivity(i);
    }

    // 文学交流区
    public void onCRWXClick(View view) {
        Intent i = new Intent(this,PostListActivity.class);
        i.putExtra("node","CRWX");
        startActivity(i);
    }

    public void onDGEQZClick(View view) {
        Intent i = new Intent(this,PostListActivity.class);
        i.putExtra("node","DGEQZ");
        startActivity(i);
    }

    public void onXDSWMClick(View view) {
        Intent i = new Intent(this,PostListActivity.class);
        i.putExtra("node","XSDWM");
        startActivity(i);
    }

    long lastTime = 0;
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        //只处理KeyDown
        if (event.getAction() == KeyEvent.ACTION_UP){
            return true;
        }
        if (event.getKeyCode() == 4){
            if(lastTime==0L){
                //第一次按返回键
                lastTime = System.currentTimeMillis();
                Toast.makeText(getApplicationContext(),"再按一次退出",Toast.LENGTH_SHORT).show();
            }else{
                //已经按过一次返回键
                long thisTime = System.currentTimeMillis();
                if(thisTime - lastTime <= 800){
                    //退出
                    System.exit(0);
                }else{
                    //间隔太久
                    lastTime = thisTime;
                    Toast.makeText(getApplicationContext(),"再按一次退出",Toast.LENGTH_SHORT).show();
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public void onNightClick(View view) {
        SharedPreferences.Editor editor = getSharedPreferences("settings", MODE_PRIVATE).edit();
        if (pref.getBoolean("night", false)) {
            editor.putBoolean("night", false);
        }else {
            editor.putBoolean("night", true);
        }
    }
}
