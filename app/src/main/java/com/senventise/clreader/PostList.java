package com.senventise.clreader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class PostList{
    static final String CRWX = "https://www.t66y.com/thread0806.php?fid=20&page=";
    static final String JSTL = "https://www.t66y.com/thread0806.php?fid=7&page=";
    static final String XSDDWM = "https://www.t66y.com/thread0806.php?fid=8&page=";
    static final String DGEDQZ = "https://www.t66y.com/thread0806.php?fid=16&page=";
    private int currentPage = 0;
    private String url;

    public PostList(String url){
        this.url = url;
    }

    public List<PostItem> getPostList(int page){
        List<PostItem> postItems = new ArrayList<>();
        currentPage = page;
        Document document = getSource(page);
        for(Element i:document.getElementsByClass("tr3 t_one tac")){
            String title = i.select(".tal").select("h3").text();
            String author = i.getElementsByClass("bl").text();
            String time = i.getElementsByClass("f12").text();
            String path = i.select(".tal").select("a").attr("abs:href");
            // 屏蔽置顶项
            if (document.getElementsByClass("tr3 t_one tac").indexOf(i) != 0){
                PostItem postItem = new PostItem(title, author, time, path);
                postItems.add(postItem);
            }
        }
        return postItems;
    }

    public List<PostItem> getNextPostList(){
        currentPage += 1;
        return getPostList(currentPage);
    }

    private Document getSource(int page){
        try {
            return Jsoup.connect(url+page).userAgent("Mozilla/5.0 (Linux; Android 9) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.111 Mobile Safari/537.36").get();
        }catch (IOException e){
            e.printStackTrace();
            return new Document("");
        }
    }

    public int getCurrentPage() {
        return currentPage;
    }
}

class PostItem{
    private String title;
    private String author;
    private String time;
    private String path;

    public PostItem(String title, String author, String time, String path){
        this.title = title;
        this.time = time;
        this.author = author;
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getTime() {
        return time;
    }

    public String getPath() {
        return path;
    }
}
