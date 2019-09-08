package com.senventise.clreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.senventise.clreader.Post.*;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class PostActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    String path;
    Post post;
    Boolean loading = false; // 防止疯狂加载
    Boolean hasNext = true; // 是否还有下一页
    FloorItemAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 是否为夜间模式
        SharedPreferences pref = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isNightMode = pref.getBoolean("night",true);
        if (isNightMode){
            setTheme(R.style.AppThemeNight);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        recyclerView = findViewById(R.id.post_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(PostActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        setListeners();
        path = getIntent().getStringExtra("path");
        new Thread(getPostContent).start();
    }

    Runnable getPostContent = new Runnable() {
        @Override
        public void run() {
            Message msg = new Message();
            Post post = new Post(path);
            msg.obj = post;
            postContentHandler.sendMessage(msg);
        }
    };

    @SuppressLint("HandlerLeak")
    Handler postContentHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            post = (Post) msg.obj;
            List<Floor> floors = post.getPostFloors();
            adapter = new FloorItemAdapter(floors);
            recyclerView.setAdapter(adapter);
            setTitle(post.getTitle());
        }
    };

    Runnable getNextPage = new Runnable() {
        @Override
        public void run() {
            Message msg = new Message();
            hasNext = post.nextPage();
            msg.obj = post;
            nextPageHandler.sendMessage(msg);
        }
    };

    @SuppressLint("HandlerLeak")
    Handler nextPageHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Post post = (Post) msg.obj;
            List<Floor> floors = post.floors;
            if (hasNext) {
                adapter.add(floors);
                Toast.makeText(getApplicationContext(), "已加载下一页", Toast.LENGTH_SHORT).show();
            }
            loading = false; // 加载完成
        }
    };

    // 设置监听器
    private void setListeners(){
        // 滚动到底
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)){
                    if ((!loading) && hasNext) {
                        Log.d("RecyclerView", "滑动到底部");
                        loading = true; // 正在加载
                        new Thread(getNextPage).start();
                    }else if (!hasNext){
                        Toast.makeText(getApplicationContext(),"最后一页",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

}


class FloorItemAdapter extends RecyclerView.Adapter<FloorItemAdapter.ViewHolder> {
    private List<Floor> floors;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView poster;
        TextView content;
        TextView time;

        public ViewHolder(View view){
            super(view);
            poster = view.findViewById(R.id.floor_poster);
            content = view.findViewById(R.id.floor_content);
            time = view.findViewById(R.id.floor_time);
        }
    }

    public FloorItemAdapter(List<Floor> arg){
        floors = arg;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.floor_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Floor floor = floors.get(position);
        holder.poster.setText(floor.getPoster());
        holder.content.setText(Html.fromHtml(floor.getContent(),Html.FROM_HTML_MODE_COMPACT));
        // TODO:图片支持
        holder.time.setText(floor.getTime());
    }

    @Override
    public int getItemCount() {
        return floors.size();
    }

    public void add(List<Floor> floors){
        this.floors.addAll(floors);
        this.notifyDataSetChanged();
    }
}
