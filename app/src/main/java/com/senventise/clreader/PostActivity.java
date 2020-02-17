package com.senventise.clreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.senventise.clreader.Post.*;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.NotNull;
import java.util.List;

import static com.senventise.clreader.Utils.DatabaseUtils.addToFav;
import static com.senventise.clreader.Utils.DatabaseUtils.findInFav;
import static com.senventise.clreader.Utils.DatabaseUtils.deleteExistFav;
import static com.senventise.clreader.Utils.DatabaseUtils.addToHistory;

public class PostActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ProgressBar progressBar;
    String path;
    String title;
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
            setTheme(R.style.AppThemeNightNoActionBar);
        }else {
            setTheme(R.style.AppThemeNoActionBar);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Toolbar toolbar = findViewById(R.id.post_toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.post_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(PostActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        setListeners();
        String action = getIntent().getAction();
        if (action != null && action.equals(Intent.ACTION_VIEW)){
            path = getIntent().getDataString();
            if (path != null && path.contains("htm_mob")){
                path = path.replace("htm_mob", "htm_data");
            }
        } else {
            path = getIntent().getStringExtra("path");
            title = getIntent().getStringExtra("title");
        }
        if (path != null) {
            path = path.replace("{root}", MyApplication.getRootUrl());
        }
        progressBar = findViewById(R.id.post_progress_bar);
        new Thread(getPostContent).start();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (findInFav(path.replace(MyApplication.getRootUrl(), "{root}"))) {
            menu.findItem(R.id.toolbar_fav).setTitle("取消收藏");
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.toolbar_fav:
                if (item.getTitle().equals("收藏")) {
                    addToFav(title, path.replace(MyApplication.getRootUrl(), "{root}"));
                    Toast.makeText(this, "已收藏", Toast.LENGTH_SHORT).show();
                }else{
                    deleteExistFav(path.replace(MyApplication.getRootUrl(), "{root}"));
                    Toast.makeText(this, "已取消收藏", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.toolbar_copy_title:
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("url", title));
                Toast.makeText(this,"已复制",Toast.LENGTH_SHORT).show();
                break;
            case R.id.toolbar_copy_url:
                ClipboardManager cm1 = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm1.setPrimaryClip(ClipData.newPlainText("url", path));
                Toast.makeText(this,"已复制",Toast.LENGTH_SHORT).show();
                break;
            case R.id.toolbar_browser:
                Uri uri = Uri.parse(path);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    Runnable getPostContent = new Runnable() {
        @Override
        public void run() {
            Message msg = new Message();
            msg.obj = new Post(path);
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
            if (title != null){
                setTitle(title);
            }else {
                setTitle(post.getTitle());
                title = post.getTitle();
            }
            addToHistory(post.getTitle(), path);
            progressBar.setVisibility(View.INVISIBLE);
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

    public static class PicassoImageGetter implements Html.ImageGetter {

        private TextView textView = null;

        public PicassoImageGetter() {

        }

        public PicassoImageGetter(TextView target) {
            textView = target;
        }

        @Override
        public Drawable getDrawable(String source) {
            BitmapDrawablePlaceHolder drawable = new BitmapDrawablePlaceHolder();
            Picasso.get().load(source)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(drawable);
            return drawable;
        }

        private class BitmapDrawablePlaceHolder extends BitmapDrawable implements Target {

            protected Drawable drawable;

            @Override
            public void draw(final Canvas canvas) {
                if (drawable != null) {
                    drawable.draw(canvas);
                }
            }

            public void setDrawable(Drawable drawable) {
                this.drawable = drawable;
                int width = drawable.getIntrinsicWidth();
                int height = drawable.getIntrinsicHeight();
                drawable.setBounds(0, 0, width, height);
                setBounds(0, 0, width, height);
                if (textView != null) {
                    textView.setText(textView.getText());
                }
            }

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                setDrawable(new BitmapDrawable(MyApplication.getInstance().getResources(), bitmap));
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                System.out.println("Load Failed !");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }

        }
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
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Floor floor = floors.get(position);
        holder.poster.setText(floor.getPoster());
        // TODO:GIF支持
        // TODO:点击放大显示
        PostActivity.PicassoImageGetter picassoImageGetter = new PostActivity.PicassoImageGetter(holder.content);
        holder.content.setText(Html.fromHtml(floor.getContent(),Html.FROM_HTML_MODE_COMPACT,picassoImageGetter,null));
        //System.out.println(floor.getContent());
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


