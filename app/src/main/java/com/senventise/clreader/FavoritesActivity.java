package com.senventise.clreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    static String currentPath;
    static String currentTitle;
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
        setTitle("收藏夹");
        recyclerView = findViewById(R.id.favorites_list_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(FavoritesActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        FavoriteItemAdapter adapter = new FavoriteItemAdapter(loadFav());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        FavoriteItemAdapter adapter = new FavoriteItemAdapter(loadFav());
        recyclerView.setAdapter(adapter);
    }

    private List<FavItem> loadFav(){
        List<FavItem> items = new ArrayList<>();
        MySqlHelper mySqlHelper = new MySqlHelper(this, "data.db", null, 1);
        SQLiteDatabase db = mySqlHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from fav", null);
        if (cursor.moveToFirst()){
            do {
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String path = cursor.getString(cursor.getColumnIndex("path"));
                items.add(new FavItem(title, path));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        Collections.reverse(items);
        return items;
    }

    public boolean onFavCopyPathClick(MenuItem menuItem){
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("url", currentPath));
        Toast.makeText(this,"已复制",Toast.LENGTH_SHORT).show();
        return false;
    }

    public boolean onFavDeleteClick(final MenuItem menuItem){
        AlertDialog.Builder dialog = new AlertDialog.Builder(FavoritesActivity.this);
        dialog.setTitle("确认删除");
        dialog.setMessage("是否要删除 \"" + currentTitle + "\"?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MySqlHelper mySqlHelper = new MySqlHelper(MyApplication.getInstance(), "data.db", null, 1);
                SQLiteDatabase database = mySqlHelper.getWritableDatabase();
                database.delete("fav", "path=?", new String[]{currentPath});
                Toast.makeText(MyApplication.getInstance(),"已删除",Toast.LENGTH_SHORT).show();
                FavoriteItemAdapter adapter = new FavoriteItemAdapter(loadFav());
                recyclerView.setAdapter(adapter);
                database.close();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
        return false;
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
            }
        });
        // 长按
        holder.favItemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View view) {
                int position = holder.getAdapterPosition();
                FavItem item = favItems.get(position);
                PopupMenu popupMenu = new PopupMenu(view.getContext(),view);
                MenuInflater menuInflater = popupMenu.getMenuInflater();
                menuInflater.inflate(R.menu.fav_popup_menu,popupMenu.getMenu());
                FavoritesActivity.currentPath = item.getPath();
                FavoritesActivity.currentTitle = item.getTitle();
                popupMenu.show();
                return true;
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