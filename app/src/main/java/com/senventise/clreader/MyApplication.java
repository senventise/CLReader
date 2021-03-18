package com.senventise.clreader;

import android.app.Application;


public class MyApplication extends Application {
    private static MyApplication instance;
    private static String rootUrl;

    public static MyApplication getInstance(){
        return instance;
    }

    public static void setRootUrl(String rootUrl) {
        MyApplication.rootUrl = rootUrl;
    }

    public static String getRootUrl() {
        if (rootUrl == null){
            System.exit(0);
        }
        return rootUrl;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
