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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class PostListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    PostList pl;
    int page = 1;
    PostItemAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);
        recyclerView = findViewById(R.id.post_list_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(PostListActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        //adapter = new PostItemAdapter(new ArrayList<PostItem>());
        //recyclerView.setAdapter(adapter);
        String node = getIntent().getStringExtra("node");
        switch (node){
            case "CRWX":
                pl = new PostList(PostList.CRWX);
                break;
            case "JSTL":
                pl = new PostList(PostList.JSTL);
                break;
            case "DGEQZ":
                pl = new PostList(PostList.DGEDQZ);
                break;
            case "XSDWM":
                pl = new PostList(PostList.XSDDWM);
                break;
        }
        Toast.makeText(getApplicationContext(),"加载中",Toast.LENGTH_SHORT).show();
        new Thread(networkTask).start();
        setListeners();
    }

    public void mstartActivity(Intent i){
        startActivity(i);
    }

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
                    Log.d("RecyclerView","滑动到底部");
                    new Thread(loadNextPage).start();
                }
            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler nextPageHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter.add((List<PostItem>)msg.obj);
            Toast.makeText(getApplicationContext(),"已加载下一页",Toast.LENGTH_SHORT).show();
        }
    };

    Runnable loadNextPage = new Runnable() {
        @Override
        public void run() {
            Message msg = new Message();
            msg.obj = pl.getNextPostList();
            nextPageHandler.sendMessage(msg);
        }
    };

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
      @Override
      public void handleMessage(Message msg){
          super.handleMessage(msg);
          adapter = new PostItemAdapter((List<PostItem>)msg.obj);
          recyclerView.setAdapter(adapter);
      }
    };

    Runnable networkTask = new Runnable() {
        @Override
        public void run() {
            Message msg = new Message();
            msg.obj = pl.getPostList(page);
            handler.sendMessage(msg);
        }
    };
}

class PostItemAdapter extends RecyclerView.Adapter<PostItemAdapter.ViewHolder> {
    private List<PostItem> postItems;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View postItemView;
        TextView title;
        TextView author;
        TextView time;

        public ViewHolder(View view){
            super(view);
            postItemView = view;
            title = view.findViewById(R.id.post_item_title);
            author = view.findViewById(R.id.post_item_author);
            time = view.findViewById(R.id.post_item_time);
        }
    }

    public PostItemAdapter(List<PostItem> arg){
        postItems = arg;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        // 点击事件
        holder.postItemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                System.out.println(position);
                PostItem item = postItems.get(position);
                Intent i = new Intent(v.getContext(),PostActivity.class);
                i.putExtra("path", item.getPath());
                v.getContext().startActivity(i);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PostItem postItem = postItems.get(position);
        holder.title.setText(postItem.getTitle());
        holder.author.setText(postItem.getAuthor());
        holder.time.setText(postItem.getTime());
    }

    @Override
    public int getItemCount() {
        return postItems.size();
    }

    // 追加内容
    public void add(List<PostItem> postItems){
        this.postItems.addAll(postItems);
        this.notifyDataSetChanged();
    }
}
