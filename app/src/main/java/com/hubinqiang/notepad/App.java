package com.hubinqiang.notepad;

import android.app.Application;

import com.hubinqiang.notepad.module.AppModule;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;


public class App extends Application{
    private ObjectGraph objectGraph;
    @Override
    public void onCreate() {
        super.onCreate();
        objectGraph = ObjectGraph.create(getModules().toArray());
        objectGraph.inject(this);
    }


    private List<Object> getModules() {
        return Arrays.<Object>asList(new AppModule(this));
    }

    // 注入依赖
    public ObjectGraph createScopedGraph(Object... modules) {
        return objectGraph.plus(modules);
    }
}
