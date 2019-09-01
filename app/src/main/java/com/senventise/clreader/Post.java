package com.senventise.clreader;

import androidx.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Post {
    private int page = 1;
    private String url;
    private Document document = new Document(""); //页面源码
    private String nextUrl; // 下一页URL
    private String prevUrl; // 上一页URL
    private String tid;
    private String title;
    
    public Post(String url){
        this.url = url;
        String[] s = url.split("/");
        this.tid = s[s.length - 1];
        getSource();
    }
    
    public List<Floor> getPostFloors(){
        List<Floor> floors = new ArrayList<>();
        for (Element e:document.getElementsByClass("t t2")) {
            String poster = e.getElementsByTag("b").text();
            String time = e.getElementsByClass("tipad").text().split(" ")[1];
            String content = e.getElementsByClass("tpc_content").html();
            Floor floor = new Floor(poster, time, content);
            floors.add(floor);
            //System.out.println(floor);
        }
        return floors;
    }
    
    public String netxPage(){
        StringBuilder sb = new StringBuilder("https://www.t66y.com/read.php?tid=");
        sb.append(this.tid);
        nextUrl = sb.toString();
        return nextUrl;
    }
    
    private void getSource(){
        try{
            document = Jsoup.connect(url).userAgent("Mozilla/5.0 (Linux; Android 9) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.111 Mobile Safari/537.36").get();
            this.title = document.title();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public String getTitle() {
        return title;
    }

    // 楼层
    class Floor{
        private String poster;
        private String time;
        private String content;
        public Floor(String poster, String time, String content){
            this.poster = poster;
            this.time = time;
            this.content = content;
        }

        public String getTime() {
            return time;
        }

        public String getContent() {
            return content;
        }

        public String getPoster() {
            return poster;
        }

        @NonNull
        @Override
        public String toString() {
            return this.poster + this.content;
        }
    }
}
