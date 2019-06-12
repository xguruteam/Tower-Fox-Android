package com.foxridge.towerfox.model;

import android.content.Context;

import java.util.ArrayList;

public class Database {
    private Context context;
    private static Database instance;
    ArrayList<String> projectsGlobalArray = new ArrayList<>();

    private Database(){
    }

    public Database(Context context){
        this.context = context;
    }

    public void init(Context context) {
        this.context = context;
        createDB();
    }

    public static synchronized Database getInstance(){
        if(instance==null){
            instance=new Database();
        }
        return instance;
    }

    public void createDB(){

    }

}
