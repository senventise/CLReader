package com.senventise.clreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    HistoryItemAdapter adapter;
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
        setContentView(R.layout.activity_history);
        recyclerView = findViewById(R.id.history_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new HistoryItemAdapter(loadHistory());
        recyclerView.setAdapter(adapter);
    }

    public void reload(){
        adapter.update(loadHistory());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // 焦点改变时更新数据
        this.reload();
    }

    public List<HistoryItem> loadHistory(){
        List<HistoryItem> items = new ArrayList<>();
        MySqlHelper mySqlHelper = new MySqlHelper(this, "data.db", null, 1);
        SQLiteDatabase db = mySqlHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from history", null);
        if (cursor.moveToFirst()){
            do {
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String path = cursor.getString(cursor.getColumnIndex("path"));
                items.add(new HistoryItem(title, path));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // 倒序
        Collections.reverse(items);
        return items;
    }
}

class HistoryItemAdapter extends RecyclerView.Adapter<HistoryItemAdapter.ViewHolder> {
    private List<HistoryItem> historyItems;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View HistoryItemView;
        TextView title;

        public ViewHolder(View view){
            super(view);
            HistoryItemView = view;
            title = view.findViewById(R.id.history_item_title);
        }
    }

    public HistoryItemAdapter(List<HistoryItem> arg){
        historyItems = arg;
    }

    @NotNull
    @Override
    public HistoryItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        final HistoryItemAdapter.ViewHolder holder = new HistoryItemAdapter.ViewHolder(view);
        // 点击事件
        holder.HistoryItemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                HistoryItem item = historyItems.get(position);
                Intent i = new Intent(v.getContext(),PostActivity.class);
                i.putExtra("path", item.getPath());
                v.getContext().startActivity(i);

            }
        });
        // 长按
        holder.HistoryItemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View view) {
                int position = holder.getAdapterPosition();
                final HistoryItem item = historyItems.get(position);
                AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                dialog.setTitle("确认删除");
                dialog.setMessage("是否要删除 \"" + item.getTitle() + "\"?");
                dialog.setCancelable(false);
                dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MySqlHelper mySqlHelper = new MySqlHelper(MyApplication.getInstance(), "data.db", null, 1);
                        SQLiteDatabase database = mySqlHelper.getWritableDatabase();
                        database.delete("history", "path=?", new String[]{item.getPath()});
                        Toast.makeText(MyApplication.getInstance(),"已删除",Toast.LENGTH_SHORT).show();
                        database.close();
                    }
                });
                dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.show();
                return true;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryItemAdapter.ViewHolder holder, int position) {
        HistoryItem favItem = historyItems.get(position);
        holder.title.setText(favItem.getTitle());
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }

    public void update(List<HistoryItem> historyItems){
        this.historyItems = historyItems;
        this.notifyDataSetChanged();
    }

}

class HistoryItem extends FavItem{
    public HistoryItem(String title, String path) {
        super(title, path);
    }
}