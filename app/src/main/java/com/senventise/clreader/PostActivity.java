package com.senventise.clreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.senventise.clreader.Post.*;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class PostActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        recyclerView = findViewById(R.id.post_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(PostActivity.this);
        recyclerView.setLayoutManager(layoutManager);
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
            Post post = (Post) msg.obj;
            List<Floor> floors = post.getPostFloors();
            FloorItemAdapter adapter = new FloorItemAdapter(floors);
            recyclerView.setAdapter(adapter);
            setTitle(post.getTitle());
        }
    };

}


class FloorItemAdapter extends RecyclerView.Adapter<FloorItemAdapter.ViewHolder> {
    private List<Floor> floors;

    static class ViewHolder extends RecyclerView.ViewHolder {
        //View floorView;
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
        holder.content.setText(Html.fromHtml(floor.getContent()));
        holder.time.setText(floor.getTime());
    }

    @Override
    public int getItemCount() {
        return floors.size();
    }

    /*
    // 追加内容
    public void add(List<PostItem> postItems){
        this.postItems.addAll(postItems);
        this.notifyDataSetChanged();
    }*/
}
