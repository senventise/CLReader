package com.senventise.clreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import org.jetbrains.annotations.NotNull;
import java.util.List;

import static com.senventise.clreader.Utils.DatabaseUtils.addToFav;
import static com.senventise.clreader.Utils.DatabaseUtils.findInFav;
import static com.senventise.clreader.Utils.DatabaseUtils.findInHistory;


public class PostListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ConstraintLayout constraintLayout;
    PostList pl;
    int page = 1;
    PostItemAdapter adapter;
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
        setContentView(R.layout.activity_post_list);
        recyclerView = findViewById(R.id.post_list_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(PostListActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        String node = getIntent().getStringExtra("node");
        switch (node){
            case "CRWX":
                setTitle("成人文学交流区");
                pl = new PostList(PostList.CRWX);
                break;
            case "JSTL":
                setTitle("技术讨论区");
                pl = new PostList(PostList.JSTL);
                break;
            case "DGEQZ":
                setTitle("达盖尔的旗帜");
                pl = new PostList(PostList.DGEDQZ);
                break;
            case "XSDWM":
                setTitle("新时代的我们");
                pl = new PostList(PostList.XSDDWM);
                break;
        }
        Toast.makeText(getApplicationContext(),"加载中",Toast.LENGTH_SHORT).show();
        new Thread(networkTask).start();
        setListeners();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public boolean onMenuUrlCopyClick(MenuItem item){
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("url", currentPath));
        Toast.makeText(this,"已复制",Toast.LENGTH_SHORT).show();
        return false;
    }

    public boolean onMenuBrowserClick(MenuItem item){
        Uri uri = Uri.parse(currentPath);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
        return false;
    }

    public boolean onMenuAddToFavoriteClick(MenuItem item){
        addToFav(currentTitle, currentPath);
        return false;
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

    static class ViewHolder extends RecyclerView.ViewHolder{
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
                PostItem item = postItems.get(position);
                Intent i = new Intent(v.getContext(),PostActivity.class);
                i.putExtra("path", item.getPath());
                i.putExtra("title", item.getTitle());
                v.getContext().startActivity(i);
            }
        });
        // 长按
        holder.postItemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View view) {
                int position = holder.getAdapterPosition();
                PostItem item = postItems.get(position);
                PopupMenu popupMenu = new PopupMenu(view.getContext(),view);
                MenuInflater menuInflater = popupMenu.getMenuInflater();
                menuInflater.inflate(R.menu.popup_menu,popupMenu.getMenu());
                PostListActivity.currentPath = item.getPath();
                PostListActivity.currentTitle = item.getTitle();
                popupMenu.show();
                return true;
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
        PostItem item = postItems.get(position);
        String path = item.getPath();
        if (findInHistory(path.replace(MyApplication.getRootUrl(), "{root}"))) {
            holder.title.setTextColor(Color.LTGRAY);
            holder.author.setTextColor(Color.LTGRAY);
            holder.time.setTextColor(Color.LTGRAY);
        }
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
