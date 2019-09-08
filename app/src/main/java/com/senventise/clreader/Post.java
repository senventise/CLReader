package com.senventise.clreader;

import androidx.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Post {
    private int page = 1;
    private String url;
    private Document document = new Document(""); //页面源码
    private String tid;
    private String title;
    public List<Floor> floors = new ArrayList<>();
    
    public Post(String url){
        this.url = url;
        String[] s = url.split("/");
        this.tid = s[s.length - 1];
        getSource(url);
    }

    // 第一页
    public List<Floor> getPostFloors(){
        for (Element e:document.getElementsByClass("t t2")) {
            String poster = e.getElementsByTag("b").get(0).text();
            String time = e.getElementsByClass("tipad").text().replace("Posted:","").split(" ")[1]+" "+
                    e.getElementsByClass("tipad").text().replace("Posted:","").split(" ")[2];
            String content = e.getElementsByClass("tpc_content").html();
            Floor floor = new Floor(poster, time, content);
            floors.add(floor);
        }
        return floors;
    }

    private void getFloors(Document d){
        floors = new ArrayList<>();
        for (Element e:d.getElementsByClass("t t2")) {
            String poster = e.getElementsByTag("b").get(0).text();
            String time = e.getElementsByClass("tipad").text().replace("Posted:","").split(" ")[1]+" "+
                    e.getElementsByClass("tipad").text().replace("Posted:","").split(" ")[2];
            String content = e.getElementsByClass("tpc_content").html();
            Floor floor = new Floor(poster, time, content);
            floors.add(floor);
        }
    }
    
    public boolean nextPage(){
        Elements elements = document.getElementsByClass("pages").first().getElementsByTag("a");
        if (!elements.get(elements.size()-2).attr("class").equals("gray")) {
            String nextUrl = elements.get(elements.size() - 2).attr("abs:href");
            getFloors(getSource(nextUrl));
            return true;
        }else{
            return false;
        }
    }
    
    private Document getSource(String url){
        try{
            document = Jsoup.connect(url).userAgent("Mozilla/5.0 (Linux; Android 9) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.111 Mobile Safari/537.36").get();
            this.title = document.title();
        }catch (IOException e){
            e.printStackTrace();
        }
        return document;
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
            return this.poster + ": " + this.content;
        }
    }
}
