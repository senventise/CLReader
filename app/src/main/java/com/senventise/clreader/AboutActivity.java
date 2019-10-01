package com.senventise.clreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.SharedPreferences;
import android.widget.ImageView;
import android.widget.TextView;

import com.drakeet.about.AbsAboutActivity;
import com.drakeet.about.Card;
import com.drakeet.about.Category;
import com.drakeet.about.Contributor;
import com.drakeet.about.License;

import java.util.List;

public class AboutActivity extends AbsAboutActivity {

    @Override
    protected void onCreateHeader(@NonNull ImageView icon, @NonNull TextView slogan, @NonNull TextView version) {
        SharedPreferences pref = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isNightMode = pref.getBoolean("night",true);
        if (isNightMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        icon.setImageResource(R.mipmap.ic_launcher_round);
        slogan.setText("关于 " + getString(R.string.app_name));
        version.setText(BuildConfig.VERSION_NAME);
    }

    @Override
    protected void onItemsCreated(@NonNull List<Object> items) {
        items.add(new Category("介绍"));
        items.add(new Card("一款开源草榴社区第三方客户端\n" +
                "Github:https://www.github.com/senventise/clreader"));

        items.add(new Category("开发者"));
        items.add(new Contributor(R.drawable.author_avater,"Senventise",
                "萌新开发者","https://www.senventise.com"));

        items.add(new Category("免责声明"));
        items.add(new Card("本软件仅为草榴社区的第三方客户端，" +
                "与草榴社区无任何关系，" +
                "开发者不对其内容负任何法律责任。"));

        items.add(new Category("Licenses"));
        items.add(new License("about-page","Drakeet",License.APACHE_2,
                "https://github.com/drakeet/about-page"));
        items.add(new License("MultiType","Drakeet",License.APACHE_2,
                "https://github.com/drakeet/MultiType"));
        items.add(new License("androidx","Google",License.APACHE_2,
                "https://source.android.com/"));
        items.add(new License("okhttp","square",License.APACHE_2,
                "https://github.com/square/okhttp/"));
        items.add(new License("picasso","square",License.APACHE_2,
                "https://github.com/square/picasso/"));
        items.add(new License("jsoup","Jonathan Hedley",License.MIT,
                "https://jsoup.org/"));
        items.add(new License("material","Google",License.APACHE_2,
                "https://source.android.com/"));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
