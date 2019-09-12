package com.senventise.clreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 是否为夜间模式
        SharedPreferences pref = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isNightMode = pref.getBoolean("night",true);
        if (isNightMode){
            setTheme(R.style.AppThemeNight);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        recyclerView = findViewById(R.id.favorites_list_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(FavoritesActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        FavoriteItemAdapter adapter = new FavoriteItemAdapter(loadFav());
        recyclerView.setAdapter(adapter);
    }

    private List<FavItem> loadFav(){
        List<FavItem> items = new ArrayList<>();
        try {
            FileInputStream fileInputStream = openFileInput("fav.lst");
            byte[] buffer = new byte[1024];
            fileInputStream.read(buffer);
            fileInputStream.close();
            String content = new String(buffer, "UTF-8");
            String[] str = content.split("\n");
            for (int i = 0;i<str.length-1;i++){
                String title,path;
                title = str[i].split("::")[0];
                path = str[i].split("::")[1];
                FavItem item = new FavItem(title, path);
                items.add(item);
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            Toast.makeText(this,"读取文件出现错误", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return items;
    }
}


class FavoriteItemAdapter extends RecyclerView.Adapter<FavoriteItemAdapter.ViewHolder> {
    private List<FavItem> favItems;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View favItemView;
        TextView title;

        public ViewHolder(View view){
            super(view);
            favItemView = view;
            title = view.findViewById(R.id.fav_item_title);
        }
    }

    public FavoriteItemAdapter(List<FavItem> arg){
        favItems = arg;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorites_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        // 点击事件
        holder.favItemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                FavItem item = favItems.get(position);
                Intent i = new Intent(v.getContext(),PostActivity.class);
                i.putExtra("path", item.getPath());
                v.getContext().startActivity(i);
                System.out.println(item.getPath());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavItem favItem = favItems.get(position);
        holder.title.setText(favItem.getTitle());
    }

    @Override
    public int getItemCount() {
        return favItems.size();
    }

    // 追加内容
    public void add(List<FavItem> favItems){
        this.favItems.addAll(favItems);
        this.notifyDataSetChanged();
    }
}

class FavItem{
    private String title;
    private String path;

    public FavItem(String title, String path){
        this.title = title;
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }
}