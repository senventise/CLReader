package com.senventise.clreader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashMap;

public class CLHelper {

}

class PostList{
    static final String CRWX = "https://www.t66y.com/thread0806.php?fid=20&page=";
    static final String JSTL = "https://www.t66y.com/thread0806.php?fid=7&page=";
    private int currentPage = 0;
    private String url;

    public PostList(String url){
        this.url = url;
    }

    public HashMap<String,String> getPostList(int page){
        currentPage = page;
        Document document = getSource(page);
        HashMap<String,String> hm = new HashMap<>();
        for(Element i:document.getElementsByClass("tr3 t_one tac")){
            String title = i.select(".tal").select("h3").text();
            String path = i.select(".tal").select("a").attr("abs:href");
            hm.put(title,path);
            //System.out.println(title+": "+path);
        }
        return hm;
    }

    public HashMap<String,String> getNextPostList(){
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
