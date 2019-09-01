package com.senventise.clreader;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostListActivity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter<String> aa;
    SimpleAdapter adapter;
    PostList pl;
    int page = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);
        listView = findViewById(R.id.post_list_list);
        pl = new PostList(PostList.CRWX);
        String forum = getIntent().getStringExtra("forum");
        switch (forum){
            case "literature":
                createList("");
                break;
            case "technique":
                System.out.println("TECH");
                break;
        }
    }

    private void createList(String url){
        final String[] data = {"测试1","测试2","测试3","测试3","测试3","测试3","测试3","测试3","测试3","测试3","测试3","测试3","测试3","测试3","测试3","测试3","测试3","测试3","测试3"};
        //HashMap<String,String> hm = pl.getPostList(1);
        //String[] list = new String[40];
        //hm.keySet().toArray(list);
        //List arrList = new ArrayList(list);
        //aa = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, list);
        //adapter = new SimpleAdapter(this,hm,1,hm.keySet(),hm.keySet());
        //listView.setAdapter(aa);
        new Thread(networkTask).start();
        listView.setOnItemLongClickListener(new AbsListView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("list",i+"");
                return false;
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
                    View lastVisibleItemView = listView.getChildAt(listView.getChildCount() - 1);
                    if (lastVisibleItemView != null && lastVisibleItemView.getBottom() == listView.getHeight()) {
                        // list view 到底部
                        addItemToList(data,aa);
                    }
                }
            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
      @Override
      public void handleMessage(Message msg){
          super.handleMessage(msg);
          aa = new ArrayAdapter<>(PostListActivity.this,android.R.layout.simple_list_item_1, msg.getData().getStringArrayList("key"));
          listView.setAdapter(aa);
          aa.notifyDataSetChanged();
      }
    };

    Runnable networkTask = new Runnable() {
        @Override
        public void run() {
            Message msg = new Message();
            Bundle bundle = new Bundle();
            //String[] list = new String[40];
            HashMap<String,String> hm = pl.getPostList(page);
            List<String> list = new ArrayList<String>(hm.keySet());
            //hm.keySet().toArray(list);
            bundle.putStringArrayList("key",new ArrayList(list));
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    };

    private void addItemToList(String[] item,ArrayAdapter<String> aa){
        aa.addAll(item);
        aa.notifyDataSetChanged();
    }
}
